package ir.fum.oop.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DependencyType {
    ASSOCIATION(2*3*7),
    DELEGATION(5);

    private final int value;
}
