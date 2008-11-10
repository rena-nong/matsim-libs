package playground.mmoyo.PTCase2;

import org.matsim.network.Link;
import org.matsim.router.util.TravelTime;

public class PTTravelTime implements TravelTime {
	private final double WALKING_SPEED = 0.9; //      4 km/h  human speed 
	private PTTimeTable2 ptTimeTable;
	
	public PTTravelTime(PTTimeTable2 ptTimeTable) {
		this.ptTimeTable = ptTimeTable; 
	}
	
	//minutes
	public double getLinkTravelTime(Link link, double time) {
		double travelTime =0;
		if (link.getType().equals("Transfer")){
			travelTime= ptTimeTable.GetTransferTime(link, time);
		}else if (link.getType().equals("Walking")){
			travelTime = link.getLength()* WALKING_SPEED ;	
		}else if (link.getType().equals("Standard")){
			travelTime= ptTimeTable.GetTravelTime(link);
		}
		return travelTime;
	}
}