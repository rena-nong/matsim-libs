/* *********************************************************************** *
 * project: org.matsim.*
 * Getagentcoods.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.dressler.util;

import java.util.Collection;

import java.util.*;

import org.matsim.config.Config;
import org.matsim.controler.Controler;
import org.matsim.controler.events.StartupEvent;
import org.matsim.controler.listener.StartupListener;
import org.matsim.events.Events;
import org.matsim.events.algorithms.EventWriterTXT;
import org.matsim.gbl.Gbl;
import org.matsim.interfaces.basic.v01.Coord;
import org.matsim.interfaces.basic.v01.Id;
import org.matsim.interfaces.core.v01.Leg;
import org.matsim.interfaces.core.v01.Person;
import org.matsim.interfaces.core.v01.Plan;
import org.matsim.mobsim.queuesim.QueueNetwork;
import org.matsim.mobsim.queuesim.QueueSimulation;
//import org.matsim.network.MatsimNetworkReader;
//import org.matsim.network.NetworkLayer;
import org.matsim.network.*;
//import org.matsim.population.MatsimPopulationReader;
//import org.matsim.population.Population;
import org.matsim.population.*;
import org.matsim.run.OTFVis;
import org.matsim.utils.vis.netvis.NetVis;
import org.matsim.utils.vis.netvis.streaming.StreamConfig;
import org.matsim.utils.vis.otfvis.executables.OTFEvent2MVI;
import org.matsim.world.World;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class Getagentcoords {

	public static void main(final String[] args) {
		// choose instance
		final String netFilename = "./examples/meine_EA/siouxfalls_network_test.xml";
		//final String netFilename = "/homes/combi/dressler/V/Project/padang/network/padang_net_evac.xml";
		
		//final String plansFilename = "/homes/combi/dressler/V/Project/padang/plans/padang_plans_10p.xml.gz";
		//final String plansFilename = "/homes/combi/dressler/V/code/workspace/matsim/examples/meine_EA/padangplans.xml";
		final String plansFilename = "./examples/meine_EA/siouxfalls_plans_test.xml";
		
		final String outputPngFilename = "./output/exitmap_SF_test.png";
		boolean planstats = true;

		@SuppressWarnings("unused")
		Config config = Gbl.createConfig(null);

		World world = Gbl.getWorld();

		NetworkLayer network = new NetworkLayer();
		new MatsimNetworkReader(network).readFile(netFilename);
		world.setNetworkLayer(network);
		world.complete();

		Population population = new Population();
		new MatsimPopulationReader(population).readFile(plansFilename);


		// get evac links
		Node evac1node = network.getNode("20");
		Map<Id,? extends Link> evaclinks = null;		
		if (evac1node != null) {
			evaclinks = evac1node.getInLinks();
			/*for (Link link : evaclinks.values()) {
				System.out.println(link.getId().toString());
			}*/
		}

		// colours
		Map<String,Color> colours = new HashMap<String, Color>();
		Color noexit = Color.BLACK;

		if (evaclinks != null) {
			for (Link link : evaclinks.values()) {
				colours.put(link.getId().toString(), Color.getHSBColor((float) ((colours.size()*7) % evaclinks.size() )/ (float) evaclinks.size(),1.0f,1.0f));
				//colours.put(link.getId(), Color.getHSBColor(0.3f,1.0f,1.0f));
				//System.out.println(colours.get(link.getId().toString()));
			}		   			
		}

		// create chosen-exit map		
		if (planstats) {			
			Double minx = +500000000d;
			Double maxx = -500000000d;
			Double miny = +500000000d;
			Double maxy = -500000000d;
			
			int width = 400;
			int height = 400;
			BufferedImage image = new BufferedImage(width,
					height,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2D = image.createGraphics(); 

			for (Person p : population.getPersons().values()) {
				Plan plan = p.getSelectedPlan();
				// p.setVisualizerData(visualizerData)
				if (plan == null) continue;

				 Id i = plan.getFirstActivity().getLinkId();
				 Link l = network.getLink(i);
				 Coord c = l.getFromNode().getCoord();
				//Coord c = plan.getFirstActivity().getCoord();
				//if (c == null) continue; // why would this happen? but happens ...
				minx = Math.min(c.getX(), minx);
				miny = Math.min(c.getY(), miny);
				maxx = Math.max(c.getX(), maxx);
				maxy = Math.max(c.getY(), maxy);
			}

			Double scalex = ((double) width) / (maxx - minx)*0.98;
			Double scaley = -((double) height) / (maxy - miny)*0.98;
			Double offsetx = -minx*scalex + 3;
			Double offsety = -maxy*scaley + 3;

			System.out.println("minx "+ minx + " maxx " + maxx);
			System.out.println("miny "+ miny + " maxy " + maxy);
			
			// print exits ...
			for (Link link : evaclinks.values()) {
				Coord c = link.getFromNode().getCoord();
				Double x = c.getX() * scalex + offsetx;
				Double y = c.getY() * scaley + offsety;					  
				int X = x.intValue();
				int Y = y.intValue();
				g2D.setColor(colours.get(link.getId().toString()));
				g2D.fillOval(X-8, Y-8, 16, 16);
			}

			Integer foundpeople = 0;
			Integer notfoundpeople = 0;			
			for (Person p : population.getPersons().values()) {
				Plan plan = p.getSelectedPlan();
				// p.setVisualizerData(visualizerData)
				if (plan == null) {notfoundpeople++; continue;}

				 Id i = plan.getFirstActivity().getLinkId();
				 Link l = network.getLink(i);
				 Coord c = l.getFromNode().getCoord();
				
				//if (c == null) continue; // why would this happen? but happens ...
				
				//System.out.println(c.getX() + " " + c.getY());
				Leg leg = plan.getNextLeg(plan.getFirstActivity());
				if (leg == null || evaclinks == null) {												
					notfoundpeople++;
					continue;
				}

				Double x = c.getX() * scalex + offsetx;
				Double y = c.getY() * scaley + offsety;					  
				int X = x.intValue();
				int Y = y.intValue();
				//System.out.println(x + " " + y);


				boolean found = false; 
				for (Id id : leg.getRoute().getLinkIds()) {			      
					if (evaclinks.containsKey(id)) {
						found = true;
						//System.out.println("Juhu " + id);
						foundpeople++;
						g2D.setColor(colours.get(id.toString()));
						g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
						//g2D.drawLine(X, Y, X, Y);
						g2D.fillOval(X-2, Y-2, 5, 5);
						//g2D.fillOval(X-1, Y-1, 2, 2);
						//g2D.drawOval(X, Y, 1, 1);
					}
				}
				if (!found) {
					notfoundpeople++;
					g2D.setColor(noexit);
					//g2D.drawLine(X, Y, X, Y);
					g2D.fillRect(X-1, Y-1, 2, 2);
				}
				
			
				
			}
			
			
			System.out.println("Found: " + foundpeople +  " not found: " + notfoundpeople);
			File pngfile = new File(outputPngFilename);
			try {
				ImageIO.write(image, "png", pngfile);
			} catch (IOException e) {
			}

		}
	}
}
