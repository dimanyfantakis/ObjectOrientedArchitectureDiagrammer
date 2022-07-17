package manager.diagram;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import model.LeafNode;
import model.LeafNodeRelationship;

import java.util.Map;
import java.util.stream.Collectors;

public class ClassDiagramArrangement extends DiagramArrangement{

    public void populateGraph(Map<Object, Integer> graphNodes, Map<Object, Integer> graphEdges, Graph<Integer, String> graph) {
        addVertexes(graphNodes.entrySet().stream()
                .collect(Collectors.toMap(e -> (LeafNode)(e.getKey()), Map.Entry::getValue)), graph);
        addEdges(graphNodes.entrySet().stream().collect(Collectors.toMap(e -> (LeafNode)(e.getKey()), Map.Entry::getValue)),
                graphEdges.entrySet().stream().collect(Collectors.toMap(e -> (LeafNodeRelationship)(e.getKey()), Map.Entry::getValue)), graph);
    }

    private void addVertexes(Map<LeafNode, Integer> graphNodes, Graph<Integer, String> graph) {
        for (Integer i : graphNodes.values()) {
            graph.addVertex(i);
        }
    }

    private void addEdges(Map<LeafNode, Integer> graphNodes, Map<LeafNodeRelationship, Integer> graphEdges, Graph<Integer, String> graph) {
        for (Map.Entry<LeafNodeRelationship, Integer> entry : graphEdges.entrySet()) {
            graph.addEdge(graphNodes.get(entry.getKey().getStartingLeafNode()) + " " + graphNodes.get(entry.getKey().getEndingLeafNode()),
                    graphNodes.get(entry.getKey().getStartingLeafNode()), graphNodes.get(entry.getKey().getEndingLeafNode()), EdgeType.DIRECTED);
        }
    }

}
