package es.ucm.fdi.control;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.events.EventBuilder;
import es.ucm.fdi.extra.graphlayout.GraphComponent;
import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.Vehicle;
import es.ucm.fdi.util.TextAreaOutputStream;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
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
  private static final Dimension HORIZONTAL_THIRD = new Dimension(WINDOW_SIZE.width / 6,
      WINDOW_SIZE.height / 8);
  private static final Dimension VERTICAL_THIRD = new Dimension(WINDOW_SIZE.width / 3,
      WINDOW_SIZE.height / 8);
  private static final String READ_FILE_ERROR = "Error reading file";
  private static final String WRITE_FILE_ERROR = "Error writing file";

	private Controller controller;

  private SimulatorTextArea eventsEditor;
  private JScrollPane eventsEditorScroll;
  private SimulatorTable<Event> eventsQueue;
  private SimulatorTextArea reportsArea;
  private SimulatorTable<Vehicle> vehiclesTable;
  private SimulatorTable<Road> roadsTable;
  private SimulatorTable<Junction> junctionsTable;
	private GraphComponent roadMap;
	private JLabel statusBarText;

	private Map<Command, SimulatorAction> actionMap;

	private JSpinner stepCounter;
	private JTextField time;

  private String previousPath;

  public SimulatorWindow(String title, File initialFile, int steps, Dimension dimension) {
		super(title);
		controller = new Controller(new TrafficSimulator());
		initialize(dimension, initialFile, steps);
    previousPath = initialFile.getPath();
	}

	private void initialize(Dimension dimension, File initialFile, int steps) {
		setSize(dimension);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    generateActionMap();
    addSections(initialFile);
		addToolBar(steps);
		addStatusBar();
		addListeners();
		setVisible(true);
	}

	private void generateActionMap() {
		actionMap = new HashMap<>();
    actionMap.put(Command.LOAD_EVENTS,
        new SimulatorAction(Command.LOAD_EVENTS, this::loadEvents));
    actionMap.put(Command.SAVE_EVENTS,
        new SimulatorAction(Command.SAVE_EVENTS, this::saveEvents));
    actionMap.put(Command.CLEAR_EVENTS,
        new SimulatorAction(Command.CLEAR_EVENTS, this::clearEvents));
    actionMap.put(Command.MOVE_EVENTS,
        new SimulatorAction(Command.MOVE_EVENTS, this::readEvents));
    actionMap.put(Command.RUN,
        new SimulatorAction(Command.RUN, this::run));
    actionMap.put(Command.RESET,
        new SimulatorAction(Command.RESET, this::reset));
    actionMap.put(Command.GENERATE_REPORT,
        new SimulatorAction(Command.GENERATE_REPORT, this::generateReports));
    actionMap.put(Command.DELETE_REPORT,
        new SimulatorAction(Command.DELETE_REPORT, this::deleteReports));
    actionMap.put(Command.SAVE_REPORT,
        new SimulatorAction(Command.SAVE_REPORT, this::saveReport));
    actionMap.put(Command.EXIT,
        new SimulatorAction(Command.EXIT, () -> System.exit(0)));
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
        controller.setOutputStream(new TextAreaOutputStream(reportsArea));
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

    eventsEditorScroll = new JScrollPane();
    eventsEditorScroll.setMinimumSize(HORIZONTAL_THIRD);
    eventsEditorScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), initialFile == null ||
        !initialFile.exists() ? "Events editor" : "Events: " + initialFile.getName()));
    eventsEditor = new SimulatorTextArea(true);
    if (initialFile != null) {
      try {
        eventsEditor.writeFromFile(initialFile);
      } catch (IOException e) {
        showErrorMessage(READ_FILE_ERROR, e.getMessage());
      }
    }
    eventsEditor.setComponentPopupMenu(createTemplatePopupMenu(eventsEditor));
    eventsEditorScroll.setViewportView(eventsEditor);

    JScrollPane eventsQueueScroll = new JScrollPane();
    eventsQueueScroll.setMinimumSize(HORIZONTAL_THIRD);
    eventsQueueScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Events Queue"));
    eventsQueue = new SimulatorTable<>(Event.INFO);
    eventsQueueScroll.setViewportView(eventsQueue);

    JScrollPane reportsAreaScroll = new JScrollPane();
    reportsAreaScroll.setMinimumSize(HORIZONTAL_THIRD);
    reportsAreaScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Reports"));
    reportsArea = new SimulatorTextArea(false);
    reportsArea.setDisabledTextColor(Color.BLACK);
    reportsAreaScroll.setViewportView(reportsArea);

    JScrollPane vehiclesScroll = new JScrollPane();
    vehiclesScroll.setMinimumSize(VERTICAL_THIRD);
    vehiclesScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Vehicles"));
    vehiclesTable = new SimulatorTable<>(Vehicle.INFO);
    vehiclesScroll.setViewportView(vehiclesTable);

    JScrollPane roadsScroll = new JScrollPane();
    roadsScroll.setMinimumSize(VERTICAL_THIRD);
    roadsScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Roads"));
    roadsTable = new SimulatorTable<>(Road.INFO);
    roadsScroll.setViewportView(roadsTable);

    JScrollPane junctionsScroll = new JScrollPane();
    junctionsScroll.setMinimumSize(VERTICAL_THIRD);
    junctionsScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Junctions"));
    junctionsTable = new SimulatorTable<>(Junction.INFO);
    junctionsScroll.setViewportView(junctionsTable);

		roadMap = new GraphComponent();

		JSplitPane topLeftSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        eventsEditorScroll, eventsQueueScroll, .5);
		JSplitPane topRightSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
        topLeftSplit, reportsAreaScroll, .66);

		JSplitPane bottomLeftTopSplit = createSeparator(
        JSplitPane.VERTICAL_SPLIT, vehiclesScroll, roadsScroll, .5);
		JSplitPane bottomLeftSplit = createSeparator(JSplitPane.VERTICAL_SPLIT,
        bottomLeftTopSplit, junctionsScroll, .66);

		JSplitPane bottomSplit = createSeparator(JSplitPane.HORIZONTAL_SPLIT,
				bottomLeftSplit, roadMap, .5);

		JSplitPane main = createSeparator(JSplitPane.VERTICAL_SPLIT,
				topRightSplit, bottomSplit, .4);
		
		add(main);

	}

  private JPopupMenu createTemplatePopupMenu(JTextArea textArea) {
    JPopupMenu popup = new JPopupMenu();
    JMenu menu = new JMenu("Add template");
    menu.setEnabled(true);
    // Aprovecha los Event.Builder
    for (Event.Builder builder : EventBuilder.SUPPORTED_EVENTS) {
      JMenuItem item = new JMenuItem(builder.getEventName());
      item.addActionListener(e ->
          textArea.insert("\n" + builder.getEventFileTemplate(), textArea.getCaretPosition()));
      menu.add(item);
    }
    popup.add(menu);
    popup.add(actionMap.get(Command.LOAD_EVENTS));
    popup.add(actionMap.get(Command.SAVE_EVENTS));
    popup.add(actionMap.get(Command.CLEAR_EVENTS));
    popup.addSeparator();
    return popup;
  }

	private void addStatusBar() {
		JPanel statusBar = new JPanel();
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBarText = new JLabel("Welcome to the simulator!");
		statusBar.add(statusBarText);
		add(statusBar, BorderLayout.SOUTH);

	}
	
	private void setStatusText(String text) {
    statusBarText.setText(text);
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
        setStatusText("Simulator has just been reset!");
			}

			@Override
			public void newEvent(TrafficSimulator.UpdateEvent ue) {
        List<Event> events = ue.getEventQueue();
        if (!events.isEmpty()) {
          eventsQueue.setElements(events);
          actionMap.get(Command.RUN).setEnabled(true);
        }
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
        setStatusText("An error occurred!!");
        showErrorMessage("Simulator error", msg);
        SimulatorWindow.this.reset();
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
    JFileChooser chooser = new JFileChooser(previousPath);
		chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      previousPath = file.getPath();
			try {
        eventsEditor.writeFromFile(file);
        TitledBorder border = (TitledBorder) eventsEditorScroll.getBorder();
        border.setTitle("Events: " + file.getName());
        eventsEditorScroll.repaint();
      } catch (IOException e) {
        showErrorMessage(READ_FILE_ERROR, e.getMessage());
			}
		}
	}

	private void saveEvents() {
    JFileChooser chooser = new JFileChooser(previousPath);
		chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      previousPath = file.getPath();
			try {
        eventsEditor.saveToFile(file);
        TitledBorder border = (TitledBorder) eventsEditorScroll.getBorder();
        border.setTitle("Events: " + file.getName());
        eventsEditorScroll.repaint();
			} catch (IOException e) {
        showErrorMessage(WRITE_FILE_ERROR, e.getMessage());
			}
		}
	}

  private void clearEvents() {
    eventsEditor.clear();
  }

	private void readEvents() {
		try {
			String text = eventsEditor.getText();
			InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
			TrafficSimulator simulator = controller.getSimulator();
			simulator.clearEvents();
			controller.loadEvents(is);
    } catch (IOException e) {
      showErrorMessage(READ_FILE_ERROR, e.getMessage());
		} catch (IllegalStateException e) {
      showErrorMessage("Error reading events", e.getMessage());
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
        showErrorMessage(WRITE_FILE_ERROR, e.getMessage());
			}
		}
	}

	private void generateReports() {
    SimulatedObjectDialog dialog = new SimulatedObjectDialog(this, "Generate reports");
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
          new TextAreaOutputStream(reportsArea), junctions, roads, vehicles);
			actionMap.get(Command.DELETE_REPORT).setEnabled(true);
			actionMap.get(Command.SAVE_REPORT).setEnabled(true);
		}
	}

	private void deleteReports() {
		reportsArea.clear();
		actionMap.get(Command.DELETE_REPORT).setEnabled(false);
		actionMap.get(Command.SAVE_REPORT).setEnabled(false);
	}

  private void showErrorMessage(String title, String msg) {
    JOptionPane.showMessageDialog(SimulatorWindow.this, msg, title,
        JOptionPane.ERROR_MESSAGE);
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

  private void refreshTables(Collection<Vehicle> vehicles, Collection<Road> roads,
                             Collection<Junction> junctions) {
		vehiclesTable.setElements(vehicles);
		roadsTable.setElements(roads);
		junctionsTable.setElements(junctions);
	}

}
