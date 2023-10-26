package ir.fum.oop.pattern.description;



public abstract class PatternDescription {
    protected FeatureContainer featureContainer;

    public PatternDescription() {
        init();
    }

    public abstract void init();

    public FeatureContainer getFeatureContainer() {
        return featureContainer;
    }
}
