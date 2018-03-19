package es.ucm.fdi.model;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.excepcions.SimulatorError;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class TrafficSimulator {

  private int currentTime;
  private MultiTreeMap<Integer, Event> events;
  private RoadMap roadMap;

  public TrafficSimulator() {
    reset();
  }

  public void reset() {
    currentTime = 0;
    events = new MultiTreeMap<>();
    roadMap = new RoadMap();
  }

  public void addEvent(Event event) {
    if (event.getTime() < currentTime) {
      throw new IllegalStateException(
          "Event " + event.getId() + " is breaking the space-time continuum");
    }
    events.putValue(event.getTime(), event);
  }

  public void addSimulatedObject(SimulatedObject o) {
    roadMap.addSimulatedObject(o);
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
      throw new IllegalArgumentException("Vehicle " + id + " not found");
    }
  }

  private void writeSimulatedObjectsReports(OutputStream out,
                                            List<? extends SimulatedObject> objects) {
    for (SimulatedObject o : objects) {
      try {
        writeReport(o.generateReport(currentTime), out);
      } catch (SimulatorError e) {
        throw new SimulatorError("Something went wrong while writing " + o + "'s report", e);
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
            throw new SimulatorError("Something went wrong while executing event " + e, ex);
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
      if (out != null) {
        writeSimulatedObjectsReports(out, roadMap.getJunctions());
        writeSimulatedObjectsReports(out, roadMap.getRoads());
        writeSimulatedObjectsReports(out, roadMap.getVehicles());
      }
    }
  }

}
