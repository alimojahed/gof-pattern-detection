package ir.fum.oop.parser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import ir.fum.oop.model.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class JavaClassParser {
    private final ClassOrInterfaceDeclaration classOrInterfaceDeclaration;
    private final String packageName;


    public ClassInfo parse() {
        ClassInfo classInfo = new ClassInfo();

        classInfo.setPackageName(packageName);
        classInfo.setClassName(classOrInterfaceDeclaration.getNameAsString());
        classInfo.setClassType(getClassType());
        classInfo.setClassVisibility(getClassVisibility());
        classInfo.setClassIsAbstract(classOrInterfaceDeclaration.isAbstract());
        classInfo.setClassIsStatic(classOrInterfaceDeclaration.isStatic());
        classInfo.setClassIsFinal(classOrInterfaceDeclaration.isFinal());
        classInfo.setClassIsInterface(classOrInterfaceDeclaration.isInterface());
        classInfo.setExtendedClass(classOrInterfaceDeclaration.getExtendedTypes().stream()
                .findFirst()
                .map(NodeWithSimpleName::getNameAsString)
                .orElse("")
        );

        classInfo.setImplementedInterfaces(classOrInterfaceDeclaration.getImplementedTypes().stream()
                .map(NodeWithSimpleName::getNameAsString)
                .collect(Collectors.toList())
        );

        classInfo.setConstructors(getConstructors());

        classInfo.setFields(getFields());
        classInfo.setMethods(getMethods());
        classInfo.setClassOrInterfaceDeclaration(classOrInterfaceDeclaration);

        return classInfo;
    }

    private List<ClassMethodInfo> getMethods() {
        return classOrInterfaceDeclaration.getMethods()
                .stream()
                .map(JavaMethodParser::new)
                .map(JavaMethodParser::parse)
                .collect(Collectors.toList());
    }

    private List<FieldOrParameter> getFields() {
        List<FieldOrParameter> fields = new ArrayList<>();

        classOrInterfaceDeclaration.getFields()
                .forEach(fieldDeclaration -> {
                    boolean isStatic = fieldDeclaration.isStatic();

                    fieldDeclaration.getVariables()
                            .forEach(variableDeclarator -> fields.add(
                                    new FieldOrParameter(
                                            variableDeclarator.getTypeAsString(),
                                            variableDeclarator.getNameAsString(),
                                            isStatic
                                    ))
                            );

                });


        return fields;
    }

    private List<ClassConstructorInfo> getConstructors() {
        return classOrInterfaceDeclaration.getConstructors()
                .stream()
                .map(JavaConstructorParser::new)
                .map(JavaConstructorParser::parse)
                .collect(Collectors.toList());
    }

    private ClassType getClassType() {
        if (classOrInterfaceDeclaration.isNestedType() || classOrInterfaceDeclaration.isInnerClass()) {
            return ClassType.NESTED_CLASS;
        }

        return classOrInterfaceDeclaration.isInterface() ? ClassType.INTERFACE : ClassType.CLASS;
    }


    public ClassVisibility getClassVisibility() {
        ClassVisibility visibility;

        if (classOrInterfaceDeclaration.isPublic()) {
            visibility = ClassVisibility.PUBLIC;

        } else if (classOrInterfaceDeclaration.isPrivate()) {
            visibility = ClassVisibility.PRIVATE;

        } else {
            visibility = ClassVisibility.PROTECTED;
        }

        return visibility;
    }

}
