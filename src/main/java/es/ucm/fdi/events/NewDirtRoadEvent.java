package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.DirtRoad;
import es.ucm.fdi.model.TrafficSimulator;

public class NewDirtRoadEvent extends NewRoadEvent {

  NewDirtRoadEvent(int time, String id, String sourceId,
                   String destinationId, int maxSpeed, int length) {
    super(time, id, sourceId, destinationId, maxSpeed, length);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new DirtRoad(id, length, maxSpeed, sourceId, destinationId));
  }

  static class Builder extends NewRoadEvent.Builder {


    @Override
    public boolean matchesType(IniSection section) {
      return DirtRoad.TYPE.equals(section.getValue("type"));
    }

    @Override
    public NewRoadEvent parseType(IniSection section, int time, String id, String src,
                                  String dest, int maxSpeed, int length) {
      return new NewDirtRoadEvent(time, id, src, dest, maxSpeed, length);
    }

  }

}
