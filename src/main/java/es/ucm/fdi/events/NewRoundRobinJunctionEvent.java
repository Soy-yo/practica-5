package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.RoundRobinJunction;
import es.ucm.fdi.model.TrafficSimulator;

public class NewRoundRobinJunctionEvent extends NewJunctionEvent {

  private int maxTimeSlice;
  private int minTimeSlice;

  NewRoundRobinJunctionEvent(int time, String id, int minTimeSlice, int maxTimeSlice) {
    super(time, id);
    this.maxTimeSlice = maxTimeSlice;
    this.minTimeSlice = minTimeSlice;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new RoundRobinJunction(id, minTimeSlice, maxTimeSlice));
  }

  static class Builder extends NewJunctionEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return RoundRobinJunction.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewJunctionEvent parseType(IniSection section, int time, String id) {
      int maxTimeSlice = parsePositiveInt(section, "max_time_slice");
      int minTimeSlice = parsePositiveInt(section, "min_time_slice");
      return new NewRoundRobinJunctionEvent(time, id, minTimeSlice, maxTimeSlice);
    }

  }

}
