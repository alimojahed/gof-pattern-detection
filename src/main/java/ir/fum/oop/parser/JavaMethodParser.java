package ir.fum.oop.parser;

import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.RequiredArgsConstructor;
import ir.fum.oop.model.ClassMethodInfo;
import ir.fum.oop.model.FieldOrParameter;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class JavaMethodParser {
    private final MethodDeclaration methodDeclaration;

    public ClassMethodInfo parse() {
        ClassMethodInfo methodInfo = new ClassMethodInfo();

        methodInfo.setAbstract(methodDeclaration.isAbstract());
        methodInfo.setStatic(methodDeclaration.isStatic());
        methodInfo.setFinal(methodDeclaration.isFinal());

        methodInfo.setName(methodDeclaration.getNameAsString());
        methodInfo.setReturnType(methodDeclaration.getTypeAsString());
        methodInfo.setParameters(getParameters());
        methodInfo.setOverride(methodDeclaration.getAnnotationByClass(Override.class).isPresent());

        return methodInfo;
    }

    private List<FieldOrParameter> getParameters() {
        return methodDeclaration.getParameters()
                .stream()
                .map(parameter -> new FieldOrParameter(parameter.getTypeAsString(), parameter.getNameAsString(), false))
                .collect(Collectors.toList());
    }



}
