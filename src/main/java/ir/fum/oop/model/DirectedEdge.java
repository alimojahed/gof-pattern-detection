package ir.fum.oop.model;


import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

@Getter
public class DirectedEdge {
    private String from;
    private String to;
    private int value = 1;

    public static DirectedEdge addWeight(DirectedEdge edge,
                                         String from,
                                         String to,
                                         DependencyType dependencyType) {
        if (edge == null) {
            edge = new DirectedEdge();
            edge.from = from;
            edge.to = to;
        }


        edge.value  *= dependencyType.getValue();

        return edge;
    }

    public String getKey() {
        return from + "-" + to;
    }

    public Pair<String, String> getNodeNames() {
        return Pair.of(from, to);
    }

    public static String getKey(String from, String to) {
        return from + "-" + to;
    }

}
