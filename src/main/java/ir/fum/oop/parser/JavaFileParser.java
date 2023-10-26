package ir.fum.oop.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import lombok.RequiredArgsConstructor;
import ir.fum.oop.model.ClassInfo;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class JavaFileParser {
    private final CompilationUnit compilationUnit;


    public List<ClassInfo> parse() {


        String packageName = compilationUnit.getPackageDeclaration()
                        .map(NodeWithName::getNameAsString)
                        .orElse("");


        List<ClassOrInterfaceDeclaration> foundedClasses = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);




        return foundedClasses.stream()
                .map(classOrInterfaceDeclaration -> new JavaClassParser(classOrInterfaceDeclaration, packageName))
                .map(JavaClassParser::parse)
                .collect(Collectors.toList());

    }


    private void getPackageName() {

    }


}
