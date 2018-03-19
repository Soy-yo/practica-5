package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.Vehicle;

public class NewVehicleEvent extends Event {

  protected static final String SECTION_TAG_NAME = "new_vehicle";

  protected int maxSpeed;
  protected String[] itinerary;

  NewVehicleEvent(int time, String id, int maxSpeed, String[] itinerary) {
    super(time, id);
    this.maxSpeed = maxSpeed;
    this.itinerary = itinerary;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Vehicle(id, maxSpeed, simulator.getPath(itinerary)));
  }

  static class Builder implements Event.Builder {

    @Override
    public Event parse(IniSection section) {

      if (!section.getTag().equals(SECTION_TAG_NAME) || !matchesType(section)) {
        return null;
      }

      int time = parsePositiveInt(section, "time", 0);

      String id = getId(section);

      int maxSpeed = parsePositiveInt(section, "max_speed");

      String[] itinerary = parseIdList(section, "itinerary", 2);

      return parseType(section, time, id, maxSpeed, itinerary);
    }

    public NewVehicleEvent parseType(IniSection section, int time, String id, int maxSpeed,
                                     String[] itinerary) {
      return new NewVehicleEvent(time, id, maxSpeed, itinerary);
    }

  }

}
