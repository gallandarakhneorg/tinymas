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

import org.arakhne.tinyMAS.core.Message;

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
public abstract class PerceptiveAgent extends MovableAgent {
	
	/** L'agent predateur se deplace en fonction de ses perceptions.
	 */ 	
	@Override
	public final void live() {
		boolean eog = false;
		PerceptionType[][] percepts = new PerceptionType[5][5];
		
		while (hasMessage()) {
			Message msg = getNextMessage();
			if (msg.CONTENT instanceof GameMessage) {
				GameMessage gmsg = (GameMessage)(msg.CONTENT);
				if (gmsg==GameMessage.END_OF_GAME) {
					eog = true;
				}
			}
			else if (msg.CONTENT instanceof PerceptionType[][]) {
				PerceptionType[][] worldPercepts = (PerceptionType[][])(msg.CONTENT);
				for(int r=0; (r<worldPercepts.length)&&(r<5); r++) {
					for(int c=0; (c<worldPercepts[r].length)&&(c<5); c++) {
						percepts[r][c] = worldPercepts[r][c];
					}
				}
				percepts[2][2] = null;
			}
		}

		if (eog) {
			killMe();
		}
		else {
			live(percepts);
		}
	}

	/** L'agent predateur se deplace en fonction de ses perceptions.
	 */ 	
	protected abstract void live(PerceptionType[][] percepts);

	/** Draw the specified percepts.
	 */
	protected void drawPercepts(PerceptionType[][] percepts) {
		System.out.println(getId().getString());
		for(int r=0; r<5; r++) {
			for(int c=0; c<5; c++) {
				System.out.print("\t"+percepts[r][c]); //$NON-NLS-1$
			}
			System.out.print("\n"); //$NON-NLS-1$
		}
	}
	
}