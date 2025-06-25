/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 *****************************************************************************/

package codelets.behaviors;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import ws3dproxy.model.Thing;


public class Delivery extends Codelet {

	private Memory leafletStatusMO;
	private Memory selfInfoMO;
	private MemoryContainer legsMO;
	private int creatureBasicSpeed;
	private double reachDistance;
	// HARDCODED
	private double xSpot = 200;
	private double ySpot = 200;

	public Delivery(int creatureBasicSpeed, int reachDistance) {
		this.creatureBasicSpeed=creatureBasicSpeed;
		this.reachDistance=reachDistance;
        this.name = "Delivery";


	}

	@Override
	public void accessMemoryObjects() {
		leafletStatusMO=(MemoryObject)this.getInput("LEAFLETS_STATUS");
		selfInfoMO=(MemoryObject)this.getInput("INNER");
		legsMO=(MemoryContainer)this.getOutput("LEGS");
	}

	@Override
	public void proc() {
		// Find distance between creature and closest apple
		//If far, go towards it
		//If close, stops
		Idea cis = (Idea) selfInfoMO.getI();
		List<Boolean> leafletStatus = (List<Boolean>) leafletStatusMO.getI();
	
		double selfX=(double)cis.get("position.x").getValue();
		double selfY=(double)cis.get("position.y").getValue();

		Point2D pSpot = new Point();
		pSpot.setLocation(xSpot, ySpot);

		Point2D pSelf = new Point();
		pSelf.setLocation(selfX, selfY);

		double distance = pSelf.distance(pSpot);
		// Idea message = Idea.createIdea("message","", Idea.guessType("Property",null,1.0,0.5));
		try {
			if(distance>reachDistance && leafletStatus.contains(true)){ //Go to it
				// message.add(Idea.createIdea("ACTION","GOTO", Idea.guessType("Property",null,1.0,0.5)));
				// message.add(Idea.createIdea("X",(int)xSpot, Idea.guessType("Property",null,1.0,0.5)));
				// message.add(Idea.createIdea("Y",(int)ySpot, Idea.guessType("Property",null,1.0,0.5)));
				// message.add(Idea.createIdea("SPEED",creatureBasicSpeed, Idea.guessType("Property",null,1.0,0.5)));
				JSONObject message=new JSONObject();
				try {
					message.put("ACTION", "DELIVERY");
									activation=1.0;
					legsMO.setI(message.toString(),activation,name);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else if (leafletStatus.contains(true))
			{//Stop
				// message.add(Idea.createIdea("ACTION","GOTO", Idea.guessType("Property",null,1.0,0.5)));
				// message.add(Idea.createIdea("X",(int)xSpot, Idea.guessType("Property",null,1.0,0.5)));
				// message.add(Idea.createIdea("Y",(int)ySpot, Idea.guessType("Property",null,1.0,0.5)));
				// message.add(Idea.createIdea("SPEED",creatureBasicSpeed, Idea.guessType("Property",null,1.0,0.5)));
				JSONObject message=new JSONObject();
				try {
					message.put("ACTION", "DELIVERY");
									activation=1.0;
					legsMO.setI(message.toString(),activation,name);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
            else activation=0.0;
            JSONObject message=new JSONObject();
            message.put("ACTION", "DELIVERY");
            legsMO.setI(message.toString(),activation,name);	
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
                
	}//end proc
        
	@Override
	public void calculateActivation() {
	
	}
	
	String toJson(Idea i) {
		String q = "\"";
		String out = "{";
		String val;
		int ii=0;
		for (Idea il : i.getL()) {
			if (il.getL().isEmpty()) {
				if (il.isNumber()) val = il.getValue().toString();
				else val = q+il.getValue()+q;
			}
			else val = toJson(il);
			if (ii == 0) out += q+il.getName()+q+":"+val;
			else out += ","+q+il.getName()+q+":"+val;
			ii++;
		}
		out += "}";
		return out;
	}

}
