package org.matsim.contrib.ev.EnergyHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.ev.MobsimScopeEventHandler;
import org.matsim.contrib.ev.MobsimScopeEventHandling;
import org.matsim.contrib.ev.charging.ChargingEndEvent;
import org.matsim.contrib.ev.charging.ChargingEndEventHandler;
import org.matsim.contrib.ev.charging.VehicleChargingHandler;
import org.matsim.contrib.ev.fleet.ElectricFleet;
import org.matsim.contrib.ev.fleet.ElectricVehicle;
import org.matsim.contrib.ev.fleet.ImmutableElectricVehicleSpecification.Builder;
import org.matsim.contrib.ev.infrastructure.Charger;
import org.matsim.contrib.ev.infrastructure.ChargingInfrastructure;
import org.matsim.contrib.ev.infrastructure.ChargingInfrastructures;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.vehicles.Vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;

public class EnergyEventHandler implements ActivityStartEventHandler, ActivityEndEventHandler, PersonLeavesVehicleEventHandler,
ChargingEndEventHandler, MobsimScopeEventHandler {

	public String vehicleType;
	
	public static final String CHARGING_IDENTIFIER = " charging";
	public static final String CHARGING_INTERACTION = PlanCalcScoreConfigGroup.createStageActivityType(CHARGING_IDENTIFIER);
	private Map<Id<Person>, Id<Vehicle>> lastVehicleUsed = new HashMap<>();
	private Map<Id<ElectricVehicle>, Id<Charger>> vehiclesAtChargers = new HashMap<>();

	private final ChargingInfrastructure chargingInfrastructure;
	private final ElectricFleet electricFleet;
	private final ImmutableListMultimap<Id<Link>, Charger> chargersAtLinks;

	@Inject
	public EnergyEventHandler(ChargingInfrastructure chargingInfrastructure, ElectricFleet electricFleet,
			MobsimScopeEventHandling events) {
		this.chargingInfrastructure = chargingInfrastructure;
		this.electricFleet = electricFleet;
		chargersAtLinks = ChargingInfrastructures.getChargersAtLinks(chargingInfrastructure);
		events.addMobsimScopeHandler(this);
	}
	
	@Override
	public void handleEvent(ActivityStartEvent event) {
		if (event.getActType().endsWith(CHARGING_INTERACTION)) {
			Id<Vehicle> vehicleId = lastVehicleUsed.get(event.getPersonId());
			if (vehicleId != null) {
				Id<ElectricVehicle> evId = Id.create(vehicleId, ElectricVehicle.class);
				if (electricFleet.getElectricVehicles().containsKey(evId)) {
					ElectricVehicle ev = electricFleet.getElectricVehicles().get(evId);
					List<Charger> chargers = chargersAtLinks.get(event.getLinkId());
					Charger c = chargers.stream()
							.filter(ch -> ev.getChargerTypes().contains(ch.getChargerType()))
							.findAny()
							.get();
					c.getLogic().addVehicle(ev, event.getTime());
					vehiclesAtChargers.put(evId, c.getId());
				}
			}
		}
	}

	@Override
	public void handleEvent(ActivityEndEvent event) {
		if (event.getActType().endsWith(CHARGING_INTERACTION)) {
			Id<Vehicle> vehicleId = lastVehicleUsed.get(event.getPersonId());
			if (vehicleId != null) {
				Id<ElectricVehicle> evId = Id.create(vehicleId, ElectricVehicle.class);
				Id<Charger> chargerId = vehiclesAtChargers.remove(evId);
				if (chargerId != null) {
					Charger c = chargingInfrastructure.getChargers().get(chargerId);
					c.getLogic().removeVehicle(electricFleet.getElectricVehicles().get(evId), event.getTime());
				}
			}
		}
	}

	@Override
	public void handleEvent(PersonLeavesVehicleEvent event) {
		lastVehicleUsed.put(event.getPersonId(), event.getVehicleId());
	}

	@Override
	public void handleEvent(ChargingEndEvent event) {
		vehiclesAtChargers.remove(event.getVehicleId());
		//Charging has ended before activity ends
	}
	
	public static void main(String args[]) {
		
		EnergyEventHandler model = new EnergyEventHandler();
		if (model.vehicleType == "defaultVehicleType") {
			model.handleEvent();
		}
		else if (model.vehicleType == "conventionalVehicleType") {
			model.handleEvent();
		}
	
	
	}
}
