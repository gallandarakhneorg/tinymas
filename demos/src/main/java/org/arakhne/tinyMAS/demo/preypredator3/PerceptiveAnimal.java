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

import java.util.List;

/** Un animal ayant des perceptions.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class PerceptiveAnimal extends Animal {
	

	@Override
	protected void doDecisionAndAction() {
		PerceptionType[][] percepts = new PerceptionType[5][5];

		List<AnimalPerception> rawPercepts = getPerceptionFilter().getPerceivedObjects();
		
		if ((rawPercepts!=null)&&(rawPercepts.size()>0)) {
			PerceptionType[][] worldPercepts = rawPercepts.get(0).getData();
			for(int r=0; (r<worldPercepts.length)&&(r<5); r++) {
				for(int c=0; (c<worldPercepts[r].length)&&(c<5); c++) {
					percepts[r][c] = worldPercepts[r][c];
				}
			}
			percepts[2][2] = null;
		}

		doDecision(percepts);
	}	
	
	/** L'agent predateur se deplace en fonction de ses perceptions.
	 */ 	
	protected abstract void doDecision(PerceptionType[][] percepts);

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