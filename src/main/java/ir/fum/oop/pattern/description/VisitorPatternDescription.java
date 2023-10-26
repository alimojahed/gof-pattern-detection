package ir.fum.oop.pattern.description;

import com.google.common.collect.Lists;

public class VisitorPatternDescription extends PatternDescription{

    @Override
    public void init() {
        featureContainer = new FeatureContainer();

        featureContainer.setRoles(Lists.newArrayList("Visitor", "ConcreteElement"));

        featureContainer.setAbstractMatrix(new double[][]
                {
                        {1.0, 0},
                        {0, 0}
                }
        );

        featureContainer.setSimilarMethodSignature(new double[][]
                {
                        {1.0, 0},
                        {0, 0}
                }
        );


        featureContainer.setNumberOfHierarchies(2);

        featureContainer.setWeight(new double[]{1, 1});
    }

}
