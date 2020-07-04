package org.matsim.contrib.ev.EnergyHandler;

import java.util.Objects;

import org.matsim.api.core.v01.Id;
import org.matsim.contrib.ev.fleet.ElectricVehicle;
import org.matsim.contrib.ev.fleet.ImmutableElectricVehicleSpecification.Builder;

import com.google.common.collect.ImmutableList;

public class EnergyEventHandler {
	
	private final Id<ElectricVehicle> id;
	private final String vehicleType;
	private final ImmutableList<String> chargerTypes;
	private final double initialSoc;
	private final double batteryCapacity;
	
	public void ImmutableElectricVehicleSpecification(Builder builder) {
		id = Objects.requireNonNull(builder.id);
		vehicleType = Objects.requireNonNull(builder.vehicleType);
		chargerTypes = Objects.requireNonNull(builder.chargerTypes);
		initialSoc = Objects.requireNonNull(builder.initialSoc);
		batteryCapacity = Objects.requireNonNull(builder.batteryCapacity);

		if (vehicleType == "defaultVehicleType") {
			handleEvent
		}
			
		}
	  

}
