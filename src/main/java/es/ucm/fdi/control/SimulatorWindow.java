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
		JPanel eventsEditor = new JPanel();
		JPanel eventsQueue = new JPanel();
		JPanel reportsArea = new JPanel();
		JPanel vehiclesTable = new JPanel();
		JPanel roadsTable = new JPanel();
		JPanel junctionsTable = new JPanel();
		JPanel roadMap = new JPanel();
		eventsEditor.setBackground(Color.WHITE);
		eventsQueue.setBackground(Color.CYAN);
		reportsArea.setBackground(Color.YELLOW);
		vehiclesTable.setBackground(Color.RED);
		roadsTable.setBackground(Color.YELLOW);
		junctionsTable.setBackground(Color.RED);
		roadMap.setBackground(Color.BLACK);

		JSplitPane topLeftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				eventsEditor, eventsQueue);
		topLeftSplit.setVisible(true);
		topLeftSplit.setResizeWeight(.5);

		JSplitPane topRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				topLeftSplit, reportsArea);
		topRightSplit.setVisible(true);
		topRightSplit.setResizeWeight(.66);

		JSplitPane bottomLeftTopSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, vehiclesTable, roadsTable);
		bottomLeftTopSplit.setVisible(true);
		bottomLeftTopSplit.setResizeWeight(.5);

		JSplitPane bottomLeftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				bottomLeftTopSplit, junctionsTable);
		bottomLeftSplit.setVisible(true);
		bottomLeftSplit.setResizeWeight(.66);
		
		JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				bottomLeftSplit, roadMap);
		bottomSplit.setVisible(true);
		bottomSplit.setResizeWeight(.5);

		JPanel simulationPanel = new JPanel(new GridLayout(1, 2));
		simulationPanel.setBackground(new Color(100, 60, 150));

		JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topRightSplit, bottomSplit);
		main.setResizeWeight(.4);
		main.setContinuousLayout(true);
		add(main);
	}

	public static void main(String... args) {
		JFrame window = new SimulatorWindow("Traffic Simulator", new Dimension(
				1000, 1000));
	}

}
