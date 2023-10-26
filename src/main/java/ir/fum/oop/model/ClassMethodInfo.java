package ir.fum.oop.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class ClassMethodInfo {
    private String name;
    private String returnType;
    private boolean isAbstract;
    private boolean isStatic;
    private boolean isFinal;
    private List<FieldOrParameter> parameters = new ArrayList<>();
    private boolean override;
    private String parent = "";


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassMethodInfo) {
            if (!((ClassMethodInfo) obj).name.equals(name)) {
                return false;
            }

            if (!((ClassMethodInfo) obj).getReturnType().equals(returnType)) {
                return false;
            }

            if (((ClassMethodInfo) obj).getParameters().size() != parameters.size()) {
                return false;
            }

            for (int i = 0; i < parameters.size(); i++) {
                if (!((ClassMethodInfo) obj).getParameters().get(i).getType().equals(parameters.get(i).getType())) {
                    return false;
                }
            }

            return true;

        }

        return false;
    }


    @Override
    public String toString() {
        return "{" +
                "n='" + name + '\'' +
                ", r='" + returnType + '\'' +
                ", p=" + parameters +
                ", o=" + (override ? 1 : 0) +
                ", f='" + parent + '\'' +
                '}';
    }

    public String simple() {
        return name + " " + parameters.stream().map(FieldOrParameter::getType).collect(Collectors.joining(", "));
    }


    public boolean similar(ClassMethodInfo candidateMethod) {
        return !this.equals(candidateMethod) &&
                this.getName().equals(candidateMethod.getName()) &&
                this.getParameters().size() == candidateMethod.getParameters().size() &&
                this.getReturnType().equals(candidateMethod.getReturnType());
    }

}
