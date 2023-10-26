package ir.fum.oop.model;


import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ClassInfo {
    private String className;
    private String packageName;
    private ClassType classType;
    private ClassVisibility classVisibility;
    private boolean classIsAbstract;
    private boolean classIsStatic;
    private boolean classIsFinal;
    private boolean classIsInterface;
    private String extendedClass;
    private List<String> implementedInterfaces = new ArrayList<>();
    private List<String> children = new ArrayList<>();
    private List<ClassConstructorInfo> constructors = new ArrayList<>();
    private List<FieldOrParameter> fields = new ArrayList<>();
    private List<ClassMethodInfo> methods = new ArrayList<>();
    private List<ClassMethodInfo> overrideMethods = new ArrayList<>();
    private List<ClassMethodInfo> staticMethods = new ArrayList<>();
    private List<ClassMethodInfo> finalMethods = new ArrayList<>();
    private List<ClassMethodInfo> abstractMethods = new ArrayList<>();
    private Set<String> association = new HashSet<>();
    private Set<String> aggregation = new HashSet<>();
    private Set<String> delegation = new HashSet<>();
    private Set<String> composition = new HashSet<>();
    private Set<String> instantiation = new HashSet<>();
    private ClassOrInterfaceDeclaration classOrInterfaceDeclaration;
    private List<String> hierarchy = new ArrayList<>();
    private boolean hierarchyTest = false;
    private List<String> patterns = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    private List<RestfulServiceVO> restfulServiceVOS = new ArrayList<>();
    public void setMethods(List<ClassMethodInfo> methods) {
        this.methods = methods;

        for (ClassMethodInfo method : methods) {
            if (method.isAbstract()) {
                abstractMethods.add(method);
            }

            if (method.isFinal()) {
                finalMethods.add(method);
            }

            if (method.isStatic()) {
                staticMethods.add(method);
            }

        }

    }

    public String getFieldsInfo() {
        return fields.stream().map(FieldOrParameter::toString)
                .collect(Collectors.joining(", "));
    }


    public List<String> getSuperClassesOrInterfaces(){
        List<String> superClasses = new ArrayList<>(implementedInterfaces);
        superClasses.add(extendedClass);

        return superClasses;
    }

}
