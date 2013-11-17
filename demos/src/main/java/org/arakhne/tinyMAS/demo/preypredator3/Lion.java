/* 
 * $Id$
 * 
 * Copyright (C) 2004-2007 St&eacute;phane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */

package org.arakhne.tinyMAS.demo.preypredator3;

import org.arakhne.tinyMAS.situatedEnvironment.agent.SituatedAgent;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedEnvironment;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

/** Un lion
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class Lion extends PerceptiveAnimal {

	@Override
	public AnimalBody createDefaultBody(SituatedEnvironment<? extends SituatedAgent<AnimalBody, SituatedObject, AnimalPerception, AnimalInfluence>, AnimalBody, SituatedObject, AnimalPerception, AnimalInfluence> in) {
		return new AnimalBody(this,AnimalType.LION);
	}

	@Override
	protected void doDecision(PerceptionType[][] percepts) {
		// Analyse les perception afin de detecter la proie
		int preyX = 0, preyY = 0;
		boolean preyFound = false;
		for(int r=0; r<percepts.length; r++) {
			for(int c=0; c<percepts[r].length; c++) {
				if (percepts[r][c]==PerceptionType.RABBIT) {
					preyX = -2+c;
					preyY = -2+r;
					preyFound = true;
					break;
				}
			}
		}
		
		MoveDirection direction;

		if (!preyFound) {
			// Le lion se déplace au hasard
			direction = computeMove(true);
		}
		else if ((preyX==0)&&(preyY<-1)) {
			// Le lion suit la proie 
			direction = MoveDirection.UP;
		}
		else if ((preyX==0)&&(preyY>1)) {
			// Le lion suit la proie 
			direction = MoveDirection.DOWN;
		}
		else if ((preyX<-1)&&(preyY==0)) {
			// Le lion suit la proie 
			direction = MoveDirection.LEFT;
		}
		else if ((preyX>1)&&(preyY==0)) {
			// Le lion suit la proie 
			direction = MoveDirection.RIGHT;
		}
		else if ((preyX<0)&&(preyY<0)) {
			// Le lion suit la proie 
			direction = computeMove(MoveDirection.LEFT, MoveDirection.UP);
		}
		else if ((preyX<0)&&(preyY>0)) {
			// Le lion suit la proie 
			direction = computeMove(MoveDirection.LEFT, MoveDirection.DOWN);
		}
		else if ((preyX>0)&&(preyY<0)) {
			// Le lion suit la proie 
			direction = computeMove(MoveDirection.RIGHT, MoveDirection.UP);
		}
		else if ((preyX>0)&&(preyY>0)) {
			// Le lion suit la proie 
			direction = computeMove(MoveDirection.RIGHT, MoveDirection.DOWN);
		}
		else {
			// Le lion se trouve prêt de la proie, il ne veut pas bouger
			direction = MoveDirection.NONE;
		}

		/*drawPercepts(percepts);
		
		System.out.print("found prey="+preyFound);
		System.out.print(";preyX="+preyX);
		System.out.print(";preyY="+preyY);
		System.out.println(";direction="+direction);*/
		
		moveTo(direction);
	}

	
}