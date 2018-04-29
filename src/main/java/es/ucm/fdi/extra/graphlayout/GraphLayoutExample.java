package es.ucm.fdi.extra.graphlayout;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

@SuppressWarnings("serial")
public class GraphLayoutExample extends JFrame {

  private GraphComponent graphComponent;
  Random rand;

  public GraphLayoutExample() {
    super("Dialog Example");
    initGUI();
  }

  private void initGUI() {
    rand = new Random(System.currentTimeMillis());

    JPanel mainPanel = new JPanel(new BorderLayout());

    graphComponent = new GraphComponent();
    mainPanel.add(graphComponent, BorderLayout.CENTER);

    JButton newGraph = new JButton("New Graph");
    newGraph.addActionListener(e -> generateGraph());

    mainPanel.add(newGraph, BorderLayout.PAGE_START);

    this.setContentPane(mainPanel);
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.pack();
    this.setVisible(true);

  }

  protected void generateGraph() {

    Graph g = new Graph();
    int numNodes = rand.nextInt(20) + 5;
    int numEdges = rand.nextInt(2 * numNodes);

    for (int i = 0; i < numNodes; i++) {
      g.addNode(new Node("n" + i));
    }

    for (int i = 0; i < numEdges; i++) {
      int s = rand.nextInt(numNodes);
      int t = rand.nextInt(numNodes);
      if (s == t) {
        t = (t + 1) % numNodes;
      }
      int l = rand.nextInt(30) + 20;
      Edge e = new Edge("e" + i, g.getNodes().get(s), g.getNodes().get(t), l,
          rand.nextFloat() < .5);

      int numDots = rand.nextInt(5);
      for (int j = 0; j < numDots; j++) {
        l = Math.max(0, rand.nextBoolean() ? l / 2 : l);
        e.addDot(new Dot("d" + j, l));
      }

      g.addEdge(e);
    }

    graphComponent.setGraph(g);

  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(GraphLayoutExample::new);
  }

}