package es.ucm.fdi.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class EventBuilderTest {

  @Test
  void simpleVehicleEvent() {
    TestSimulator simulator = new TestSimulator();
    Junction j1 = new Junction("jt1");
    Junction j2 = new Junction("jt2");
    Road r = new Road("rt1", 10, 10, "jt1", "jt2");
    simulator.addSimulatedObject(j1);
    simulator.addSimulatedObject(j2);
    simulator.addSimulatedObject(r);
    List<Junction> junctions = Arrays.asList(j1, j2);

    IniSection section = new IniSection("new_vehicle");
    section.setValue("time", 0);
    section.setValue("id", "vt1");
    section.setValue("max_speed", 10);
    section.setValue("itinerary", "jt1,jt2");

    Event event = EventBuilder.parse(section);

    assertNotNull(event);
    event.execute(simulator);

    // No se puede usar assertEquals en las colecciones porque no sobreescriben equals()
    int i = 0;
    for (Junction j : simulator.getJunctions()) {
      assertEquals(junctions.get(i), j);
      i++;
    }
    assertEquals("vt1", simulator.getVehicles().iterator().next().getId());
  }

  @Test
  void unknownEvent() {
    IniSection section = new IniSection("unknown_tag");
    section.setValue("id", "some_id");
    assertNull(EventBuilder.parse(section));
  }

  @Test
  void wrongIds() {
    IniSection section = new IniSection("new_vehicle");
    section.setValue("id", "");
    assertThrows(IllegalStateException.class, () -> EventBuilder.parse(section));
    section.setValue("id", "hello world");
    assertThrows(IllegalStateException.class, () -> EventBuilder.parse(section));
    section.setValue("id", "hello-world");
    assertThrows(IllegalStateException.class, () -> EventBuilder.parse(section));
  }

  @Test
  void wrongType() {
    IniSection section = new IniSection("new_vehicle");
    section.setValue("time", "0");
    section.setValue("id", "vehicle");
    section.setValue("itinerary", "j1,j2");
    section.setValue("max_speed", "20000");
    section.setValue("type", "spaceship");
    assertNull(EventBuilder.parse(section));
  }

  @Test
  void wrongTime() {
    IniSection section = new IniSection("new_junction");
    section.setValue("time", "-1");
    section.setValue("id", "j");
    assertThrows(IllegalStateException.class, () -> EventBuilder.parse(section));
    section.setValue("time", "hello_world");
    assertThrows(IllegalStateException.class, () -> EventBuilder.parse(section));
  }

  @Test
  void wrongItinerary() {
    IniSection section = new IniSection("new_vehicle");
    section.setValue("time", "0");
    section.setValue("id", "vehicle");
    section.setValue("itinerary", "");
    section.setValue("max_speed", "20");
    assertThrows(IllegalStateException.class, () -> EventBuilder.parse(section));
    section.setValue("itinerary", "");
    assertThrows(IllegalStateException.class, () -> EventBuilder.parse(section));
  }

  private class TestSimulator extends TrafficSimulator {

    RoadMap roadMapTest = new RoadMap();

    @Override
    public void addSimulatedObject(SimulatedObject o) {
      roadMapTest.addSimulatedObject(o);
    }

    @Override
    public Queue<Junction> getPath(String[] path) {
      return roadMapTest.getPath(path);
    }

    Collection<Vehicle> getVehicles() {
      return roadMapTest.getVehicles();
    }

    Collection<Road> getRoads() {
      return roadMapTest.getRoads();
    }

    Collection<Junction> getJunctions() {
      return roadMapTest.getJunctions();
    }

  }

}