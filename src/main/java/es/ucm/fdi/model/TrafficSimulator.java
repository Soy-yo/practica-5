package es.ucm.fdi.model;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.excepcions.SimulatorError;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class TrafficSimulator {

  private int currentTime;
  private MultiTreeMap<Integer, Event> events;
  private RoadMap roadMap;
  private List<Listener> listeners;

  public TrafficSimulator() {
    reset();
  }

  public void reset() {
    currentTime = 0;
    events = new MultiTreeMap<>();
    roadMap = new RoadMap();
    if (listeners != null) {
      fireEventUpdater(EventType.RESET, null);
    }
    listeners = new ArrayList<>();
  }

  public void addEvent(Event event) {
    if (event.getTime() < currentTime) {
      String msg = "Event " + event.getId() + " is breaking the space-time continuum";
      fireEventUpdater(EventType.ERROR, msg);
      throw new IllegalStateException(msg);
    }
    events.putValue(event.getTime(), event);
    fireEventUpdater(EventType.NEW_EVENT, null);
  }

  public void addSimulatedObject(SimulatedObject o) {
    roadMap.addSimulatedObject(o);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
    // Evento registrado
    EventUpdater updater = new EventUpdater(EventType.REGISTERED);
    SwingUtilities.invokeLater(() -> listener.update(updater, null));
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  private void fireEventUpdater(EventType type, String error) {
    EventUpdater updater = new EventUpdater(type);
    for (Listener l : listeners) {
      SwingUtilities.invokeLater(() -> l.update(updater, error));
    }
  }

  // Devuelve una cola de cruces a partir de sus ids si todos existen y hay alguna carretera que
  // los une
  public Queue<Junction> getPath(String[] junctions) {
    return roadMap.getPath(junctions);
  }

  public void makeVehicleFaulty(String id, int time) {
    Vehicle v = roadMap.vehicleSearch(id);
    if (v != null) {
      v.setFaulty(time);
    } else {
      String msg = "Vehicle " + id + " not found";
      fireEventUpdater(EventType.ERROR, msg);
      throw new IllegalArgumentException(msg);
    }
  }

  private void writeSimulatedObjectsReports(OutputStream out,
                                            List<? extends SimulatedObject> objects) {
    for (SimulatedObject o : objects) {
      try {
        writeReport(o.generateReport(currentTime), out);
      } catch (SimulatorError e) {
        String msg = "Something went wrong while writing " + o + "'s report";
        fireEventUpdater(EventType.ERROR, msg);
        throw new SimulatorError(msg, e);
      }
    }
  }

  private void writeReport(Map<String, String> report, OutputStream out) {
    Ini ini = new Ini();
    IniSection sec = new IniSection(report.get(""));
    // Elimina la cabecera: ya no se necesita
    report.remove("");
    for (Map.Entry<String, String> entry : report.entrySet()) {
      sec.setValue(entry.getKey(), entry.getValue());
    }
    ini.addSection(sec);
    try {
      ini.store(out);
    } catch (IOException e) {
      throw new SimulatorError("Failed while storing data on ini file", e);
    }
  }

  public void execute(int simulationSteps, OutputStream out) {
    int timeLimit = currentTime + simulationSteps - 1;
    while (currentTime <= timeLimit) {
      if (events.containsKey(currentTime)) {
        // Ejecuta todos los eventos de este paso
        for (Event e : events.get(currentTime)) {
          try {
            e.execute(this);
          } catch (IllegalArgumentException ex) {
            String msg = "Something went wrong while executing event " + e;
            fireEventUpdater(EventType.ERROR, msg);
            throw new SimulatorError(msg, ex);
          }
        }
      }
      for (Road r : roadMap.getRoads()) {
        r.advance();
      }
      for (Junction j : roadMap.getJunctions()) {
        j.advance();
      }
      currentTime++;
      fireEventUpdater(EventType.ADVANCED, null);
      if (out != null) {
        writeSimulatedObjectsReports(out, roadMap.getJunctions());
        writeSimulatedObjectsReports(out, roadMap.getRoads());
        writeSimulatedObjectsReports(out, roadMap.getVehicles());
      }
    }
  }

  public interface Listener {
    // TODO: utilizar esta opción o la otra? hay que hacer switch en algún lado en cualquier caso?
    void update(EventUpdater updater, String error);
  }

  public enum EventType {
    REGISTERED, RESET, NEW_EVENT, ADVANCED, ERROR
  }

  public class EventUpdater {

    private EventType type;

    private EventUpdater(EventType type) {
      this.type = type;
    }

    public EventType getEvent() {
      return type;
    }

    public List<Vehicle> getVehicles() {
      return roadMap.getVehicles();
    }

    public List<Road> getRoads() {
      return roadMap.getRoads();
    }

    public List<Junction> getJunctions() {
      return roadMap.getJunctions();
    }

    public List<Event> getEventQueue() {
      return events.valuesList();
    }

    public int getCurrentTime() {
      return currentTime;
    }

  }

}
