package es.ucm.fdi.control;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.extra.graphlayout.GraphComponent;
import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.Vehicle;
import es.ucm.fdi.util.TextAreaOutputStream;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulatorWindow extends JFrame {

	public static Dimension WINDOW_SIZE = new Dimension(1000, 1000);

	private Controller controller;

	private EventsEditorPanel eventsEditor;
	private InfoTablePanel<Event> eventsQueue;
	private ReportsAreaPanel reportsArea;
	private InfoTablePanel<Vehicle> vehiclesTable;
	private InfoTablePanel<Road> roadsTable;
	private InfoTablePanel<Junction> junctionsTable;
	private GraphComponent roadMap;
	private JLabel statusBarText;

	private Map<Command, SimulatorAction> actionMap;

	private JSpinner stepCounter;
	private JTextField time;

	public SimulatorWindow(String title, File initialFile, int steps,
			Dimension dimension) {
		super(title);
		controller = new Controller(new TrafficSimulator());
		initialize(dimension, initialFile, steps);
	}

	private void initialize(Dimension dimension, File initialFile, int steps) {
		setSize(dimension);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addSections(initialFile);
		generateActionMap();
		addToolBar(steps);
		addStatusBar();
		addListeners();
		setVisible(true);
	}

	private void generateActionMap() {
		actionMap = new HashMap<>();
		actionMap.put(Command.LOAD_EVENTS, new SimulatorAction(
				Command.LOAD_EVENTS, this::loadEvents));
		actionMap.put(Command.SAVE_EVENTS, new SimulatorAction(
				Command.SAVE_EVENTS, this::saveEvents));
		actionMap.put(Command.CLEAR_EVENTS, new SimulatorAction(
				Command.CLEAR_EVENTS, eventsEditor::clear));
		actionMap.put(Command.MOVE_EVENTS, new SimulatorAction(
				Command.MOVE_EVENTS, this::readEvents));
		actionMap.put(Command.RUN, new SimulatorAction(Command.RUN, this::run));
		actionMap.put(Command.RESET, new SimulatorAction(Command.RESET,
				this::reset));
		actionMap.put(Command.GENERATE_REPORT, new SimulatorAction(
				Command.GENERATE_REPORT, this::generateReports));
		actionMap.put(Command.DELETE_REPORT, new SimulatorAction(
				Command.DELETE_REPORT, this::deleteReports));
		actionMap.put(Command.SAVE_REPORT, new SimulatorAction(
				Command.SAVE_REPORT, this::saveReport));
		actionMap.put(Command.EXIT, new SimulatorAction(Command.EXIT,
				() -> System.exit(0)));
	}

	private void addToolBar(int initialSteps) {
		// Tool bar
		JToolBar bar = new JToolBar();

		stepCounter = new JSpinner(new SpinnerNumberModel(initialSteps, 0, 100,
				1));
		stepCounter.setMaximumSize(new Dimension(75, 30));

		time = new JTextField("0", 4);
		time.setMaximumSize(new Dimension(75, 30));
		time.setHorizontalAlignment(JTextField.RIGHT);
		time.setEnabled(false);
		time.setDisabledTextColor(UIManager.getColor("TextField.foreground"));

		addActionToToolBar(bar, Command.LOAD_EVENTS, Command.SAVE_EVENTS,
				Command.CLEAR_EVENTS, Command.MOVE_EVENTS, Command.RUN,
				Command.RESET);
		addComponentToToolBar(bar, new JLabel(" Steps: "), stepCounter,
				new JLabel(" Time: "), time);
		addActionToToolBar(bar, Command.GENERATE_REPORT, Command.DELETE_REPORT,
				Command.SAVE_REPORT, Command.EXIT);

		add(bar, BorderLayout.NORTH);

		JCheckBoxMenuItem redirectOutput = new JCheckBoxMenuItem(
				"Redirect output");
		redirectOutput.addItemListener(e -> {
			if (redirectOutput.isSelected()) {
				controller.setOutputStream(new TextAreaOutputStream(reportsArea
						.getArea()));
			} else {
				controller.setOutputStream(null);
			}
		});

		// Menu bar
		JMenuBar menu = new JMenuBar();

		JMenu file = new JMenu("File");

		file.add(actionMap.get(Command.LOAD_EVENTS));
		file.add(actionMap.get(Command.SAVE_EVENTS));
		file.addSeparator();
		file.add(actionMap.get(Command.SAVE_REPORT));
		file.addSeparator();
		file.add(actionMap.get(Command.EXIT));

		JMenu simulator = new JMenu("Simulator");

		simulator.add(actionMap.get(Command.RUN));
		simulator.add(actionMap.get(Command.RESET));
		simulator.addSeparator();
		simulator.add(actionMap.get(Command.MOVE_EVENTS));
		simulator.add(redirectOutput);

		JMenu reports = new JMenu("Reports");

		reports.add(actionMap.get(Command.GENERATE_REPORT));
		reports.add(actionMap.get(Command.DELETE_REPORT));

		menu.add(file);
		menu.add(simulator);
		menu.add(reports);

		setJMenuBar(menu);
	}

	private void addSections(File initialFile) {

		Dimension horizontalThird = new Dimension(WINDOW_SIZE.width / 6,
				WINDOW_SIZE.height / 8);
		Dimension verticalThird = new Dimension(WINDOW_SIZE.width / 3,
				WINDOW_SIZE.height / 8);

		eventsEditor = new EventsEditorPanel(horizontalThird, initialFile);
		eventsQueue = new InfoTablePanel<>("Events Queue", horizontalThird,
				Event.INFO);
		reportsArea = new ReportsAreaPanel(horizontalThird);
		vehiclesTable = new InfoTablePanel<>("Vehicles", verticalThird,
				Vehicle.INFO);
		roadsTable = new InfoTablePanel<>("Roads", verticalThird, Road.INFO);
		junctionsTable = new InfoTablePanel<>("Junctions", verticalThird,
				Junction.INFO);
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

	private void addStatusBar() {
		JPanel statusBar = new JPanel();
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBarText = new JLabel("Welcome to the simulator!");
		statusBar.add(statusBarText);
		add(statusBar, BorderLayout.SOUTH);

	}
	
	private void setStatusText(String text) {
		statusBarText.setText(text);;
	}

	private void addListeners() {
		controller.getSimulator().addListener(new TrafficSimulator.Listener() {
			@Override
			public void registered(TrafficSimulator.UpdateEvent ue) {
				actionMap.get(Command.RUN).setEnabled(false);
				actionMap.get(Command.RESET).setEnabled(false);
				actionMap.get(Command.GENERATE_REPORT).setEnabled(false);
				actionMap.get(Command.DELETE_REPORT).setEnabled(false);
				actionMap.get(Command.SAVE_REPORT).setEnabled(false);
			}

			@Override
			public void reset(TrafficSimulator.UpdateEvent ue) {
				time.setText("" + 0);
				actionMap.get(Command.MOVE_EVENTS).setEnabled(true);
				actionMap.get(Command.RESET).setEnabled(false);
				actionMap.get(Command.GENERATE_REPORT).setEnabled(false);
				refreshTables(ue.getVehicles(), ue.getRoads(),
						ue.getJunctions());
				roadMap.clear();
				setStatusText("Simulator has just been resetted!");
			}

			@Override
			public void newEvent(TrafficSimulator.UpdateEvent ue) {
				setStatusText("Events have been loaded to the simulator!");
			}

			@Override
			public void advanced(TrafficSimulator.UpdateEvent ue) {
				time.setText("" + ue.getCurrentTime());
				refreshTables(ue.getVehicles(), ue.getRoads(),
						ue.getJunctions());
				roadMap.generateGraph(ue.getVehicles(), ue.getRoads(), ue
						.getJunctions(), controller.getSimulator()
						.getGreenRoads());
				setStatusText("Simulator advanced " + stepCounter.getValue() + " steps!");
			}

			@Override
			public void error(TrafficSimulator.UpdateEvent ue, String msg) {
				setStatusText("An error ocurred!!");
			}
		});
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
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				eventsEditor.writeFromFile(chooser.getSelectedFile());
			} catch (IOException ignored) {
				// TODO: hacer algo con las excecpciones
			}
		}
	}

	private void saveEvents() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
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
			TrafficSimulator simulator = controller.getSimulator();
			simulator.clearEvents();
			controller.loadEvents(is);
			List<Event> events = simulator.getEvents();
			if (!events.isEmpty()) {
				eventsQueue.setElements(events);
				actionMap.get(Command.RUN).setEnabled(true);
			}
		} catch (IOException ignored) {
			// TODO: hacer algo con las excecpciones
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	private void run() {
		actionMap.get(Command.MOVE_EVENTS).setEnabled(false);
		actionMap.get(Command.RUN).setEnabled(false);
		actionMap.get(Command.RESET).setEnabled(true);
		actionMap.get(Command.GENERATE_REPORT).setEnabled(true);
		int ticks = (Integer) stepCounter.getValue();
		controller.run(ticks);
	}

	private void reset() {
		eventsQueue.clear();
		reportsArea.clear();
		controller.reset();
	}

	private void saveReport() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
		int value = chooser.showSaveDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			try {
				reportsArea.saveToFile(chooser.getSelectedFile());
			} catch (IOException e) {
				// TODO
			}
		}
	}

	private void generateReports() {
		SimulatedObjectDialog dialog = new SimulatedObjectDialog(this,
				"Generate reports");
		TrafficSimulator simulator = controller.getSimulator();
		dialog.setVehicles(simulator.getVehicles());
		dialog.setRoads(simulator.getRoads());
		dialog.setJunctions(simulator.getJunctions());
		if (dialog.open() == SimulatedObjectDialog.ACCEPTED) {
			Collection<Vehicle> vehicles = dialog.getSelectedVehicles();
			Collection<Road> roads = dialog.getSelectedRoads();
			Collection<Junction> junctions = dialog.getSelectedJunctions();
			reportsArea.clear();
			controller.getSimulator().generateReports(
					new TextAreaOutputStream(reportsArea.getArea()), junctions,
					roads, vehicles);
			actionMap.get(Command.DELETE_REPORT).setEnabled(true);
			actionMap.get(Command.SAVE_REPORT).setEnabled(true);
		}
	}

	private void deleteReports() {
		reportsArea.clear();
		actionMap.get(Command.DELETE_REPORT).setEnabled(false);
		actionMap.get(Command.SAVE_REPORT).setEnabled(false);
	}

	private void addComponentToToolBar(JComponent bar, JComponent... elements) {
		for (JComponent c : elements) {
			bar.add(c);
		}
	}

	private void addActionToToolBar(JToolBar bar, Command... commands) {
		for (Command c : commands) {
			bar.add(actionMap.get(c));
		}
	}

	private void refreshTables(Collection<Vehicle> vehicles,
			Collection<Road> roads, Collection<Junction> junctions) {
		vehiclesTable.setElements(vehicles);
		roadsTable.setElements(roads);
		junctionsTable.setElements(junctions);
	}

}
