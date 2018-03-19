package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Road;
import es.ucm.fdi.model.TrafficSimulator;

public class NewRoadEvent extends Event {

  protected static final String SECTION_TAG_NAME = "new_road";

  protected String sourceId;
  protected String destinationId;
  protected int maxSpeed;
  protected int length;

  NewRoadEvent(int time, String id, String sourceId, String destinationId, int maxSpeed,
               int length) {
    super(time, id);
    this.sourceId = sourceId;
    this.destinationId = destinationId;
    this.maxSpeed = maxSpeed;
    this.length = length;
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Road(id, length, maxSpeed, sourceId, destinationId));
  }

  static class Builder implements Event.Builder {

    @Override
    public Event parse(IniSection section) {

      if (!section.getTag().equals(SECTION_TAG_NAME) || !matchesType(section)) {
        return null;
      }

      int time = parsePositiveInt(section, "time", 0);

      String id = getId(section);

      String src = parseString(section, "src");

      String dest = parseString(section, "dest");

      int maxSpeed = parsePositiveInt(section, "max_speed");

      int length = parsePositiveInt(section, "length");

      return parseType(section, time, id, src, dest, maxSpeed, length);
    }

    public NewRoadEvent parseType(IniSection section, int time, String id, String src,
                                  String dest, int maxSpeed, int length) {
      return new NewRoadEvent(time, id, src, dest, maxSpeed, length);
    }

  }

}
