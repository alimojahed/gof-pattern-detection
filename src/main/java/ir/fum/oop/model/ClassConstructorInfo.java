package ir.fum.oop.model;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ClassConstructorInfo {
    private ClassVisibility visibility;
    private List<FieldOrParameter> parameters = new ArrayList<>();

    @Override
    public String toString() {
        return visibility.name() +
                " (" +
                parameters.stream().map(FieldOrParameter::getType).collect(Collectors.joining(", ")) +
                " )";
    }
}
