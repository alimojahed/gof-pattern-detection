package ir.fum.oop.pattern.detection;

import ir.fum.oop.pattern.description.FeatureContainer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

public class ClusterResult {
    private TreeSet<Entry> entrySet;
    private FeatureContainer patternDescriptor;
    private FeatureContainer systemContainer;

    public ClusterResult(final double[][] results, final FeatureContainer patternDescriptor, final FeatureContainer systemContainer) {
        this.entrySet = new TreeSet<Entry>();
        this.patternDescriptor = patternDescriptor;
        this.systemContainer = systemContainer;
        final List<String> rowNameList = patternDescriptor.getRoles();
        final List<String> columnNameList = systemContainer.getRoles();
        for (int i = 0; i < results.length; ++i) {
            for (int j = 0; j < results[i].length; ++j) {
                final Entry entry = new Entry(results[i][j], rowNameList.get(i), columnNameList.get(j), j);
                this.entrySet.add(entry);
            }
        }
    }

    public List<DetectedPattern> getPatternInstanceList() {
        final List<DetectedPattern.ClassEntry> firstRoleEntryList = new ArrayList<>();
        final List<DetectedPattern.ClassEntry> secondRoleEntryList = new ArrayList<>();
        double firstRoleMaxScore = 0.0;
        double secondRoleMaxScore = 0.0;
        final DetectedPattern patternInstance = new DetectedPattern();
        for (final Entry entry : this.entrySet) {
            if (entry.getScore() > 0.45) { //todo: tune confidence
                if (firstRoleEntryList.isEmpty() && secondRoleEntryList.isEmpty()) {
                    firstRoleEntryList.add(patternInstance.new ClassEntry(entry.getRole(), entry.getClassName(), entry.getPosition(), entry.getScore()));
                    firstRoleMaxScore = entry.getScore();
                } else if (firstRoleEntryList.get(0).getRole().equals(entry.getRole())) {
                    if (entry.getScore() != firstRoleMaxScore) {
                        continue;
                    }
                    firstRoleEntryList.add(patternInstance.new ClassEntry(entry.getRole(), entry.getClassName(), entry.getPosition(), entry.getScore()));
                } else if (secondRoleMaxScore == 0.0) {
                    secondRoleEntryList.add(patternInstance.new ClassEntry(entry.getRole(), entry.getClassName(), entry.getPosition(), entry.getScore()));
                    secondRoleMaxScore = entry.getScore();
                } else {
                    if (entry.getScore() != secondRoleMaxScore) {
                        continue;
                    }
                    secondRoleEntryList.add(patternInstance.new ClassEntry(entry.getRole(), entry.getClassName(), entry.getPosition(), entry.getScore()));
                }
            }
        }
        final List<DetectedPattern> patternInstanceList = new ArrayList<DetectedPattern>();
        if (firstRoleEntryList.size() == 1 && secondRoleEntryList.size() > 1) {
            for (int i = 0; i < secondRoleEntryList.size(); ++i) {
                final DetectedPattern instance = new DetectedPattern();
                instance.addEntry(firstRoleEntryList.get(0));
                instance.addEntry(secondRoleEntryList.get(i));
                instance.setScore(firstRoleEntryList.get(0).getScore());
                patternInstanceList.add(instance);
            }
            return patternInstanceList;
        }
        if (firstRoleEntryList.size() > 1 && secondRoleEntryList.size() == 1) {
            for (int i = 0; i < firstRoleEntryList.size(); ++i) {
                final DetectedPattern instance = new DetectedPattern();
                instance.addEntry(secondRoleEntryList.get(0));
                instance.addEntry(firstRoleEntryList.get(i));
                instance.setScore(secondRoleEntryList.get(0).getScore());
                patternInstanceList.add(instance);
            }
            return patternInstanceList;
        }
        if (firstRoleEntryList.size() == 1 && secondRoleEntryList.size() == 1) {
            final DetectedPattern instance2 = new DetectedPattern();
            instance2.addEntry(firstRoleEntryList.get(0));
            instance2.addEntry(secondRoleEntryList.get(0));
            instance2.setScore(firstRoleEntryList.get(0).getScore());
            patternInstanceList.add(instance2);
            return patternInstanceList;
        }
        if (firstRoleEntryList.size() > 1 && secondRoleEntryList.size() > 1) {
            final LinkedHashMap<DetectedPattern, Integer> tempPatternInstanceMap = new LinkedHashMap<DetectedPattern, Integer>();
            int maxScore = 0;
            for (int j = 0; j < firstRoleEntryList.size(); ++j) {
                for (int k = 0; k < secondRoleEntryList.size(); ++k) {
                    final DetectedPattern instance3 = new DetectedPattern();
                    instance3.addEntry(firstRoleEntryList.get(j));
                    instance3.addEntry(secondRoleEntryList.get(k));
                    final int score = this.score(firstRoleEntryList.get(j), secondRoleEntryList.get(k));
                    if (score > maxScore) {
                        maxScore = score;
                    }

                    tempPatternInstanceMap.put(instance3, score);
                }
            }
            for (final DetectedPattern instance4 : tempPatternInstanceMap.keySet()) {
                if (tempPatternInstanceMap.get(instance4) == maxScore) {
                    instance4.setScore(maxScore);
                    patternInstanceList.add(instance4);

                }
            }
            return patternInstanceList;
        }
        return patternInstanceList;
    }

    private int score(final DetectedPattern.ClassEntry e1, final DetectedPattern.ClassEntry e2) {
        int score = 0;
        if (this.patternDescriptor.getAbstractMethodMatrix() != null) {
            final double[][] matrix = this.systemContainer.getAbstractMethodMatrix();
            if (matrix[e1.getPosition()][e2.getPosition()] == 1.0 || matrix[e2.getPosition()][e1.getPosition()] == 1.0) {
                ++score;
            }
        }

        if (this.patternDescriptor.getSimilarMethodSignature() != null) {
            final double[][] matrix = this.systemContainer.getSimilarMethodSignature();
            if (matrix[e1.getPosition()][e2.getPosition()] == 1.0 || matrix[e2.getPosition()][e1.getPosition()] == 1.0) {
                ++score;
            }
        }

        if (this.patternDescriptor.getGeneralizationMatrix() != null) {
            final double[][] matrix = this.systemContainer.getGeneralizationMatrix();
            if (matrix[e1.getPosition()][e2.getPosition()] == 1.0 || matrix[e2.getPosition()][e1.getPosition()] == 1.0) {
                ++score;
            }
        }
        if (this.patternDescriptor.getAssociationMatrix() != null) {
            final double[][] matrix = this.systemContainer.getAssociationMatrix();
            if (matrix[e1.getPosition()][e2.getPosition()] == 1.0 || matrix[e2.getPosition()][e1.getPosition()] == 1.0) {
                ++score;
            }
        }
        return score;
    }

    private class Entry implements Comparable {
        private volatile int hashCode;
        private Double score;
        private String role;
        private String className;
        private int position;

        public Entry(final Double score, final String role, final String className, final int position) {
            this.hashCode = 0;
            this.score = score;
            this.role = role;
            this.className = className;
            this.position = position;
        }

        public Double getScore() {
            return this.score;
        }

        public String getRole() {
            return this.role;
        }

        public String getClassName() {
            return this.className;
        }

        public int getPosition() {
            return this.position;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Entry) {
                final Entry entry = (Entry) o;
                return this.score.equals(entry.score) && this.role.equals(entry.role) && this.className.equals(entry.className);
            }
            return false;
        }

        public int compareTo(final Object o) {
            final Entry entry = (Entry) o;
            if (!this.score.equals(entry.score)) {
                return -this.score.compareTo(entry.score);
            }
            if (!this.role.equals(entry.role)) {
                return this.role.compareTo(entry.role);
            }
            if (!this.className.equals(entry.className)) {
                return this.className.compareTo(entry.className);
            }
            return 0;
        }

        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int result = 17;
                result = 37 * result + this.score.hashCode();
                result = 37 * result + this.role.hashCode();
                result = 37 * result + this.className.hashCode();
                this.hashCode = result;
            }
            return this.hashCode;
        }

        @Override
        public String toString() {
            return this.score + " (" + this.role + "," + this.className + ")";
        }
    }
}
