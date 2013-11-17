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
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

/** Un animal
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class Animal extends SituatedAgent<AnimalBody, SituatedObject, AnimalPerception, AnimalInfluence> {
	
	/** Deplace l'agent dans la direction indiquee.
	 * 
	 * @param direction  
	 * @return <code>true</code> si le mouvement est envoye.
	 */	
	protected boolean moveTo(final MoveDirection direction) {
		getAgentBody().influence(direction);
		setProbe("LAST_MOVE", direction); //$NON-NLS-1$
		return true;
	}
	
	/** Tire au hasard une direction.
	 */
	protected MoveDirection computeMove(boolean allow_no_move) {
		int tries = 0;
		MoveDirection[] values = MoveDirection.values();
		MoveDirection selected;
		do {
			int direction = (int)(Math.random() * values.length * 10.)/10 ;
			selected = values[direction];
			tries++;
		}
		while ((!allow_no_move)&&(selected==MoveDirection.NONE)&&(tries<10));
		return selected;
	}
	
	/** Tire au hasard une direction parmi celles fournies.
	 */
	protected MoveDirection computeMove(MoveDirection... allowedDirections) {
		int direction = (int)(Math.random() * allowedDirections.length * 10.)/10 ;
		return allowedDirections[direction];
	}
	
}