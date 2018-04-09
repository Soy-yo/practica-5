package es.ucm.fdi.control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SimulatorWindow extends JFrame {

  public SimulatorWindow(String title, Dimension dimension) {
    super(title);
    initialize(dimension);
  }

  private void initialize(Dimension dimension) {
    setSize(dimension);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    addToolBar();
    addSections();
    setVisible(true);
  }

  private void addToolBar() {
    // Tool bar
    JToolBar bar = new JToolBar();

    SimulatorAction exit = new SimulatorAction("Exit", "exit.png", "Exit application",
        KeyEvent.VK_E, "control W", () -> System.exit(0));
    SimulatorAction save = new SimulatorAction(
        "Save", "save.png", "Save", KeyEvent.VK_S, "control S",
        () -> System.err.println("Saving..."));

    bar.add(save);
    bar.add(exit);

    add(bar, BorderLayout.NORTH);

    // Menu bar
    JMenuBar menu = new JMenuBar();

    JMenu file = new JMenu("File");
    file.add(save);
    file.add(exit);

    menu.add(file);

    setJMenuBar(menu);
  }

  private void addSections() {
    JPanel eventsPanel = new JPanel(new GridLayout(1, 3));
    eventsPanel.setBackground(new Color(50, 0, 0));

    JPanel simulationPanel = new JPanel(new GridLayout(1, 2));
    simulationPanel.setBackground(new Color(100, 60, 150));

    JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT, eventsPanel, simulationPanel);
    main.setResizeWeight(.55);
    main.setContinuousLayout(true);
    add(main);
  }


  public static void main(String... args) {
    JFrame window = new SimulatorWindow("Traffic Simulator", new Dimension(1000, 1000));
  }

}
