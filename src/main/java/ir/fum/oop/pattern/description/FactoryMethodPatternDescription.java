package ir.fum.oop.pattern.description;

import com.google.common.collect.Lists;

public class FactoryMethodPatternDescription extends PatternDescription{
    @Override
    public void init() {
        featureContainer = new FeatureContainer();

        featureContainer.setRoles(Lists.newArrayList("Factory Method"));


        featureContainer.setNumberOfHierarchies(0);

        featureContainer.setWeight(new double[]{1});
    }
}
