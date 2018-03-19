package es.ucm.fdi.control;

import es.ucm.fdi.events.Event;
import es.ucm.fdi.events.EventBuilder;
import es.ucm.fdi.excepcions.SimulatorError;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Controller {

  private TrafficSimulator simulator;
  private OutputStream outputStream;

  public Controller(TrafficSimulator simulator) {
    this.simulator = simulator;
  }

  public void setOutputStream(OutputStream os) {
    outputStream = os;
  }

  public void run(int ticks) {
    try {
      simulator.execute(ticks, outputStream);
    } catch (SimulatorError e) {
      throw new SimulatorError("Execution failed", e);
    }
  }

  public void loadEvents(InputStream is) {
    try {
      Ini ini = new Ini(is);
      for (IniSection section : ini.getSections()) {
        try {
          Event event = EventBuilder.parse(section);
          if (event == null) {
            throw new IllegalStateException(
                "Event for section " + section.getTag() + " was not recognized");
          }
          simulator.addEvent(event);
        } catch (IllegalStateException e) {
          throw new IllegalStateException("Failed while trying to load events", e);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Something went wrong while reading ini file", e);
    }
  }

}
