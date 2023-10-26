package ir.fum.oop.pattern.detection;

import ir.fum.oop.pattern.description.FeatureContainer;
import org.math.array.DoubleArray;
import org.math.array.LinearAlgebra;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimilarityAlgorithm {
    public static double[][] getSimilarityScore(FeatureContainer system,
                                                FeatureContainer pattern) {
        double[][] similarity = new double[pattern.getRoles().size()][system.getRoles().size()];


        if (pattern.getSimilarMethodSignature() != null) {
            if (allElementsEqualToZero(system.getSimilarMethodSignature())) {
                return null;
            }
            similarity = LinearAlgebra.plus(similarity, getSimilarityScore(system.getSimilarMethodSignature(), pattern.getSimilarMethodSignature()));
        }

        if (pattern.getAbstractMatrix() != null) {
            if (allElementsEqualToZero(system.getAbstractMatrix())) {
                return null;
            }
            final double[][] s = system.getAbstractMatrix();
            final double[][] p = pattern.getAbstractMatrix();
            for (int i = 0; i < s.length; ++i) {
                for (int j = 0; j < p.length; ++j) {
                    if (s[i][i] == 1.0 && p[j][j] == 1.0) {
                        ++similarity[j][i];
                    }
                }
            }
        }
        if (pattern.getGeneralizationMatrix() != null) {
            if (allElementsEqualToZero(system.getGeneralizationMatrix())) {
                return null;
            }
            similarity = LinearAlgebra.plus(similarity, getSimilarityScore(system.getGeneralizationMatrix(), pattern.getGeneralizationMatrix()));
        }
        if (pattern.getAssociationMatrix() != null) {
            if (allElementsEqualToZero(system.getAssociationMatrix())) {
                return null;
            }
            int associationStartsFromRole = -1;
            int associationEndsToRole = -1;
            final double[][] p2 = pattern.getAssociationMatrix();
            for (int k = 0; k < p2.length; ++k) {
                for (int l = 0; l < p2[k].length; ++l) {
                    if (p2[k][l] == 1.0) {
                        associationStartsFromRole = k;
                        associationEndsToRole = l;
                    }
                }
            }
            final double[][] temp = getSimilarityScore(system.getAssociationMatrix(), pattern.getAssociationMatrix());
            final Map<Integer, LinkedHashMap<String, ArrayList<Integer>>> roleMaps = new LinkedHashMap<Integer, LinkedHashMap<String, ArrayList<Integer>>>();
            roleMaps.put(0, new LinkedHashMap<String, ArrayList<Integer>>());
            roleMaps.put(1, new LinkedHashMap<String, ArrayList<Integer>>());
            final NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(15);
            for (int m = 0; m < temp.length; ++m) {
                for (int j2 = 0; j2 < temp[m].length; ++j2) {
                    if (temp[m][j2] > 0.0 && temp[m][j2] < 0.01) {
                        final Map<String, ArrayList<Integer>> roleMap = roleMaps.get(m);
                        final String score = nf.format(temp[m][j2]);
                        if (!roleMap.containsKey(score)) {
                            final ArrayList<Integer> list = new ArrayList<Integer>();
                            list.add(j2);
                            roleMap.put(score, list);
                        }
                        else {
                            final ArrayList<Integer> list = roleMap.get(score);
                            list.add(j2);
                        }
                    }
                }
            }
            final Map<String, ArrayList<Integer>> associationStartsFromRoleMap = roleMaps.get(associationStartsFromRole);
            final Map<String, ArrayList<Integer>> associationEndsToRoleMap = roleMaps.get(associationEndsToRole);
            final Iterator<String> i$ = associationStartsFromRoleMap.keySet().iterator();
            while (i$.hasNext()) {
                final String score = i$.next();
                final ArrayList<Integer> associationStartsFromClasses = associationStartsFromRoleMap.get(score);
                final ArrayList<Integer> associationEndsToClasses = associationEndsToRoleMap.get(score);
                final double[][] systemAssociationMatrix = system.getAssociationMatrix();
                if (associationStartsFromClasses != null && associationEndsToClasses != null) {
                    for (final Integer i2 : associationStartsFromClasses) {
                        for (final Integer j3 : associationEndsToClasses) {
                            if (systemAssociationMatrix[i2][j3] == 1.0) {
                                if (temp[associationStartsFromRole][i2] != 1.0) {
                                    temp[associationStartsFromRole][i2] = 1.0;
                                }
                                if (temp[associationEndsToRole][j3] == 1.0) {
                                    continue;
                                }
                                temp[associationEndsToRole][j3] = 1.0;
                            }
                        }
                    }
                }
            }
            similarity = LinearAlgebra.plus(similarity, temp);
        }

        for (int i3 = 0; i3 < similarity.length; ++i3) {
            for (int j4 = 0; j4 < similarity[i3].length; ++j4) {
                similarity[i3][j4] *= pattern.getWeight()[i3];
            }
        }
        return similarity;
    }

    private static boolean allElementsEqualToZero(final double[][] m) {
        for (int i = 0; i < m.length; ++i) {
            for (int j = 0; j < m[i].length; ++j) {
                if (m[i][j] != 0.0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static double[][] getSimilarityScore(final double[][] A, final double[][] B) {
        final int m = A.length;
        final int n = B[0].length;
        if (A == new double[A.length][A[0].length] || B == new double[B.length][B[0].length]) {
            return new double[n][m];
        }
        double[][] X = DoubleArray.fill(n, m, 1.0);
        double[][] prevX = new double[n][m];
        boolean flag = false;
        int i = 0;
        while (!flag) {
            final double[][] temp1 = LinearAlgebra.times(LinearAlgebra.times(B, X), DoubleArray.transpose(A));
            final double[][] temp2 = LinearAlgebra.times(LinearAlgebra.times(DoubleArray.transpose(B), X), A);
            final double[][] temp3 = LinearAlgebra.plus(temp1, temp2);
            X = LinearAlgebra.divide(temp3, norm1(temp3));
            if (++i % 2 == 0) {
                flag = convergence(X, prevX);
                prevX = X;
            }
        }
        return X;
    }

    private static boolean convergence(final double[][] a, final double[][] b) {
        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a[i].length; ++j) {
                if (Math.abs(a[i][j] - b[i][j]) > 0.001) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double norm1(final double[][] m) {
        double f = 0.0;
        for (int j = 0; j < m[0].length; ++j) {
            double s = 0.0;
            for (int i = 0; i < m.length; ++i) {
                s += Math.abs(m[i][j]);
            }
            f = Math.max(f, s);
        }
        return f;
    }


}
