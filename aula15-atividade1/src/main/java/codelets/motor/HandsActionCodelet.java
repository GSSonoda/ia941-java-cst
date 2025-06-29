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

package codelets.motor;


import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;

/**
 *  Hands Action Codelet monitors working storage for instructions and acts on the World accordingly.
 *  
 * @author klaus
 *
 */


public class HandsActionCodelet extends Codelet{

	private Memory handsMO;
	private Memory knownJewelsMO;
	private Memory knownApplesMO;
	private Memory closestAppleMO;
	private Memory closestJewelMO;
	private String previousHandsAction="";
	private Creature c;
	private Random r = new Random();
	static Logger log = Logger.getLogger(HandsActionCodelet.class.getCanonicalName());
	private List<Thing> knownJewels;
	private List<Thing> knownApples;
	private Thing closestApple;
	private Thing closestJewel;

	public HandsActionCodelet(Creature nc) {
                c = nc;
                this.name = "HandsActionCodelet";
	}
	
    @Override
	public void accessMemoryObjects() {
		handsMO=(MemoryObject)this.getInput("HANDS");
		knownJewelsMO=(MemoryObject)this.getOutput("KNOWN_JEWELS");
		knownApplesMO=(MemoryObject)this.getOutput("KNOWN_APPLES");
		closestJewelMO=(MemoryObject)this.getOutput("CLOSEST_JEWEL");
		closestAppleMO=(MemoryObject)this.getOutput("CLOSEST_APPLE");
	}

	public void proc() {
            
        String command = (String) handsMO.getI();
		knownJewels = (List<Thing>) knownJewelsMO.getI();
		knownApples = (List<Thing>) knownApplesMO.getI();
		closestJewel = (Thing) closestJewelMO.getI();
		closestApple = (Thing) closestAppleMO.getI();

		// if(!command.equals("") && (!command.equals(previousHandsAction))){
		if(!command.equals("")){
			JSONObject jsonAction;
			try {
				jsonAction = new JSONObject(command);
				if(jsonAction.has("ACTION") && jsonAction.has("OBJECT")){
					String action=jsonAction.getString("ACTION");
					String objectName=jsonAction.getString("OBJECT");
					if(action.equals("PICKUP")){
						try {
							System.out.println("PICKUP " + objectName);
							System.out.println("closestJewel.getName(): " + closestJewel.getName());
							c.putInSack(objectName);

							Thread.sleep(2000);
							System.out.println("DESTROY - closestJewel.getName(): " + closestJewel.getName());
							destroyThingByName(knownJewels, objectName);

							if (closestJewel != null && closestJewel.getName().equals(objectName)) {
								closestJewel = null;
							}
						} catch (Exception e) {
						}
					}
					if(action.equals("EATIT")){
						try {
							c.eatIt(objectName);
							System.out.println("EATIT " + objectName);
							System.out.println("closestApple.getName(): " + closestApple.getName());

							Thread.sleep(2000);
							System.out.println("DESTROY - closestApple.getName(): " + closestApple.getName());
							destroyThingByName(knownApples, objectName);
							if (closestApple != null && closestApple.getName().equals(objectName)) {
								closestApple = null;
							}
						} catch (Exception e) {
						}						
					}
					if(action.equals("BURY")){
						try {
							c.hideIt(objectName);
						} catch (Exception e) {
							
						}
						log.info("Sending Bury command to agent:****** "+objectName+"**********");							
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		previousHandsAction = (String) handsMO.getI();

		knownJewelsMO.setI(knownJewels);
		knownApplesMO.setI(knownApples);
		closestJewelMO.setI(closestJewel);
		closestAppleMO.setI(closestApple);
	}//end proc

    @Override
    public void calculateActivation() {
        
    }

	private List<Thing> destroyThingByName(List<Thing> things, String thingName) {
		synchronized(things) {
			things.removeIf(t -> t.getName().equals(thingName));
		}
		return things;
	}

}
