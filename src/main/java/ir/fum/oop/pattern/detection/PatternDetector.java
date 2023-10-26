package ir.fum.oop.pattern.detection;

import ir.fum.oop.pattern.description.*;
import ir.fum.oop.pattern.detection.inheritance.Enumeratable;

import java.util.*;

public class PatternDetector {
    public static Map<String, List<DetectedPattern>> detect(SystemDescriptionGenerator systemDescriptionGenerator) {
        final SortedSet<ClusterSet.Entry> clusterSet = systemDescriptionGenerator.getClusterSet().getInvokingClusterSet();
        final List<Enumeratable> hierarchyList = systemDescriptionGenerator.getHierarchies();
        final LinkedHashMap<String, List<DetectedPattern>> map = new LinkedHashMap<>();
        final OopPattern[] patternEnum = OopPattern.values();
        for (int i = 0; i < patternEnum.length; ++i) {
            final String patternName = patternEnum[i].toString();
            OopPattern pattern = patternEnum[i];
            final PatternDescription patternDescriptor = PatternDescriptionFactory.getPatternDescription(pattern);

            if (patternDescriptor == null) {
                continue;
            }

            if (patternDescriptor.getFeatureContainer().getNumberOfHierarchies() == 0) {
                final FeatureContainer systemContainer = systemDescriptionGenerator.getFeatureContainer();
                double[][] systemMatrix = null;
                if (patternName.equals(OopPattern.SINGLETON.toString())) {
                    systemMatrix = systemContainer.getSingletonMatrix();
                }
                else if (patternName.equals(OopPattern.FACTORY_METHOD.toString())) {
                    systemMatrix = systemContainer.getFactoryMethodMatrix();
                }
                final List<DetectedPattern> patternInstanceVector = new ArrayList<>();
                for (int j = 0; j < systemMatrix.length; ++j) {
                    if (systemMatrix[j][j] == 1.0) {
                        final DetectedPattern patternInstance = new DetectedPattern();
                        patternInstance.addEntry(patternInstance.new ClassEntry(patternName, systemContainer.getRoles().get(j), j, 1.0));
                        patternInstanceVector.add(patternInstance);
                    }
                }
                map.put(patternName, patternInstanceVector);
            }
            else if (patternDescriptor.getFeatureContainer().getNumberOfHierarchies() == 1) {
                final Vector<DetectedPattern> patternInstanceVector2 = new Vector<DetectedPattern>();
                for (final Enumeratable ih : hierarchyList) {
                    final List<Enumeratable> tempList = new ArrayList<Enumeratable>();
                    tempList.add(ih);
                    final FeatureContainer hierarchyMatrixContainer = systemDescriptionGenerator.getHierarchiesMatrixContainer(tempList);
                    generateResults(hierarchyMatrixContainer, patternDescriptor, patternInstanceVector2);
                }
                map.put(patternName, patternInstanceVector2);
            }
            else if (patternDescriptor.getFeatureContainer().getNumberOfHierarchies() == 2) {
                final Iterator<ClusterSet.Entry> it = clusterSet.iterator();
                final List<DetectedPattern> patternInstanceVector3 = new ArrayList<>();
                while (it.hasNext()) {
                    final ClusterSet.Entry entry = it.next();
                    final FeatureContainer hierarchiesMatrixContainer = systemDescriptionGenerator.getHierarchiesMatrixContainer(entry.getHierarchyList());
                    generateResults(hierarchiesMatrixContainer, patternDescriptor, patternInstanceVector3);
                }
                map.put(patternName, patternInstanceVector3);
            }
        }


        return map;

    }


    private static void generateResults(final FeatureContainer systemContainer,
                                        final PatternDescription patternDescriptor,
                                        final List<DetectedPattern> patternInstanceVector) {

        final double[][] results = SimilarityAlgorithm.getSimilarityScore(systemContainer, patternDescriptor.getFeatureContainer());
        if (results != null) {
            final ClusterResult clusterResult = new ClusterResult(results, patternDescriptor.getFeatureContainer(), systemContainer);
            final List<DetectedPattern> list = clusterResult.getPatternInstanceList();
            for (final DetectedPattern pi : list) {
                if (!patternInstanceVector.contains(pi)) {
                    patternInstanceVector.add(pi);
                }
            }
        }
    }


}
