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

import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ws3dproxy.model.Thing;

public class GetClosestJewel extends Codelet {

	private Memory closestJewelMO;
	private Memory innerSenseMO;
	private int reachDistance;
	private Memory handsMO;
    private Thing closestJewel;
    private Idea cis;

	public GetClosestJewel(int reachDistance) {
        setTimeStep(50);
		this.reachDistance=reachDistance;
        this.name = "GetClosestJewel";
	}

	@Override
	public void accessMemoryObjects() {
		closestJewelMO=(MemoryObject)this.getInput("CLOSEST_JEWEL");
		innerSenseMO=(MemoryObject)this.getInput("INNER");
		handsMO=(MemoryObject)this.getOutput("HANDS");
	}

	@Override
	public void proc() {
		String jewelName="";
		closestJewel = (Thing) closestJewelMO.getI();
		cis = (Idea) innerSenseMO.getI();

		if(closestJewel != null)
		{
			double jewelX=0;
			double jewelY=0;
			try {
				jewelX=closestJewel.getCenterPosition().getX();
				jewelY=closestJewel.getCenterPosition().getY();
                jewelName = closestJewel.getName();
			} catch (Exception e) {
				e.printStackTrace();
			}

			double selfX=(double)cis.get("position.x").getValue();
			double selfY=(double)cis.get("position.y").getValue();

			Point2D pJewel = new Point();
			pJewel.setLocation(jewelX, jewelY);

			Point2D pSelf = new Point();
			pSelf.setLocation(selfX, selfY);

			double distance = pSelf.distance(pJewel);
			JSONObject message=new JSONObject();
			try {
				if(distance<=reachDistance){ //get it						
					message.put("OBJECT", jewelName);
					message.put("ACTION", "PICKUP");
					handsMO.setI(message.toString());
                    activation=1.0;
				}else{
					handsMO.setI("");	//nothing
                    activation=0.0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			handsMO.setI("");	//nothing
                        activation=0.0;
		}
	}
        
	@Override
	public void calculateActivation() {
	
	}

}
