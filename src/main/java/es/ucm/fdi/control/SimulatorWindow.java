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

    Dimension horizontalThird = new Dimension(WINDOW_SIZE.width / 3, WINDOW_SIZE.height / 4);
    Dimension verticalThird = new Dimension(WINDOW_SIZE.width / 2, WINDOW_SIZE.height / 4);

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

		JSplitPane topLeftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				eventsEditor, eventsQueue);
		topLeftSplit.setVisible(true);
		topLeftSplit.setResizeWeight(.5);
    topLeftSplit.setContinuousLayout(true);

		JSplitPane topRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				topLeftSplit, reportsArea);
		topRightSplit.setVisible(true);
		topRightSplit.setResizeWeight(.66);
    topRightSplit.setContinuousLayout(true);

		JSplitPane bottomLeftTopSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, vehiclesTable, roadsTable);
		bottomLeftTopSplit.setVisible(true);
		bottomLeftTopSplit.setResizeWeight(.5);
    bottomLeftTopSplit.setContinuousLayout(true);

		JSplitPane bottomLeftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				bottomLeftTopSplit, junctionsTable);
		bottomLeftSplit.setVisible(true);
		bottomLeftSplit.setResizeWeight(.66);
    bottomLeftSplit.setContinuousLayout(true);
		
		JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				bottomLeftSplit, roadMap);
		bottomSplit.setVisible(true);
		bottomSplit.setResizeWeight(.5);
    bottomSplit.setContinuousLayout(true);

		JPanel simulationPanel = new JPanel(new GridLayout(1, 2));
		simulationPanel.setBackground(new Color(100, 60, 150));

		JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topRightSplit, bottomSplit);
		main.setResizeWeight(.4);
		main.setContinuousLayout(true);
		add(main);
	}

	public static void main(String... args) {
    new SimulatorWindow("Traffic Simulator", WINDOW_SIZE);
	}

}
