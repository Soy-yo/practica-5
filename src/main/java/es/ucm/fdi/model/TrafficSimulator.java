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
  private List<Listener> listeners; // lista de listeners para comunicar
  // cambios de estado

  public TrafficSimulator() {
    listeners = new ArrayList<>();
    reset();
  }

  /**
   * Resetea el simulador y lo notifica
   */
  public void reset() {
    currentTime = 0;
    events = new MultiTreeMap<>();
    roadMap = new RoadMap();
    fireUpdateEvent(EventType.RESET, null);
  }

  /**
   * Añade un nuevo evento al simulador y lo notifica
   */
  public void addEvent(Event event) {
    events.putValue(event.getTime(), event);
    fireUpdateEvent(EventType.NEW_EVENT, null);
  }

  /**
   * Vacía los eventos del simulador
   */
  public void clearEvents() {
    events.clear();
  }

  public List<Event> getEvents() {
    return events.valuesList();
  }

  /**
   * Añade un nuevo objeto al roadMap
   */
  public void addSimulatedObject(SimulatedObject o) {
    roadMap.addSimulatedObject(o);
  }

  /**
   * Añade un nuevo observador a la lista
   */
  public void addListener(Listener listener) {
    listeners.add(listener);
    // Evento registrado
    UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
    SwingUtilities.invokeLater(() -> listener.registered(ue));
  }

  /**
   * Elimina un observador de la lista
   */
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

  public Collection<Vehicle> getVehicles() {
    return roadMap.getVehicles();
  }

  public Collection<Road> getRoads() {
    return roadMap.getRoads();
  }

  public Collection<Junction> getJunctions() {
    return roadMap.getJunctions();
  }

  public Set<Road> getGreenRoads() {
    return roadMap.getGreenRoads();
  }

  /**
   * Devuelve una cola de cruces a partir de sus ids si todos existen y hay
   * alguna carretera que los une
   */
  public Queue<Junction> getPath(String[] junctions) {
    return roadMap.getPath(junctions);
  }

  /**
   * Avería un vehículo
   */
  public void makeVehicleFaulty(String id, int time) {
    Vehicle v = roadMap.vehicleSearch(id);
    if (v != null) {
      v.setFaulty(time);
    } else {
      throw new IllegalArgumentException("Vehicle " + id + " not found");
    }
  }

  /**
   * Escribe los reports de todos los objetos en la salida que se le indica
   */
  public void generateReports(OutputStream out,
                              Collection<Junction> junctions, Collection<Road> roads,
                              Collection<Vehicle> vehicles) {
    if (out != null) {
      writeSimulatedObjectsReports(out, junctions);
      writeSimulatedObjectsReports(out, roads);
      writeSimulatedObjectsReports(out, vehicles);
    }
  }

  private void writeSimulatedObjectsReports(OutputStream out,
                                            Collection<? extends SimulatedObject> objects) {
    for (SimulatedObject o : objects) {
      try {
        writeReport(o.generateReport(currentTime), out);
      } catch (SimulatorError e) {
        fireUpdateEvent(EventType.ERROR,
            "Something went wrong while writing " + o + "'s report");
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

  /**
   * Ejecuta la simulación durante tantos pasos como se le indiquen
   */
  public void execute(int simulationSteps, OutputStream out) {
    int timeLimit = currentTime + simulationSteps - 1;
    while (currentTime <= timeLimit) {
      if (events.containsKey(currentTime)) {
        // Ejecuta todos los eventos de este paso
        for (Event e : events.get(currentTime)) {
          try {
            e.execute(this);
          } catch (IllegalArgumentException | IllegalStateException ex) {
            fireUpdateEvent(EventType.ERROR,
                "Something went wrong while executing event "
                    + e + "\n" + ex.getMessage());
            // Error occurred, can't continue at this point
            return;
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
      generateReports(out, roadMap.getJunctions(), roadMap.getRoads(),
          roadMap.getVehicles());
    }
  }

  /**
   * Interfaz para los observadores
   */
  public interface Listener {

    void registered(UpdateEvent ue);

    void reset(UpdateEvent ue);

    void newEvent(UpdateEvent ue);

    void advanced(UpdateEvent ue);

    void error(UpdateEvent ue, String msg);

  }

  /**
   * Enumerado para los tipos de eventos que pueden notificar los observadores
   */
  public enum EventType {
    REGISTERED, RESET, NEW_EVENT, ADVANCED, ERROR
  }

  /**
   * Clase que se envía con los observadores y que tiene la información de la
   * actualización
   */
  public class UpdateEvent {

    private EventType type;

    private UpdateEvent(EventType type) {
      this.type = type;
    }

    public EventType getEvent() {
      return type;
    }

    public Collection<Vehicle> getVehicles() {
      return roadMap.getVehicles();
    }

    public Collection<Road> getRoads() {
      return roadMap.getRoads();
    }

    public Collection<Junction> getJunctions() {
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
