package ir.fum.oop.pattern.description;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeatureContainer {
    private List<String> roles;
    private double[][] generalizationMatrix;
    private double[][] associationMatrix;
    private double[][] abstractMatrix;
    private double[][] methodInvocationMatrix;
    private double[][] singletonMatrix;
    private double[][] factoryMethodMatrix;
    private double[][] overrideMethods;
    private double[][] similarMethodSignature; //for visitor
    private double[][] abstractMethodMatrix;
    private double[] weight;
    private int numberOfHierarchies;


}
