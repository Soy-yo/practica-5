package es.ucm.fdi.model;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SimulatedObject {

  protected final String id; // id único

  public SimulatedObject(String id) {
    this.id = id;
  }

  public abstract void advance();

  public abstract void fillReportDetails(Map<String, String> kvps);

  protected abstract String getReportHeader();

  public String getId() {
    return id;
  }

  public Map<String, String> generateReport(int time) {
    Map<String, String> kvps = new LinkedHashMap<>();
    kvps.put("", getReportHeader());
    kvps.put("id", id);
    kvps.put("time", "" + time);
    fillReportDetails(kvps);
    return kvps;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimulatedObject other = (SimulatedObject) o;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return id;
  }

}
