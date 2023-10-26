package ir.fum.oop;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import ir.fum.oop.model.ClassInfo;
import ir.fum.oop.model.ClassMethodInfo;
import ir.fum.oop.parser.JavaFileParser;
import ir.fum.oop.parser.visitor.AssociationVisitor;
import ir.fum.oop.parser.visitor.DelegationVisitor;
import ir.fum.oop.parser.visitor.ObjectCreationVisitor;
import ir.fum.oop.pattern.description.SystemDescription;
import ir.fum.oop.pattern.description.SystemDescriptionGenerator;
import ir.fum.oop.pattern.detection.DetectedPattern;
import ir.fum.oop.pattern.detection.PatternDetector;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectParser {
    public static void parse(String projectName, String projectSourceFolder) throws IOException {
        ParserConfiguration configuration = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8);

        JavaParser parser = new JavaParser(configuration);
        System.out.println("Pasing " + projectName);
        Path source = Paths.get(projectSourceFolder);
        List<ClassInfo> classInfos = Files.walk(source)
                .filter(path -> path.toString().endsWith(".java"))
                .flatMap(path -> {
                    File file = path.toFile();
                    try {
                        CompilationUnit cu = parser.parse(file).getResult().orElse(null);

                        return new JavaFileParser(cu).parse().stream();

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .peek(classInfo -> {
                    new AssociationVisitor(classInfo).visit(classInfo.getClassOrInterfaceDeclaration(), null);
                    new DelegationVisitor(classInfo).visit(classInfo.getClassOrInterfaceDeclaration(), null);
                    new ObjectCreationVisitor(classInfo).visit(classInfo.getClassOrInterfaceDeclaration(), null);
                })
                .collect(Collectors.toList());

        Map<String, ClassInfo> classInfoMap = new HashMap<>();

        for (ClassInfo classInfo : classInfos) {

            for (ClassInfo possibleChild : classInfos) {
                if (possibleChild.getExtendedClass().equals(classInfo.getClassName())) {
                    classInfo.getChildren().add(possibleChild.getClassName());
                }
            }

            classInfoMap.put(classInfo.getClassName(), classInfo);

        }


        for (ClassInfo classInfo : classInfos) {
            if (!classInfo.isHierarchyTest()) {
                classInfo.setHierarchy(getParent(classInfo, classInfoMap));
            }

            for (ClassMethodInfo classMethodInfo : classInfo.getMethods()) {
                boolean done = false;
                for (String parentName : classInfo.getHierarchy()) {
                    if (classInfoMap.containsKey(parentName)) {
                        ClassInfo parent = classInfoMap.get(parentName);

                        for (ClassMethodInfo parentMethod : parent.getMethods()) {
                            if (classMethodInfo.equals(parentMethod)) {
                                classMethodInfo.setOverride(true);
                                classMethodInfo.setParent(parentName);
                                classInfo.getOverrideMethods().add(classMethodInfo);
                                done = true;
                                break;
                            }
                        }

                        if (done) {
                            break;
                        }

                    }
                }

                if (!done && classMethodInfo.isOverride()) {
                    classInfo.getOverrideMethods().add(classMethodInfo);
                }

            }

        }

        SystemDescription systemDescription = new SystemDescription(classInfoMap);

        SystemDescriptionGenerator systemDescriptionGenerator = new SystemDescriptionGenerator(systemDescription);

        Map<String, List<DetectedPattern>> detections = PatternDetector.detect(systemDescriptionGenerator);


        XSSFWorkbook patternWorkbook = new XSSFWorkbook();
        XSSFSheet patternSheet = patternWorkbook.createSheet();
        String patternFilename = projectName + "-patterns.xlsx";

        String[] patternHeaders = new String[]{"pattern-name", "class1", "role1", "class2", "role2", "score"};

        int rowCount = 0;

        int colCount = 0;
        Row headerRow = patternSheet.createRow(rowCount++);

        for (String head : patternHeaders) {
            Cell cell = headerRow.createCell(colCount++);
            cell.setCellValue(head);
        }

        for (String patternName : detections.keySet()) {
            for (DetectedPattern detectedPattern : detections.get(patternName)) {
                Row row = patternSheet.createRow(rowCount++);
                setPatternRow(row, patternName, detectedPattern);
            }
        }


        try (FileOutputStream outputStream = new FileOutputStream(patternFilename)) {
            patternWorkbook.write(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void setPatternRow(Row row, String patternName, DetectedPattern detectedPattern) {
        int colCount = 0;
        row.createCell(colCount++).setCellValue(patternName);
        row.createCell(colCount++).setCellValue(detectedPattern.getNameAndRole(0).getKey());
        row.createCell(colCount++).setCellValue(detectedPattern.getNameAndRole(0).getValue());
        row.createCell(colCount++).setCellValue(detectedPattern.getNameAndRole(1).getKey());
        row.createCell(colCount++).setCellValue(detectedPattern.getNameAndRole(1).getValue());
        row.createCell(colCount++).setCellValue(detectedPattern.getScore());
    }

    private static List<String> getParent(ClassInfo classInfo, Map<String, ClassInfo> classInfoMap) {
        if (classInfo == null || classInfo.getExtendedClass().isEmpty()) {
            return Collections.emptyList();
        }

        if (classInfo.isHierarchyTest()) {
            return classInfo.getHierarchy();
        }

        if (classInfo.getHierarchy().size() > 10) { //due to memory limit
            return classInfo.getHierarchy();
        }

        classInfo.getHierarchy().add(classInfo.getExtendedClass());
        classInfo.getHierarchy().addAll(getParent(classInfoMap.getOrDefault(classInfo.getExtendedClass(), null), classInfoMap));
        classInfo.setHierarchyTest(true);
        classInfoMap.put(classInfo.getClassName(), classInfo);

        return classInfo.getHierarchy();

    }



}
