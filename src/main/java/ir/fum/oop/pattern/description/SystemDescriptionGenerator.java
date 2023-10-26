package ir.fum.oop.pattern.description;

import ir.fum.oop.model.ClassInfo;
import ir.fum.oop.model.ClassMethodInfo;
import ir.fum.oop.pattern.detection.inheritance.Enumeratable;
import ir.fum.oop.pattern.detection.inheritance.HierarchyDetection;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class SystemDescriptionGenerator {
    private FeatureContainer featureContainer;
    private SystemDescription systemDescription;
    private List<Enumeratable> hierarchies;
    private ClusterSet clusterSet;

    private final int SYSTEM_MATRIX_SIZE;

    public SystemDescriptionGenerator(SystemDescription systemDescription) {
        this.systemDescription = systemDescription;

        SYSTEM_MATRIX_SIZE = systemDescription.getNumberOfClasses();

        this.featureContainer = new FeatureContainer();

        HierarchyDetection hierarchyDetection = new HierarchyDetection(systemDescription);
        this.hierarchies = hierarchyDetection.getHierarchyList();

        setClassNames();
        setGeneralizationMatrix();
        setAssociationMatrix();
        setAbstractMatrix();
        setSingleton();
        setDelegationMatrix();
        setFactoryMethodMatrix();
        setOverrideMethodMatrix();
        setSimilarMethodSignatureMatrix();
        setAbstractMethodMatrix();

        this.clusterSet = generateClusterSet();


    }

    private void setGeneralizationMatrix() {
        ListIterator<ClassInfo> classInfoListIterator = systemDescription.getClassInfos()
                .listIterator();

        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        int counter = 0;

        while (classInfoListIterator.hasNext()) {
            ClassInfo classInfo = classInfoListIterator.next();
            ListIterator<String> parents = classInfo.getSuperClassesOrInterfaces().listIterator();

            while (parents.hasNext()) {
                String superClassName = parents.next();
                int index = systemDescription.getClassIndexLocator().getOrDefault(superClassName, -1);
                if (index != -1) {
                    matrix[counter][index] = 1.0;
                }
            }

            counter++;

        }

        featureContainer.setGeneralizationMatrix(matrix);
    }

    private void setAssociationMatrix() {

        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        for (int index = 0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);
            for (String associatedClass: classInfo.getAssociation()) {
                int relatedClassIndex = systemDescription.getClassIndexLocator()
                        .getOrDefault(associatedClass, -1);

                if (relatedClassIndex != -1) {
                    matrix[index][relatedClassIndex] = 1.0;
                }

            }
        }


        featureContainer.setAssociationMatrix(matrix);
    }

    private void setAbstractMatrix() {
        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];
        for (int index = 0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);

            if (classInfo.isClassIsAbstract() || classInfo.isClassIsInterface()) {
                matrix[index][index] = 1.0;
            }

        }


        featureContainer.setAbstractMatrix(matrix);

    }


    private void setSingleton(){
        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        for (int index = 0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);

            boolean singleton = classInfo.getFields()
                    .stream()
                    .anyMatch(fieldOrParameter -> fieldOrParameter.isStatic() && fieldOrParameter.getType().equals(classInfo.getClassName()));

            if (singleton) {
                matrix[index][index] = 1.0;
            }


        }


        featureContainer.setSingletonMatrix(matrix);

    }


    private void setDelegationMatrix() {
        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        for (int index =0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);

            for (String delegatedClasses: classInfo.getDelegation()) {
                int delegatedIndex = systemDescription.getClassIndexLocator().getOrDefault(delegatedClasses, -1);

                if (delegatedIndex != -1) {
                    matrix[index][delegatedIndex] = 1.0;
                }

            }

        }

        featureContainer.setMethodInvocationMatrix(matrix);

    }

    private void setFactoryMethodMatrix(){
        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        for (int index = 0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);
            for (ClassMethodInfo classMethodInfo: classInfo.getMethods()) {
                if (classMethodInfo.isStatic() && classInfo.getInstantiation().contains(classMethodInfo.getReturnType())) {
                    matrix[index][index] = 1.0;
                }
            }

        }

        featureContainer.setFactoryMethodMatrix(matrix);
    }

    private void setOverrideMethodMatrix(){
        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        for (int index = 0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);

            if (!classInfo.getOverrideMethods().isEmpty()) {
                matrix[index][index] = 1.0;
            }

        }

        featureContainer.setOverrideMethods(matrix);

    }


    private void setSimilarMethodSignatureMatrix(){
        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        for (int index = 0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);

            for (ClassMethodInfo classMethodInfo: classInfo.getMethods()) {
                for (ClassMethodInfo candidateMethod: classInfo.getMethods()) {
                    if (classMethodInfo.similar(candidateMethod)) {
                        matrix[index][index] = 1.0;
                    }
                }
            }
        }

        featureContainer.setSimilarMethodSignature(matrix);

    }

    private void setAbstractMethodMatrix() {
        final double[][] matrix = new double[SYSTEM_MATRIX_SIZE][SYSTEM_MATRIX_SIZE];

        for (int index = 0; index < systemDescription.getNumberOfClasses(); index++) {
            ClassInfo classInfo = systemDescription.getClassInfos().get(index);

            if (!classInfo.getAbstractMethods().isEmpty()) {
                matrix[index][index] = 1.0;
            }

        }

        featureContainer.setAbstractMethodMatrix(matrix);

    }



    private ClusterSet generateClusterSet() {
        final ClusterSet clusterSet = new ClusterSet();
        final double[][] systemAdjacencyMatrix = this.getFeatureContainer().getMethodInvocationMatrix();
        for (int i = 0; i < this.hierarchies.size(); ++i) {
            final Enumeratable ih1 = this.hierarchies.get(i);
            Enumeratable ih2 = null;
            for (int j = i + 1; j < this.hierarchies.size(); ++j) {
                double sum = 0.0;
                final Enumeration<DefaultMutableTreeNode> e1 = ih1.getEnumeration();
                final List<String> ih1NodesChecked = new ArrayList<String>();
                while (e1.hasMoreElements()) {
                    final String className1 = (String)e1.nextElement().getUserObject();
                    if (!ih1NodesChecked.contains(className1)) {
                        ih1NodesChecked.add(className1);
                        ih2 = this.hierarchies.get(j);
                        final Enumeration<DefaultMutableTreeNode> e2 = ih2.getEnumeration();
                        final List<String> ih2NodesChecked = new ArrayList<String>();
                        while (e2.hasMoreElements()) {
                            final String className2 = (String)e2.nextElement().getUserObject();
                            if (!ih2NodesChecked.contains(className2)) {
                                ih2NodesChecked.add(className2);
                                if (className1.equals(className2)) {
                                    continue;
                                }
                                sum += systemAdjacencyMatrix[this.systemDescription.getClassIndexLocator().get(className1)][this.systemDescription.getClassIndexLocator().get(className2)];
                            }
                        }
                    }
                }
                final ClusterSet.Entry entry = clusterSet.new Entry();
                entry.addHierarchy(ih1);
                entry.addHierarchy(ih2);
                entry.setNumberOfMethodInvocations((int)sum);
                clusterSet.addClusterEntry(entry);
            }
        }
        return clusterSet;
    }


    private void setClassNames() {
        featureContainer.setRoles(systemDescription.getClassInfos()
                .stream()
                .map(ClassInfo::getClassName)
                .collect(Collectors.toList())
        );
    }


    public FeatureContainer getHierarchiesMatrixContainer(final List<Enumeratable> hierarchyList) {
        final List<String> hierarchiesClassNameList = new ArrayList<String>();
        final FeatureContainer hierarchiesMatrixContainer = new FeatureContainer();
        for (final Enumeratable ih : hierarchyList) {
            final Enumeration<DefaultMutableTreeNode> e = ih.getEnumeration();
            while (e.hasMoreElements()) {
                final String s = (String)e.nextElement().getUserObject();
                if (!hierarchiesClassNameList.contains(s)) {
                    hierarchiesClassNameList.add(s);
                }
            }
        }
        hierarchiesMatrixContainer.setRoles(hierarchiesClassNameList);
        hierarchiesMatrixContainer.setGeneralizationMatrix(this.generateHierarchiesMatrix(hierarchiesClassNameList, this.getFeatureContainer().getGeneralizationMatrix()));
        hierarchiesMatrixContainer.setAssociationMatrix(this.generateHierarchiesMatrix(hierarchiesClassNameList, this.getFeatureContainer().getAssociationMatrix()));
        hierarchiesMatrixContainer.setAbstractMatrix(this.generateHierarchiesMatrix(hierarchiesClassNameList, this.getFeatureContainer().getAbstractMatrix()));
        hierarchiesMatrixContainer.setAbstractMethodMatrix(this.generateHierarchiesMatrix(hierarchiesClassNameList, this.getFeatureContainer().getAbstractMethodMatrix()));
        hierarchiesMatrixContainer.setSimilarMethodSignature(this.generateHierarchiesMatrix(hierarchiesClassNameList, this.getFeatureContainer().getSimilarMethodSignature()));
        return hierarchiesMatrixContainer;
    }

    private double[][] generateHierarchiesMatrix(final List<String> hierarchiesClassNameList, final double[][] systemMatrix) {
        final double[][] hierarchiesMatrix = new double[hierarchiesClassNameList.size()][hierarchiesClassNameList.size()];
        for (int i = 0; i < hierarchiesClassNameList.size(); ++i) {
            final String className1 = hierarchiesClassNameList.get(i);
            final int systemI = this.systemDescription.getClassIndexLocator().get(className1);
            for (int j = 0; j < hierarchiesClassNameList.size(); ++j) {
                final String className2 = hierarchiesClassNameList.get(j);
                final int systemJ = this.systemDescription.getClassIndexLocator().get(className2);
                hierarchiesMatrix[i][j] = systemMatrix[systemI][systemJ];
            }
        }
        return hierarchiesMatrix;
    }




    public FeatureContainer getFeatureContainer() {
        return featureContainer;
    }

    public SystemDescription getSystemDescription() {
        return systemDescription;
    }

    public List<Enumeratable> getHierarchies() {
        return hierarchies;
    }

    public ClusterSet getClusterSet() {
        return clusterSet;
    }
}
