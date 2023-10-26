package ir.fum.oop.pattern.description;

public class PatternDescriptionFactory {

    public static PatternDescription getPatternDescription(OopPattern oopPattern) {
        PatternDescription patternDescription = null;

        switch (oopPattern) {
            case STATE_STRATEGY:
                patternDescription = new StateStrategyPatternDescription();
                break;

            case ADAPTER:
                patternDescription = new AdapterPatternDescription();
                break;

            case VISITOR:
                patternDescription = new VisitorPatternDescription();
                break;

            case OBSERVER:
                patternDescription = new ObserverPatternDescription();
                break;


            case COMPOSITE:
                patternDescription = new CompositePatternDescription();
                break;

            case DECORATOR:
                patternDescription = new DecoratorPatternDescription();
                break;

            case SINGLETON:
                patternDescription = new SingletonPatternDescription();
                break;

            case FACTORY_METHOD:
                patternDescription = new FactoryMethodPatternDescription();

        }


        return patternDescription;
    }

}
