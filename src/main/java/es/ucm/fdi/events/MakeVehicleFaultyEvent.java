package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;

public class MakeVehicleFaultyEvent extends Event {

  private static final String SECTION_TAG_NAME = "make_vehicle_faulty";

  private String[] vehicles;
  private int duration;

  MakeVehicleFaultyEvent(int time, String id, String[] vehicles, int duration) {
    super(time, id);
    this.vehicles = vehicles;
    this.duration = duration;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    for (String id : vehicles) {
      simulator.makeVehicleFaulty(id, duration);
    }
  }

  @Override
  public String toString() {
    return "Break vehicles [" + String.join(",", vehicles) + "]";
  }

  static class Builder implements Event.Builder {

    @Override
    public Event parse(IniSection section) {

      if (!section.getTag().equals(SECTION_TAG_NAME)) {
        return null;
      }

      int time = parsePositiveInt(section, "time", 0);

      String[] vehicles = parseIdList(section, "vehicles", 1);

      int duration = parsePositiveInt(section, "duration");

      return new MakeVehicleFaultyEvent(time, "", vehicles, duration);
    }

    @Override
    public String getEventName() {
      return "Make Vehicle Faulty";
    }

    @Override
    public String getEventFileTemplate() {
      return "[" + SECTION_TAG_NAME + "]\n" +
          "time=\n" +
          "vehicles=\n" +
          "duration=\n";
    }

  }

}
