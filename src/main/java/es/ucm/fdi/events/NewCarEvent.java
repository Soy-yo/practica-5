package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Car;
import es.ucm.fdi.model.TrafficSimulator;

public class NewCarEvent extends NewVehicleEvent {

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

  }

}
