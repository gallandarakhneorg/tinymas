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

import org.arakhne.tinyMAS.core.Agent;
import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Kernel;

/** Un agent capable de se deplacer dans le monde du
 *  jeu "Proie et Predateurs".
 * <p>
 * Travaux pratiques d'IA54.<br>
 * Universit&eacute; de Technologie de Belfort-monb&eacute;liard.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class MovableAgent extends Agent {

	/** Deplace l'agent dans la direction indiquee.
	 * 
	 * @param direction  
	 * @return <code>true</code> si le mouvement est envoye.
	 */	
	protected boolean moveTo(MoveDirection direction) {
		// Get the agent that provide a world grid service 
		AgentIdentifier id = Kernel.getSingleton().getYellowPageSystem().getAgentFor("WORLD_GRID"); //$NON-NLS-1$
		assert(id!=null);
		// Send the message to the agent
		setProbe("LAST_MOVE", direction); //$NON-NLS-1$
		return sendMessage(id, direction) ;
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