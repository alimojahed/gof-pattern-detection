package ir.fum.oop;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;

public class TestGraphX extends JFrame {
    public TestGraphX() {
        super("JGraphX Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, "Node 1", 0, 0, 80, 30);
            Object v2 = graph.insertVertex(parent, null, "Node 2", 0, 0, 80, 30);
            graph.insertEdge(parent, null, "Edge", v1, v2);

        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(graph.getDefaultParent());

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TestGraphX example = new TestGraphX();
            example.setVisible(true);
        });
    }
}
