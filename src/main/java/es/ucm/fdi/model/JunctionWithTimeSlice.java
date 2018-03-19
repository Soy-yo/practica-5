package es.ucm.fdi.model;

public abstract class JunctionWithTimeSlice extends Junction {

  protected int timeLapse;
  protected int timeUnits;
  protected int timesUsed;

  public JunctionWithTimeSlice(String id, int timeLapse) {
    super(id);
    this.timeLapse = timeLapse;
    timeUnits = 0;
    timesUsed = 0;
  }

  @Override
  // Sobreescrito para usar la nueva IncomingRoad
  public void addRoad(Road road) {
    incomingRoads.put(road, new IncomingRoad());
  }

  protected class IncomingRoad extends Junction.IncomingRoad {

    @Override
    void vehicleOut() {
      Vehicle vehicle = vehicleList.poll();
      vehicle.moveToNextRoad();
      timesUsed++;
    }

    @Override
    void switchLight() {
      super.switchLight();
      timesUsed = 0;
    }

    @Override
    String lightColor() {
      return greenLight ? "green:" + (timeLapse - timeUnits) : "red";
    }

  }

}
