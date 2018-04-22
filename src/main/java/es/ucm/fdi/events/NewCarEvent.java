package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Car;
import es.ucm.fdi.model.TrafficSimulator;

public class NewCarEvent extends NewVehicleEvent {

  private static final String FRIENDLY_CLASS_NAME = "New Car";

  private int resistance;
  private double faultProbability;
  private int maxFaultDuration;
  private long seed;

  NewCarEvent(int time, String id, int maxSpeed, String[] itinerary,
              int resistance, double faultProbability, int maxFaultDuration,
              long seed) {
    super(time, id, maxSpeed, itinerary);
    this.resistance = resistance;
    this.faultProbability = faultProbability;
    this.maxFaultDuration = maxFaultDuration;
    this.seed = seed;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Car(id, maxSpeed, simulator.getPath(itinerary),
        resistance, faultProbability, maxFaultDuration, seed));
  }

  @Override
  public String toString() {
    return FRIENDLY_CLASS_NAME + " " + id;
  }

  static class Builder extends NewVehicleEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return Car.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewVehicleEvent parseType(IniSection section, int time, String id, int maxSpeed,
                                     String[] itinerary) {

      int resistance = parsePositiveInt(section, "resistance");

      double faultProbability = parsePositiveDouble(section, "fault_probability", 1.0);

      int maxFaultDuration = parsePositiveInt(section, "max_fault_duration");

      long seed = parsePositiveLong(section, "seed", System.currentTimeMillis());

      return new NewCarEvent(time, id, maxSpeed, itinerary, resistance,
          faultProbability, maxFaultDuration, seed);
    }

    @Override
    public String getEventName() {
      return FRIENDLY_CLASS_NAME;
    }

    @Override
    public String getEventFileTemplate() {
      return "[" + SECTION_TAG_NAME + "]\n" +
          "time=\n" +
          "id=\n" +
          "type=" + Car.TYPE + "\n" +
          "max_speed=\n" +
          "itinerary=\n" +
          "resistance=\n" +
          "faulty_probability=\n" +
          "max_fault_duration=\n" +
          "seed=\n";
    }

  }

}
