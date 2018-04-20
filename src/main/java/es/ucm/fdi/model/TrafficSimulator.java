package es.ucm.fdi.model;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.excepcions.SimulatorError;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

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
      fireUpdateEvent(EventType.RESET, null);
    }
    listeners = new ArrayList<>();
  }

  public void addEvent(Event event) {
    if (event.getTime() < currentTime) {
      String msg = "Event " + event.getId() + " is breaking the space-time continuum";
      fireUpdateEvent(EventType.ERROR, msg);
      throw new IllegalStateException(msg);
    }
    events.putValue(event.getTime(), event);
    fireUpdateEvent(EventType.NEW_EVENT, null);
  }

  public List<Event> getEvents() {
    return Collections.unmodifiableList(events.valuesList());
  }

  public void addSimulatedObject(SimulatedObject o) {
    roadMap.addSimulatedObject(o);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
    // Evento registrado
    UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
    SwingUtilities.invokeLater(() -> listener.registered(ue));
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  private void fireUpdateEvent(EventType type, String error) {
    UpdateEvent ue = new UpdateEvent(type);
    switch (type) {
      case REGISTERED:
        for (Listener l : listeners) {
          SwingUtilities.invokeLater(() -> l.registered(ue));
        }
        break;
      case RESET:
        for (Listener l : listeners) {
          SwingUtilities.invokeLater(() -> l.reset(ue));
        }
        break;
      case NEW_EVENT:
        for (Listener l : listeners) {
          SwingUtilities.invokeLater(() -> l.newEvent(ue));
        }
        break;
      case ADVANCED:
        for (Listener l : listeners) {
          SwingUtilities.invokeLater(() -> l.advanced(ue));
        }
        break;
      case ERROR:
        for (Listener l : listeners) {
          SwingUtilities.invokeLater(() -> l.error(ue, error));
        }
        break;
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
      fireUpdateEvent(EventType.ERROR, msg);
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
        fireUpdateEvent(EventType.ERROR, msg);
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
            fireUpdateEvent(EventType.ERROR, msg);
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
      fireUpdateEvent(EventType.ADVANCED, null);
      if (out != null) {
        writeSimulatedObjectsReports(out, roadMap.getJunctions());
        writeSimulatedObjectsReports(out, roadMap.getRoads());
        writeSimulatedObjectsReports(out, roadMap.getVehicles());
      }
    }
  }

  public interface Listener {

    void registered(UpdateEvent ue);

    void reset(UpdateEvent ue);

    void newEvent(UpdateEvent ue);

    void advanced(UpdateEvent ue);

    void error(UpdateEvent ue, String msg);

  }

  public enum EventType {
    REGISTERED, RESET, NEW_EVENT, ADVANCED, ERROR
  }

  public class UpdateEvent {

    private EventType type;

    private UpdateEvent(EventType type) {
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
      return getEvents();
    }

    public int getCurrentTime() {
      return currentTime;
    }

  }

}
