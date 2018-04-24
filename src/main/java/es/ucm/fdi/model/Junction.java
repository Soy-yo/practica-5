package es.ucm.fdi.model;

import java.util.*;

public class Junction extends SimulatedObject {

	private static final String SECTION_TAG_NAME = "junction_report";
	public static final String[] INFO = { "ID", "Green", "Red" };

	protected Map<Road, IncomingRoad> incomingRoads;
	protected IncomingRoad currentRoadOn; // carretera con semáforo en verde
											// actualmente
	protected Iterator<IncomingRoad> nextRoad; // siguiente carretera a la del
												// semáforo en verde

	public Junction(String id) {
		super(id);
		incomingRoads = new LinkedHashMap<>();
	}

	public void addRoad(Road road) {
		incomingRoads.put(road, new IncomingRoad());
	}

	public void vehicleIn(Vehicle vehicle) {
		incomingRoads.get(vehicle.getRoad()).vehicleIn(vehicle);
	}

	@Override
	public void advance() {
		if (!incomingRoads.isEmpty()) {
			if (currentRoadOn != null && !currentRoadOn.isEmpty()) {
				currentRoadOn.vehicleOut();
			}
			switchLights();
		}
	}

	protected void switchLights() {
		IncomingRoad previous = currentRoadOn;
		currentRoadOn = getNextRoad();
		if (previous != null) {
			previous.switchLight();
		}
		currentRoadOn.switchLight();
	}

	protected IncomingRoad getNextRoad() {
		// Reinicia el iterador cada vuelta
		if (nextRoad == null || !nextRoad.hasNext()) {
			nextRoad = incomingRoads.values().iterator();
		}
		return nextRoad.next();
	}

	// Devuelve la carretera que une el cruce con id previousJunction con esta
	// (si existe)
	public Road getStraightRoad(String previousJunction) {
		for (Road r : incomingRoads.keySet()) {
			if (r.getSource().equals(previousJunction)) {
				return r;
			}
		}
		return null;
	}

	@Override
	public void fillReportDetails(Map<String, String> kvps) {
		if (!incomingRoads.isEmpty()) {
			StringBuilder stringBuilder = new StringBuilder();
			for (Map.Entry<Road, IncomingRoad> e : incomingRoads.entrySet()) {
				// Para cada carretera entrante
				stringBuilder.append("(" + e.getKey() + ","
						+ e.getValue().lightColor() + ",[");
				// Rellena los vehículos en la cola
				for (Vehicle v : e.getValue().vehicles()) {
					stringBuilder.append(v + ",");
				}
				if (!e.getValue().isEmpty()) {
					stringBuilder.deleteCharAt(stringBuilder.length() - 1); // coma
				}
				stringBuilder.append("]),");
			}
			kvps.put("queues",
					stringBuilder.substring(0, stringBuilder.length() - 1));
		} else {
			kvps.put("queues", "");
		}
	}

	@Override
	public Map<String, String> describe() {
		Map<String, String> result = new HashMap<>();
		StringBuilder greenBuilder = new StringBuilder();
		StringBuilder redBuilder = new StringBuilder();
		greenBuilder.append('[');
		redBuilder.append('[');
		for (Map.Entry<Road, IncomingRoad> e : incomingRoads.entrySet()) {
			// Para cada carretera entrante
			if (e.getValue().lightColor().equals("green")) {
				greenBuilder.append("(" + e.getKey() + ","
						+ e.getValue().lightColor() + ",[");
				// Rellena los vehículos en la cola
				for (Vehicle v : e.getValue().vehicles()) {
					greenBuilder.append(v + ",");
				}
				if (!e.getValue().isEmpty()) {
					greenBuilder.deleteCharAt(greenBuilder.length() - 1); // coma
				}
				greenBuilder.append("]),");
			} else {
				redBuilder.append("(" + e.getKey() + ","
						+ e.getValue().lightColor() + ",[");
				// Rellena los vehículos en la cola
				for (Vehicle v : e.getValue().vehicles()) {
					redBuilder.append(v + ",");
				}
				if (!e.getValue().isEmpty()) {
					redBuilder.deleteCharAt(redBuilder.length() - 1); // coma
				}
				redBuilder.append("]),");
			}
		}
		greenBuilder.append(']');
		redBuilder.append(']');
		result.put(INFO[0], super.id);
		result.put(INFO[1], greenBuilder.toString());
		result.put(INFO[2], redBuilder.toString());
		return result;
	}

	@Override
	protected String getReportHeader() {
		return SECTION_TAG_NAME;
	}

	protected class IncomingRoad {

		Queue<Vehicle> vehicleList;
		boolean greenLight;

		public IncomingRoad() {
			this.vehicleList = new ArrayDeque<>();
			greenLight = false;
		}

		void vehicleIn(Vehicle vehicle) {
			vehicleList.add(vehicle);
		}

		void vehicleOut() {
			Vehicle vehicle = vehicleList.poll();
			vehicle.moveToNextRoad();
		}

		int vehicleCount() {
			return vehicleList.size();
		}

		boolean isEmpty() {
			return vehicleList.size() == 0;
		}

		void switchLight() {
			greenLight = !greenLight;
		}

		String lightColor() {
			return greenLight ? "green" : "red";
		}

		Iterable<Vehicle> vehicles() {
			return () -> vehicleList.iterator();
		}

	}

}
