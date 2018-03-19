package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Junction;
import es.ucm.fdi.model.TrafficSimulator;

public class NewJunctionEvent extends Event {

  protected static final String SECTION_TAG_NAME = "new_junction";

  NewJunctionEvent(int time, String id) {
    super(time, id);
  }

  @Override
  public void execute(TrafficSimulator simulator) {
    simulator.addSimulatedObject(new Junction(id));
  }

  static class Builder implements Event.Builder {

    @Override
    public Event parse(IniSection section) {

      if (!section.getTag().equals(SECTION_TAG_NAME) || !matchesType(section)) {
        return null;
      }

      int time = parsePositiveInt(section, "time", 0);

      String id = getId(section);

      return parseType(section, time, id);
    }

    public NewJunctionEvent parseType(IniSection section, int time, String id) {
      return new NewJunctionEvent(time, id);
    }

  }

}
