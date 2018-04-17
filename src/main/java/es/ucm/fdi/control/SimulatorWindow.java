package es.ucm.fdi.control;

import es.ucm.fdi.extra.graphlayout.GraphComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SimulatorWindow extends JFrame {

  private static Dimension WINDOW_SIZE = new Dimension(1000, 1000);

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

    SimulatorAction load = new SimulatorAction("Load events", "open.png",
        "Load events file", KeyEvent.VK_L, "control L",
        () -> System.err.println("Opening file..."));

    SimulatorAction save = new SimulatorAction("Save events", "save.png",
        "Save events", KeyEvent.VK_S, "control S",
        () -> System.err.println("Saving..."));

    SimulatorAction clear = new SimulatorAction("Clear", "clear.png",
        "Clear events editor", KeyEvent.VK_C, "control D",
        () -> System.err.println("Clearing events editor..."));

    // TODO: events no se qué hace (el que parece un calendario), así que no lo pongo aún

    SimulatorAction run = new SimulatorAction("Run", "play.png",
        "Run simulation", KeyEvent.VK_R, "control P",
        () -> System.err.println("Running..."));

    SimulatorAction reset = new SimulatorAction("Reset", "reset.png",
        "Reset simulation", null, "control shift P",
        () -> System.err.println("Resetting..."));

    // TODO: steps y time no sé cómo se harán ahora mismo

    SimulatorAction generateReport = new SimulatorAction("Generate report", "report.png",
        "Generate new report", null, null,
        () -> System.err.println("Generating report..."));

    SimulatorAction deleteReport = new SimulatorAction("Delete report", "delete_report.png",
        "Delete current report", null, null,
        () -> System.err.println("Deleting report..."));

    SimulatorAction saveReport = new SimulatorAction("Save report", "save_report.png",
        "Save current report", null, "control shift S",
        () -> System.err.println("Saving report..."));

		SimulatorAction exit = new SimulatorAction("Exit", "exit.png",
				"Exit application", KeyEvent.VK_E, "control W",
				() -> System.exit(0));

    bar.add(load);
		bar.add(save);
    bar.add(clear);
    bar.add(run);
    bar.add(reset);
    bar.add(generateReport);
    bar.add(deleteReport);
    bar.add(saveReport);
		bar.add(exit);

		add(bar, BorderLayout.NORTH);

		// Menu bar
		JMenuBar menu = new JMenuBar();

		JMenu file = new JMenu("File");
    file.add(load);
		file.add(save);
    file.addSeparator();
    file.add(saveReport);
    file.addSeparator();
		file.add(exit);
    JMenu simulator = new JMenu("Simulator");
    simulator.add(run);
    simulator.add(reset);
    JMenu reports = new JMenu("Reports");
    reports.add(generateReport);
    reports.add(deleteReport);

		menu.add(file);
    menu.add(simulator);
    menu.add(reports);

		setJMenuBar(menu);
	}

	private void addSections() {

    Dimension horizontalThird = new Dimension(WINDOW_SIZE.width / 6, WINDOW_SIZE.height / 8);
    Dimension verticalThird = new Dimension(WINDOW_SIZE.width / 3, WINDOW_SIZE.height / 8);

    EventsEditorPanel eventsEditor = new EventsEditorPanel(horizontalThird);
    // TODO: pedir datos para las tablas a las clases
    InfoTablePanel eventsQueue = new InfoTablePanel("Events Queue", horizontalThird,
        new Object[]{"#", "Time", "Type"});
    ReportsAreaPanel reportsArea = new ReportsAreaPanel(horizontalThird);
    InfoTablePanel vehiclesTable = new InfoTablePanel("Vehicles", verticalThird,
        new Object[]{"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"});
    InfoTablePanel roadsTable = new InfoTablePanel("Roads", verticalThird,
        new Object[]{"ID", "Source", "Target", "Length", "Max Speed", "Vehicles"});
    InfoTablePanel junctionsTable = new InfoTablePanel("Junctions", verticalThird,
        new Object[]{"ID", "Green", "Red"});
    GraphComponent roadMap = new GraphComponent();

    JSplitPane topLeftSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        eventsEditor, eventsQueue, .5);
    JSplitPane topRightSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        topLeftSplit, reportsArea, .66);

    JSplitPane bottomLeftTopSplit = createSeparator(JSplitPane.VERTICAL_SPLIT,
        vehiclesTable, roadsTable, .5);
    JSplitPane bottomLeftSplit = createSeparator(JSplitPane.VERTICAL_SPLIT,
        bottomLeftTopSplit, junctionsTable, .66);

    JSplitPane bottomSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        bottomLeftSplit, roadMap, .5);

    JSplitPane main = createSeparator(JSplitPane.VERTICAL_SPLIT,
        topRightSplit, bottomSplit, .4);

		add(main);
	}

  private JSplitPane createSeparator(int orientation, Component first, Component second,
                                     double weight) {
    JSplitPane splitPane = new JSplitPane(orientation, first, second);
    splitPane.setDividerSize(5);
    splitPane.setVisible(true);
    splitPane.setResizeWeight(weight);
    splitPane.setContinuousLayout(true);
    return splitPane;
  }

	public static void main(String... args) {
    new SimulatorWindow("Traffic Simulator", WINDOW_SIZE);
	}

}
