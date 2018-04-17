package es.ucm.fdi.extra.graphlayout;

import java.util.ArrayList;
import java.util.List;

public class Graph {

  private List<Edge> edges;
  private List<Node> nodes;
	
	public Graph() {
    edges = new ArrayList<>();
    nodes = new ArrayList<>();
	}
	
	public void addEdge(Edge e) {
    edges.add(e);
	}
	
	public void addNode(Node n) {
    nodes.add(n);
	}
	
	public List<Edge> getEdges() {
    return edges;
	}
	
	public List<Node> getNodes() {
    return nodes;
  }

}
