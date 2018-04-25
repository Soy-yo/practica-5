package es.ucm.fdi.model;

import java.util.*;

public class RoadMap {

  private Map<String, Vehicle> vehicles;
  private Map<String, Road> roads;
  private Map<String, Junction> junctions;

  private Collection<Vehicle> unmodifiableVehicleList;
  private Collection<Road> unmodifiableRoadList;
  private Collection<Junction> unmodifiableJunctionList;

  public RoadMap() {
    reset();
  }

  public void reset() {
    vehicles = new LinkedHashMap<>();
    unmodifiableVehicleList = Collections.unmodifiableCollection(vehicles.values());
    roads = new LinkedHashMap<>();
    unmodifiableRoadList = Collections.unmodifiableCollection(roads.values());
    junctions = new LinkedHashMap<>();
    unmodifiableJunctionList = Collections.unmodifiableCollection(junctions.values());
  }

  public boolean contains(String id) {
    return vehicles.containsKey(id) || roads.containsKey(id) || junctions.containsKey(id);
  }

  public void addSimulatedObject(SimulatedObject o) {
    if (contains(o.getId())) {
      throw new IllegalArgumentException("Object " + o.getId() + " is already registered");
    }
    if (o instanceof Vehicle) {
      addVehicle((Vehicle) o);
    } else if (o instanceof Road) {
      addRoad((Road) o);
    } else if (o instanceof Junction) {
      addJunction((Junction) o);
    }
  }

  public void addVehicle(Vehicle v) {
    List<Junction> itinerary = v.getItinerary();
    // Comprueba que el itinerario del vehículo es posible
    for (Junction j : itinerary) {
      if (!junctions.containsKey(j.getId())) {
        throw new IllegalArgumentException("Junction " + j + " in vehicle's " + v
            + " itinerary does not exists in the road map");
      }
    }
    v.moveToNextRoad();
    vehicles.put(v.getId(), v);
  }

  public void addRoad(Road r) {
    Junction destination = junctionSearch(r.getDestiny());
    // Comprueba que tanto el origen como el destino existen en el mapa
    if (!junctions.containsKey(r.getSource())) {
      throw new IllegalArgumentException("Couldn't find source for road " + r.getId());
    }
    if (destination == null) {
      throw new IllegalArgumentException("Couldn't find destination for road " + r.getId());
    }
    destination.addRoad(r);
    roads.put(r.getId(), r);
  }

  public void addJunction(Junction j) {
    junctions.put(j.getId(), j);
  }

  public SimulatedObject searchById(String id) {
    if (vehicles.containsKey(id)) {
      return vehicles.get(id);
    }
    if (roads.containsKey(id)) {
      return roads.get(id);
    }
    if (junctions.containsKey(id)) {
      return junctions.get(id);
    }
    return null;
  }

  public Vehicle vehicleSearch(String id) {
    return vehicles.get(id);
  }

  public Road roadSearch(String id) {
    return roads.get(id);
  }

  public Junction junctionSearch(String id) {
    return junctions.get(id);
  }

  public Collection<Vehicle> getVehicles() {
    return unmodifiableVehicleList;
  }

  public Collection<Road> getRoads() {
    return unmodifiableRoadList;
  }

  public Collection<Junction> getJunctions() {
    return unmodifiableJunctionList;
  }

  // Devuelve una cola de cruces a partir de sus ids si todos existen y hay alguna carretera que
  // los une
  public Queue<Junction> getPath(String[] path) {
    Queue<Junction> result = new ArrayDeque<>();
    String previousJunctionId = null;
    for (String id : path) {
      Junction j = junctionSearch(id);
      if (j == null) {
        throw new IllegalArgumentException("Junction " + id + " does not exit in road map");
      }
      if (previousJunctionId != null && j.getStraightRoad(previousJunctionId) == null) {
        throw new IllegalArgumentException("No road connects " + previousJunctionId + " and " + id);
      }
      result.add(j);
      previousJunctionId = id;
    }
    return result;
  }

}
