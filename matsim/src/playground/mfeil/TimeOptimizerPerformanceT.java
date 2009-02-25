package playground.mfeil;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.matsim.controler.Controler;
import org.matsim.gbl.MatsimRandom;
import org.matsim.interfaces.core.v01.Act;
import org.matsim.interfaces.core.v01.Plan;
import org.matsim.planomat.costestimators.LegTravelTimeEstimator;
import org.matsim.population.algorithms.PlanAlgorithm;
import org.matsim.router.PlansCalcRoute;
import org.matsim.router.costcalculators.FreespeedTravelTimeCost;
import org.matsim.router.util.AStarLandmarksFactory;
import org.matsim.router.util.PreProcessLandmarks;
import org.matsim.scoring.PlanScorer;
import org.matsim.scoring.ScoringFunctionFactory;


public class TimeOptimizerPerformanceT implements org.matsim.population.algorithms.PlanAlgorithm {
	
	private final PlanAlgorithm 	timeOptAlgorithm;
	private final PlanScorer		scorer;
	private final PlansCalcRoute router;
	private final PreProcessLandmarks		preProcessRoutingData;
	
	public TimeOptimizerPerformanceT (Controler controler, LegTravelTimeEstimator estimator, PlanScorer scorer, ScoringFunctionFactory factory){
		System.out.println("disabled code as it doesn't compile. mrieser/16feb2009");
		System.exit(-1);
		// TimeOptimizer11 cannot be found
		this.timeOptAlgorithm 		= null;// = new TimeOptimizer11 (estimator, scorer);
		
		//this.timeOptAlgorithm 	= new Planomat (estimator, factory);
		this.scorer			  		= scorer;
		this.preProcessRoutingData 	= new PreProcessLandmarks(new FreespeedTravelTimeCost());
		this.preProcessRoutingData.run(controler.getNetwork());
		this.router 				= new PlansCalcRoute(controler.getNetwork(), controler.getTravelCostCalculator(), controler.getTravelTimeCalculator(), new AStarLandmarksFactory(this.preProcessRoutingData));
		
	}
	
	public void run (Plan plan){
		
		if (plan.getPerson().getId().toString().equals("2")){
		
			String outputfile = Controler.getOutputFilename("TimeOptimizerTest.xls");
			PrintStream stream;
			try {
				stream = new PrintStream (new File(outputfile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			stream.print(plan.getScore()+"\t");
			for (int z= 0;z<plan.getActsLegs().size();z=z+2){
			Act act = (Act)plan.getActsLegs().get(z);
				stream.print(act.getType()+"\t");
			}
			stream.println();
			stream.print("\t");
			for (int z= 0;z<plan.getActsLegs().size();z=z+2){
				stream.print(((Act)(plan.getActsLegs()).get(z)).getDuration()+"\t");
			}
			stream.println();
			
			// Routing
			this.router.run(plan);
			
			
			// Variation of plan
			PlanomatXPlan [] variation = new PlanomatXPlan [50];
			double [][] statistics 	 = new double [variation.length][2];
			for (int i=0;i<variation.length;i++){
				variation[i] = new PlanomatXPlan (plan.getPerson());
				variation[i].copyPlan(plan);
			}
			for (int i = 0;i<variation.length;i++){
				double time = 70000;
				((Act)variation[i].getActsLegs().get(0)).setDuration(MatsimRandom.random.nextDouble()*time);
				((Act)variation[i].getActsLegs().get(0)).setEndTime(((Act)variation[i].getActsLegs().get(0)).getDuration());
				time -=((Act)variation[i].getActsLegs().get(0)).getDuration();
				for (int j=2; j<variation[i].getActsLegs().size()-2;j+=2){
					((Act)variation[i].getActsLegs().get(j)).setStartTime(((Act)variation[i].getActsLegs().get(j-2)).getEndTime());
					((Act)variation[i].getActsLegs().get(j)).setDuration(MatsimRandom.random.nextDouble()*time);
					((Act)variation[i].getActsLegs().get(j)).setEndTime(((Act)variation[i].getActsLegs().get(j)).getDuration()+((Act)variation[i].getActsLegs().get(j)).getStartTime());
					time -= ((Act)variation[i].getActsLegs().get(j)).getDuration();
				}
				((Act)variation[i].getActsLegs().get(variation[i].getActsLegs().size()-1)).setStartTime(((Act)variation[i].getActsLegs().get(variation[i].getActsLegs().size()-3)).getEndTime());
				((Act)variation[i].getActsLegs().get(variation[i].getActsLegs().size()-1)).setDuration(86400-((Act)variation[i].getActsLegs().get(variation[i].getActsLegs().size()-1)).getStartTime());
				
				stream.print("\t");
				for (int z= 0;z<plan.getActsLegs().size();z=z+2){
					stream.print(((Act)(variation[i].getActsLegs()).get(z)).getDuration()+"\t");
				}
				stream.println();
			}
			
			stream.println();
			long average=0;
			double mean=0;
			for (int i = 0;i<variation.length;i++){
				long runtime = System.currentTimeMillis();
				timeOptAlgorithm.run(variation[i]);
				statistics[i][1] = System.currentTimeMillis()-runtime;
				average+=statistics[i][1];
				
				
				variation[i].setScore(scorer.getScore(variation[i]));
				statistics[i][0] = variation[i].getScore();
				mean+=statistics[i][0];
				
				stream.print(variation[i].getScore()+"\t");
				for (int z= 0;z<plan.getActsLegs().size();z=z+2){
					stream.print(((Act)(variation[i].getActsLegs()).get(z)).getDuration()+"\t");
				}
				stream.println(statistics[i][1]);
			}
			mean = mean/statistics.length;
			double varianz=0;
			for (int i=0;i<statistics.length;i++){
				//varianz += Math.exp(statistics[i][0]-mean);
				varianz += (statistics[i][0]-mean)*(statistics[i][0]-mean);
			}
			stream.println(mean+"\t\t\t\t\t\t"+average/statistics.length);
			stream.println(varianz/statistics.length);
		}
	}
}
