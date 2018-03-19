package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.MostCrowdedJunction;
import es.ucm.fdi.model.TrafficSimulator;

public class NewMostCrowdedJunctionEvent extends NewJunctionEvent {

  NewMostCrowdedJunctionEvent(int time, String id) {
    super(time, id);
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new MostCrowdedJunction(id));
  }

  static class Builder extends NewJunctionEvent.Builder {

    @Override
    public boolean matchesType(IniSection section) {
      return MostCrowdedJunction.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewJunctionEvent parseType(IniSection section, int time, String id) {
      return new NewMostCrowdedJunctionEvent(time, id);
    }

  }

}
