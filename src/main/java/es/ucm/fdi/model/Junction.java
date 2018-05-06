package es.ucm.fdi.model;

import java.util.*;

import static java.util.stream.Collectors.joining;

public class Junction extends SimulatedObject {

  private static final String SECTION_TAG_NAME = "junction_report";
  public static final String[] INFO = {"ID", "Green", "Red"};

  protected Map<Road, IncomingRoad> incomingRoads;
  protected IncomingRoad currentRoadOn; // carretera con semáforo en verde actualmente
  protected Iterator<IncomingRoad> nextRoad; // siguiente carretera a la del semáforo en verde

  public Junction(String id) {
    super(id);
    incomingRoads = new LinkedHashMap<>();
  }

  public void addRoad(Road road) {
    incomingRoads.put(road, new IncomingRoad(road));
  }

  public void vehicleIn(Vehicle vehicle) {
    incomingRoads.get(vehicle.getRoad()).vehicleIn(vehicle);
  }

  public Road getGreenRoad() {
    return currentRoadOn == null ? null : currentRoadOn.road;
  }

  @Override
  public void advance() {
    if (!incomingRoads.isEmpty()) {
      if (currentRoadOn != null && !currentRoadOn.isEmpty()) {
        currentRoadOn.vehicleOut();
      }
      switchLights();
    }
  }

  protected void switchLights() {
    IncomingRoad previous = currentRoadOn;
    currentRoadOn = getNextRoad();
    if (previous != null) {
      previous.switchLight();
    }
    currentRoadOn.switchLight();
  }

  protected IncomingRoad getNextRoad() {
    // Reinicia el iterador cada vuelta
    if (nextRoad == null || !nextRoad.hasNext()) {
      nextRoad = incomingRoads.values().iterator();
    }
    return nextRoad.next();
  }

  /**
   * Devuelve la carretera que une el cruce con id previousJunction con esta
   * (si existe)
   */
  public Road getStraightRoad(String previousJunction) {
    for (Road r : incomingRoads.keySet()) {
      if (r.getSource().equals(previousJunction)) {
        return r;
      }
    }
    return null;
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    kvps.put("queues", incomingRoads.entrySet().stream()
        .map(e -> "(" + e.getKey() + "," + e.getValue().lightColor() + ",["
            + e.getValue().vehicleList.stream()
            .map(Vehicle::toString)
            .collect(joining(",")) + "])")
        .collect(joining(",")));
  }

  @Override
  protected String getReportHeader() {
    return SECTION_TAG_NAME;
  }

  @Override
  public Map<String, String> describe() {
    Map<String, String> result = new HashMap<>();
    result.put(INFO[0], id);
    result.put(INFO[1], currentRoadOn == null ? "[]" : "[(" + currentRoadOn.road + "," +
        currentRoadOn.lightColor() + ",[" + currentRoadOn.vehicleList.stream()
        .map(Vehicle::toString)
        .collect(joining(","))
        + "])]");
    result.put(INFO[2], "[" + incomingRoads.entrySet().stream()
        .filter(r -> r.getValue() != currentRoadOn)
        .map(r -> "(" + r.getKey() + ",red,[" + (r.getValue().vehicleList.stream()
            .map(Vehicle::toString)
            .collect(joining(",")) + "])"))
        .collect(joining(",")) + "]");
    return result;
  }

  protected class IncomingRoad {

    Road road;
    Queue<Vehicle> vehicleList;
    boolean greenLight;

    public IncomingRoad(Road road) {
      this.road = road;
      this.vehicleList = new ArrayDeque<>();
      greenLight = false;
    }

    void vehicleIn(Vehicle vehicle) {
      vehicleList.add(vehicle);
    }

    void vehicleOut() {
      Vehicle vehicle = vehicleList.poll();
      vehicle.moveToNextRoad();
    }

    int vehicleCount() {
      return vehicleList.size();
    }

    boolean isEmpty() {
      return vehicleList.size() == 0;
    }

    void switchLight() {
      greenLight = !greenLight;
    }

    boolean hasGreenLight() {
      return greenLight;
    }

    String lightColor() {
      return greenLight ? "green" : "red";
    }

    Iterable<Vehicle> vehicles() {
      return () -> vehicleList.iterator();
    }

  }

}
