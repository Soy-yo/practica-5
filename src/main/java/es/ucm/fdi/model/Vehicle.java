package es.ucm.fdi.model;

import java.util.*;

public class Vehicle extends SimulatedObject {

  private static final String SECTION_TAG_NAME = "vehicle_report";
  public static final String[] INFO = { "ID", "Road", "Location", "Speed", "Km",
		"Faulty Units", "Itinerary" };

  protected int maxSpeed;
  protected int currentSpeed;
  protected int faulty;
  protected int kilometrage;
  private Road road;
  private int location;
  private Queue<Junction> itinerary;
  private boolean hasArrived;
  private boolean inJunction;

  public Vehicle(String id, int maxSpeed, Queue<Junction> itinerary) {
    super(id);
    this.maxSpeed = maxSpeed;
    currentSpeed = 0;
    location = 0;
    this.itinerary = itinerary;
    faulty = 0;
    hasArrived = false;
    inJunction = false;
    kilometrage = 0;
  }

  public int getLocation() {
    return location;
  }

  public Road getRoad() {
    return road;
  }

  public void setCurrentSpeed(int currentSpeed) {
    if (faulty == 0 && !inJunction) {
      this.currentSpeed = Math.min(currentSpeed, maxSpeed);
    }
  }

  public int getFaulty() {
    return faulty;
  }

  public void setFaulty(int faulty) {
    this.faulty += faulty;
    currentSpeed = 0;
  }

  public List<Junction> getItinerary() {
    return Collections.unmodifiableList(new ArrayList<>(itinerary));
  }

  @Override
  public void advance() {
    if (faulty > 0) {
      faulty--;
    } else if (!inJunction) {
      int newLocation = location + currentSpeed;
      if (newLocation >= road.getLength()) {
        newLocation = road.getLength();
        Junction nextJunction = itinerary.peek();
        nextJunction.vehicleIn(this);
        currentSpeed = 0;
        inJunction = true;
      }
      kilometrage += newLocation - location;
      location = newLocation;
    }
  }

  public void moveToNextRoad() {
    if (road != null) {
      road.vehicleOut(this);
    }
    if (!hasArrived) {
      String actual = itinerary.poll().getId();
      Junction next = itinerary.peek();
      if (next == null) {
        hasArrived = true;
        currentSpeed = 0;
        road.vehicleOut(this);
      } else {
        road = next.getStraightRoad(actual);
        location = 0;
        road.vehicleIn(this);
      }
      inJunction = false;
    }
  }

  @Override
  public void fillReportDetails(Map<String, String> kvps) {
    kvps.put("speed", "" + currentSpeed);
    kvps.put("kilometrage", "" + kilometrage);
    kvps.put("faulty", "" + faulty);
    kvps.put("location", hasArrived ? "arrived" : "(" + road + "," + location + ")");
  }

  @Override
  public Map<String, String> describe() {
	  Map<String, String> result = new HashMap<>();
	    result.put(INFO[0], super.id);
	    result.put(INFO[1], road.id);
	    result.put(INFO[2], "" + location);
	    result.put(INFO[3], "" + currentSpeed);
	    result.put(INFO[4], "" + kilometrage);
	    result.put(INFO[5], "" + faulty);
	    result.put(INFO[6], String.join(",", getItineraryIds()));
	    return result;
  }                                                                                     
  
  @Override
  protected String getReportHeader() {
    return SECTION_TAG_NAME;
  }
  
  private String[] getItineraryIds() {
	  String[] ids = new String[itinerary.size()];
	  int i = 0;
	  for(Junction j : itinerary) {
		  ids[i] = j.getId();
		  i++;
	  }
	  return ids;
  }

}
