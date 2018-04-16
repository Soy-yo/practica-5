package es.ucm.fdi.control;

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

		SimulatorAction exit = new SimulatorAction("Exit", "exit.png",
				"Exit application", KeyEvent.VK_E, "control W",
				() -> System.exit(0));
		SimulatorAction save = new SimulatorAction("Save", "save.png", "Save",
				KeyEvent.VK_S, "control S",
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

    Dimension horizontalThird = new Dimension(WINDOW_SIZE.width / 6, WINDOW_SIZE.height / 8);
    Dimension verticalThird = new Dimension(WINDOW_SIZE.width / 3, WINDOW_SIZE.height / 8);

    // TODO añadir bordes con el título en las clases estas
    EventsEditorPanel eventsEditor = new EventsEditorPanel(horizontalThird);
    // TODO: pedir datos para las tablas a las clases
    InfoTablePanel eventsQueue = new InfoTablePanel(horizontalThird,
        new Object[]{"#", "Time", "Type"});
    ReportsAreaPanel reportsArea = new ReportsAreaPanel(horizontalThird);
    InfoTablePanel vehiclesTable = new InfoTablePanel(verticalThird,
        new Object[]{"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"});
    InfoTablePanel roadsTable = new InfoTablePanel(verticalThird,
        new Object[]{"ID", "Source", "Target", "Length", "Max Speed", "Vehicles"});
    InfoTablePanel junctionsTable = new InfoTablePanel(verticalThird,
        new Object[]{"ID", "Green", "Red"});
		JPanel roadMap = new JPanel();
		roadMap.setBackground(Color.BLACK);

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
    splitPane.setVisible(true);
    splitPane.setResizeWeight(weight);
    splitPane.setContinuousLayout(true);
    return splitPane;
  }

	public static void main(String... args) {
    new SimulatorWindow("Traffic Simulator", WINDOW_SIZE);
	}

}
