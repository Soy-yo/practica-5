package es.ucm.fdi.control;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.extra.graphlayout.GraphComponent;
import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.Vehicle;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SimulatorWindow extends JFrame {

	private static Dimension WINDOW_SIZE = new Dimension(1000, 1000);

	private Controller controller;
	private EventsEditorPanel eventsEditor;
	private InfoTablePanel<Event> eventsQueue;
	private ReportsAreaPanel reportsArea;
	private InfoTablePanel<Vehicle> vehiclesTable;
	private InfoTablePanel<Road> roadsTable;
	private InfoTablePanel<Junction> junctionsTable;
	private GraphComponent roadMap;

	public SimulatorWindow(String title, Dimension dimension) {
		super(title);
		controller = new Controller(new TrafficSimulator());
		initialize(dimension);
	}

	private void initialize(Dimension dimension) {
		setSize(dimension);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addSections();
		addToolBar();
		addListeners();
		setVisible(true);
	}

	private void addToolBar() {
		// Tool bar
		JToolBar bar = new JToolBar();

		SimulatorAction load = new SimulatorAction("Load events", "open.png",
				"Load events file", KeyEvent.VK_L, "control L",
				this::loadEvents);

		SimulatorAction save = new SimulatorAction("Save events", "save.png",
				"Save events", KeyEvent.VK_S, "control S", this::saveEvents);

		SimulatorAction clear = new SimulatorAction("Clear", "clear.png",
				"Clear events editor", KeyEvent.VK_C, "control D",
				eventsEditor::clear);

		SimulatorAction events = new SimulatorAction("Set events",
				"events.png", "Move events to events queue", null,
				"control enter", this::readEvents);

		SimulatorAction run = new SimulatorAction("Run", "play.png",
				"Run simulation", KeyEvent.VK_R, "control P",
				() -> System.err.println("Running..."));

		SimulatorAction reset = new SimulatorAction("Reset", "reset.png",
				"Reset simulation", null, "control shift P",
				() -> System.err.println("Resetting..."));

		SpinnerNumberModel steps = new SpinnerNumberModel(1, 0, 100, 1);
		JSpinner stepCounter = new JSpinner(steps);
		stepCounter.setMaximumSize(new Dimension(75, 30));

		JTextField time = new JTextField("0", 4);
		time.setMaximumSize(new Dimension(75, 30));
		time.setHorizontalAlignment(JTextField.RIGHT);
		time.setEnabled(false);
		time.setDisabledTextColor(UIManager.getColor("TextField.foreground"));

		// TODO: añadir funcionalidad de steps y time

		SimulatorAction generateReport = new SimulatorAction("Generate report",
				"report.png", "Generate new report", null, null,
				() -> System.err.println("Generating report..."));

		SimulatorAction deleteReport = new SimulatorAction("Delete report",
				"delete_report.png", "Delete current report", null, null,
				() -> System.err.println("Deleting report..."));

		SimulatorAction saveReport = new SimulatorAction("Save report",
				"save_report.png", "Save current report", null,
				"control shift S", () -> System.err.println("Saving report..."));

		SimulatorAction exit = new SimulatorAction("Exit", "exit.png",
				"Exit application", KeyEvent.VK_E, "control W",
				() -> System.exit(0));

		/*
		 * bar.add(load); bar.add(save); bar.add(clear); bar.add(events);
		 * bar.add(run); bar.add(reset); bar.add(new JLabel(" Steps: "));
		 * bar.add(stepCounter); bar.add(new JLabel(" Time: ")); bar.add(time);
		 * bar.add(generateReport); bar.add(deleteReport); bar.add(saveReport);
		 * bar.add(exit);
		 */

		addActionToToolBar(bar, load, save, clear, events, run, reset);
		addComponentToToolBar(bar, new JLabel(" Steps: "), stepCounter,
				new JLabel(" Time: "), time);
		addActionToToolBar(bar, generateReport, deleteReport, saveReport, exit);

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
		simulator.addSeparator();
		simulator.add(events);

		JMenu reports = new JMenu("Reports");

		reports.add(generateReport);
		reports.add(deleteReport);

		menu.add(file);
		menu.add(simulator);
		menu.add(reports);

		setJMenuBar(menu);
	}

	private void addSections() {

		Dimension horizontalThird = new Dimension(WINDOW_SIZE.width / 6,
				WINDOW_SIZE.height / 8);
		Dimension verticalThird = new Dimension(WINDOW_SIZE.width / 3,
				WINDOW_SIZE.height / 8);

		eventsEditor = new EventsEditorPanel(horizontalThird);
		// TODO: pedir datos para las tablas a las clases
		eventsQueue = new InfoTablePanel<>("Events Queue", horizontalThird,
				Event.INFO);
		reportsArea = new ReportsAreaPanel(horizontalThird);
		vehiclesTable = new InfoTablePanel<>("Vehicles", verticalThird,
				new String[] { "ID", "Road", "Location", "Speed", "Km",
						"Faulty Units", "Itinerary" });
		roadsTable = new InfoTablePanel<>("Roads", verticalThird, new String[] {
				"ID", "Source", "Target", "Length", "Max Speed", "Vehicles" });
		junctionsTable = new InfoTablePanel<>("Junctions", verticalThird,
				new String[] { "ID", "Green", "Red" });
		roadMap = new GraphComponent();

		JSplitPane topLeftSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
				eventsEditor, eventsQueue, .5);
		JSplitPane topRightSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
				topLeftSplit, reportsArea, .66);

		JSplitPane bottomLeftTopSplit = createSeparator(
				JSplitPane.VERTICAL_SPLIT, vehiclesTable, roadsTable, .5);
		JSplitPane bottomLeftSplit = createSeparator(JSplitPane.VERTICAL_SPLIT,
				bottomLeftTopSplit, junctionsTable, .66);

		JSplitPane bottomSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
				bottomLeftSplit, roadMap, .5);

		JSplitPane main = createSeparator(JSplitPane.VERTICAL_SPLIT,
				topRightSplit, bottomSplit, .4);

		add(main);
	}

	private void addListeners() {

	}

	private JSplitPane createSeparator(int orientation, Component first,
			Component second, double weight) {
		JSplitPane splitPane = new JSplitPane(orientation, first, second);
		splitPane.setDividerSize(5);
		splitPane.setVisible(true);
		splitPane.setResizeWeight(weight);
		splitPane.setContinuousLayout(true);
		return splitPane;
	}

	private void loadEvents() {
		// TODO: apunta adónde?
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
		int value = chooser.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			try {
				eventsEditor.writeFromFile(chooser.getSelectedFile());
			} catch (IOException ignored) {
				// TODO: hacer algo con las excecpciones
			}
		}
	}

	private void saveEvents() {
		// TODO: revisar
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
		int value = chooser.showSaveDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			try {
				eventsEditor.saveToFile(chooser.getSelectedFile());
			} catch (IOException e) {
				// TODO
			}
		}
	}

	private void readEvents() {
		try {
			String text = eventsEditor.getText();
			InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
			controller.loadEvents(is);
			List<Event> events = controller.getLoadedEvents();
			eventsQueue.setElements(events);
		} catch (IOException ignored) {
			// TODO: hacer algo con las excecpciones
		} catch (IllegalStateException e) {
			System.out.println("ERROR!");
		}
	}

	// TODO: borrar
	private void addComponentToToolBar(JComponent bar, JComponent... elements) {
		for (JComponent c : elements) {
			bar.add(c);
		}
	}

	private void addActionToToolBar(JToolBar bar, Action... actions) {
		for (Action a : actions) {
			bar.add(a);
		}
	}

	// TODO: try-catch
	public static void main(String... args) {
		new SimulatorWindow("Traffic Simulator", WINDOW_SIZE);
	}

}
