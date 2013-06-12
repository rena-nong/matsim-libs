package playground.pieter.pseudosim.replanning;

import java.util.ArrayList;

import org.matsim.core.controler.PlanStrategyFactoryRegister;
import org.matsim.core.controler.PlanStrategyRegistrar;
import org.matsim.core.controler.PlanStrategyRegistrar.Selector;
import org.matsim.core.replanning.modules.ChangeExpBetaPlanStrategyFactory;
import org.matsim.core.replanning.modules.KeepLastSelectedPlanStrategyFactory;
import org.matsim.core.replanning.modules.ReRoutePlanStrategyFactory;
import org.matsim.core.replanning.modules.SelectBestPlanStrategyFactory;
import org.matsim.core.replanning.modules.SelectExpBetaPlanStrategyFactory;
import org.matsim.core.replanning.modules.SelectPathSizeLogitStrategyFactory;
import org.matsim.core.replanning.modules.SelectRandomStrategyFactory;

import playground.pieter.pseudosim.controler.PSimControler;
import playground.pieter.pseudosim.replanning.factories.PSimBestScorePlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimChangeExpBetaPlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimChangeTripModeStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimDoNothingPlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimKeepLastSelectedPlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimLocationChoicePlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimReRoutePlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimSelectExpBetaPlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimSelectPathSizeLogitStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimSelectRandomStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimSubtourModeChoiceStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimTimeAllocationMutatorPlanStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimTripSubtourModeChoiceStrategyFactory;
import playground.pieter.pseudosim.replanning.factories.PSimTripTimeAllocationMutatorStrategyFactory;
import playground.pieter.pseudosim.replanning.modules.PSimPlanMarkerModule;
import org.matsim.core.controler.PlanStrategyRegistrar;

/**
 * @author fouriep
 *         <P>
 *         If a mutating strategy is sent for pseudo-simulation, it needs to be
 *         marked as such, and registered with the {@link PSimControler}.
 *         Non-mutating strategies, e.g. selector strategies, should be disabled
 *         during PSim iterations, and only run during QSim iterations.
 * 
 *         <P>
 *         This class records strategies that should work with PSim. It extends
 *         their factories by appending a {@link PSimPlanMarkerModule} at the
 *         end of each strategy. Each factory is registered during controler
 *         construction, and the config entries are changed to refer to their
 *         PSim equivalents in the controler's substituteStrategies() method.
 *         
 *         <P>
 *         Each strategy name is taken from the enum in the
 *         {@link PlanStrategyRegistrar} to ensure future consistency
 * 
 *         <P>
 *         <B>NOTE:</B> to save processing overhead, selector strategies are set
 *         up to always return the person's current selected plan during
 *         non-QSim iterations.
 * 
 * 
 */
public class PSimPlanStrategyRegistrar {

	private ArrayList<String> compatibleStrategies = new ArrayList<String>();

	public PSimPlanStrategyRegistrar(PSimControler controler) {
		String strategyName = PlanStrategyRegistrar.Selector.BestScore
				.toString();
		compatibleStrategies.add(strategyName);
		controler.addPlanStrategyFactory(strategyName + "PSim",
				new PSimBestScorePlanStrategyFactory());
		strategyName = PlanStrategyRegistrar.Selector.KeepLastSelected
				.toString();
		compatibleStrategies.add(strategyName);
		controler.addPlanStrategyFactory(strategyName + "PSim",
				new PSimKeepLastSelectedPlanStrategyFactory());
		strategyName = PlanStrategyRegistrar.Selector.SelectExpBeta.toString();
		compatibleStrategies.add(strategyName);
		controler.addPlanStrategyFactory(strategyName + "PSim",
				new PSimSelectExpBetaPlanStrategyFactory());
		strategyName = PlanStrategyRegistrar.Selector.ChangeExpBeta.toString();
		compatibleStrategies.add(strategyName);
		controler.addPlanStrategyFactory(strategyName + "PSim",
				new PSimChangeExpBetaPlanStrategyFactory());
		strategyName = PlanStrategyRegistrar.Selector.SelectRandom.toString();
		compatibleStrategies.add(strategyName);
		controler.addPlanStrategyFactory(strategyName + "PSim",
				new PSimSelectRandomStrategyFactory());
		strategyName = PlanStrategyRegistrar.Selector.SelectPathSizeLogit
				.toString();
		compatibleStrategies.add(strategyName);
		controler.addPlanStrategyFactory(strategyName + "PSim",
				new PSimSelectPathSizeLogitStrategyFactory());

		strategyName = PlanStrategyRegistrar.Names.ReRoute
				.toString();
		compatibleStrategies.add(strategyName);
		controler.addPlanStrategyFactory(strategyName + "PSim",
				new PSimReRoutePlanStrategyFactory(controler));
		compatibleStrategies.add("LocationChoice");
		controler.addPlanStrategyFactory("LocationChoicePSim",
				new PSimLocationChoicePlanStrategyFactory(controler));
		compatibleStrategies.add("TimeAllocationMutator");
		controler.addPlanStrategyFactory("TimeAllocationMutatorPSim",
				new PSimTimeAllocationMutatorPlanStrategyFactory(controler));
		compatibleStrategies.add("SubtourModeChoice");
		controler.addPlanStrategyFactory("SubtourModeChoicePSim",
				new PSimSubtourModeChoiceStrategyFactory(controler));
		compatibleStrategies.add("DoNothing");
		controler.addPlanStrategyFactory("DoNothingPSim",
				new PSimDoNothingPlanStrategyFactory(controler));
		compatibleStrategies.add("TransitTimeAllocationMutator");
		controler.addPlanStrategyFactory("TransitTimeAllocationMutatorPSim",
				new PSimTripTimeAllocationMutatorStrategyFactory(controler));
		compatibleStrategies.add("TransitChangeLegMode");
		controler.addPlanStrategyFactory("TransitChangeLegModePSim",
				new PSimChangeTripModeStrategyFactory(controler));
		compatibleStrategies.add("TransitSubtourModeChoice");
		controler.addPlanStrategyFactory("TransitSubtourModeChoicePSim",
				new PSimTripSubtourModeChoiceStrategyFactory(controler));

	}

	public ArrayList<String> getCompatibleStrategies() {
		return compatibleStrategies;
	}

}
