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

package org.arakhne.tinyMAS.demo.preypredator1;

import org.arakhne.tinyMAS.core.Message;

/** AGENT PREDATEUR
 * <p>
 * Travaux pratiques d'IA54.<br>
 * Universit&eacute; de Technologie de Belfort-monb&eacute;liard.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class Predator extends MovableAgent {

	private static int PREDATOR_COUNT = 0;
	
	private final int number;
	
	public Predator() {
		this.number = PREDATOR_COUNT++;
	}
	
	@Override
	public String toString() {
		return "Predator #"+this.number; //$NON-NLS-1$
	}

	@Override
	public void start() {
		forceIdentifierStringRepresentation();
	}

	/** L'agent predateur se deplace aleatoirement.
	 */ 	
	@Override
	public void live() {
		boolean eog = false;
		
		while (hasMessage()) {
			Message msg = getNextMessage();
			if (msg.CONTENT instanceof GameMessage) {
				GameMessage gmsg = (GameMessage)(msg.CONTENT);
				if (gmsg==GameMessage.END_OF_GAME) {
					eog = true;
				}
			}
		}

		if (eog) {
			killMe();
		}
		else {
			moveTo(computeMove(true));
		}
	}
	
}