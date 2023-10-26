package ir.fum.oop.pattern.description;

import com.google.common.collect.Lists;

public class ObserverPatternDescription extends PatternDescription{

    @Override
    public void init() {
        featureContainer = new FeatureContainer();

        featureContainer.setRoles(Lists.newArrayList("Subject", "Observer"));

        featureContainer.setAssociationMatrix(new double[][]
                {
                        {0, 1.0},
                        {1.0, 0}
                }
        );

        featureContainer.setAbstractMatrix(new double[][]
                {
                        {0, 0},
                        {0, 1.0}
                }
        );




        featureContainer.setNumberOfHierarchies(2);

        featureContainer.setWeight(new double[]{1, 1});
    }


}
