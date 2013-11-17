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

package org.arakhne.tinyMAS.demo.preypredator2;

/** AGENT PREDATEUR
 * <p>
 * Travaux pratiques d'IA54.<br>
 * Universit&eacute; de Technologie de Belfort-monb&eacute;liard.
 * <p>
 * Un lion essaye de coincer une proie.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class Lion extends PerceptiveAgent {

	private static int PREDATOR_COUNT = 0;
	
	private final int number;
	
	public Lion() {
		this.number = PREDATOR_COUNT++;
	}
	
	@Override
	public String toString() {
		return "Lion #"+this.number; //$NON-NLS-1$
	}

	@Override
	public void start() {
		forceIdentifierStringRepresentation();
		setProbe("ANIMAL_TYPE", AnimalType.LION); //$NON-NLS-1$
	}

	/** L'agent predateur se deplace en fonction de ses perceptions.
	 */ 	
	@Override
	protected void live(PerceptionType[][] percepts) {
		// Analyse les perception afin de detecter la proie
		int preyX = 0, preyY = 0;
		boolean preyFound = false;
		for(int r=0; r<percepts.length; r++) {
			for(int c=0; c<percepts[r].length; c++) {
				if (percepts[r][c]==PerceptionType.PREY) {
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