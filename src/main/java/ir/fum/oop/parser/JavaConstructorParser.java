package ir.fum.oop.parser;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import lombok.RequiredArgsConstructor;
import ir.fum.oop.model.ClassConstructorInfo;
import ir.fum.oop.model.ClassVisibility;
import ir.fum.oop.model.FieldOrParameter;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JavaConstructorParser {
    private final ConstructorDeclaration constructorDeclaration;

    public ClassConstructorInfo parse() {
        ClassConstructorInfo classConstructorInfo = new ClassConstructorInfo();

        classConstructorInfo.setVisibility(getVisibility());
        classConstructorInfo.setParameters(getParameters());

        return classConstructorInfo;
    }

    private List<FieldOrParameter> getParameters() {
        return constructorDeclaration.getParameters()
                .stream()
                .map(parameter -> new FieldOrParameter(parameter.getTypeAsString(), parameter.getNameAsString(), false))
                .collect(Collectors.toList());
    }

    private ClassVisibility getVisibility() {
        if (constructorDeclaration.isPublic()) {
            return ClassVisibility.PUBLIC;
        }

        if (constructorDeclaration.isPrivate()) {
            return ClassVisibility.PRIVATE;
        }

        return ClassVisibility.PROTECTED;
    }

}
