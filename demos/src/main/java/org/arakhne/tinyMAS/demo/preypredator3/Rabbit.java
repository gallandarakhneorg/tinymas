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

import java.util.ArrayList;
import java.util.Arrays;

import org.arakhne.tinyMAS.situatedEnvironment.agent.SituatedAgent;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedEnvironment;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

/** Un lapin
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class Rabbit extends PerceptiveAnimal {

	@Override
	public AnimalBody createDefaultBody(SituatedEnvironment<? extends SituatedAgent<AnimalBody, SituatedObject, AnimalPerception, AnimalInfluence>, AnimalBody, SituatedObject, AnimalPerception, AnimalInfluence> in) {
		return new AnimalBody(this,AnimalType.RABBIT);
	}

	@Override
	protected void doDecision(PerceptionType[][] percepts) {
		
		//drawPercepts(percepts);

		int[][] dangerMatrix = computeDanger(percepts);
		
		//drawDangerMatrix(dangerMatrix);

		int[] dangers = new int[] {
				percepts[1][2]==PerceptionType.THENEANT ? Integer.MAX_VALUE : dangerMatrix[1][2], // haut
				percepts[3][2]==PerceptionType.THENEANT ? Integer.MAX_VALUE : dangerMatrix[3][2], // bas
				percepts[2][1]==PerceptionType.THENEANT ? Integer.MAX_VALUE : dangerMatrix[2][1], // left
				percepts[2][3]==PerceptionType.THENEANT ? Integer.MAX_VALUE : dangerMatrix[2][3], // right
		};
		
		ArrayList<Integer> mins = new ArrayList<Integer>();
		int minDanger = dangers[0];
		mins.add(0);
		for(int i=1; i<4; i++) {
			if (minDanger>dangers[i]) {
				minDanger = dangers[i];
				mins.clear();
				mins.add(i);
			}
			else if (minDanger==dangers[i]) {
				mins.add(i);
			}
		}
		
		MoveDirection[] directions = new MoveDirection[mins.size()];
		for(int i=0; i<mins.size(); i++) {
			switch(mins.get(i)) {
			case 0:
				directions[i] = MoveDirection.UP;
				break;
			case 1:
				directions[i] = MoveDirection.DOWN;
				break;
			case 2:
				directions[i] = MoveDirection.LEFT;
				break;
			case 3:
				directions[i] = MoveDirection.RIGHT;
				break;
			default:
				directions[i] = MoveDirection.NONE;
				break;
			}
		}
		
		moveTo(computeMove(directions));
	}	
	
	private int computeLength(PerceptionType[][] percepts) {
		int max = 0;
		for(int r=0; r<percepts.length; r++) {
			if (max<percepts[r].length)
				max = percepts[r].length;
		}
		return max;
	}
	
	private void fillDangerGrid(int[][] grid, int x, int y, int maxX, int maxY, int cellDanger) {
		assert(x>=0 && x<maxX);
		assert(y>=0 && y<maxY);
		
		if (grid[y][x]<cellDanger) {
			grid[y][x] = cellDanger;
			if (x>0) fillDangerGrid(grid,x-1,y,maxX,maxY,cellDanger-1);
			if (x<maxX-1) fillDangerGrid(grid,x+1,y,maxX,maxY,cellDanger-1);
			if (y>0) fillDangerGrid(grid,x,y-1,maxX,maxY,cellDanger-1);
			if (y<maxY-1) fillDangerGrid(grid,x,y+1,maxX,maxY,cellDanger-1);
		}
	}
		
	private int[][] computeDanger(PerceptionType[][] percepts) {
		int size = computeLength(percepts);
		int maxDistance = size*percepts.length;

		// Initialize
		int[][] danger = new int[percepts.length][size];		
		for(int r=0; r<percepts.length; r++) {
			danger[r] = new int[percepts[r].length];
			Arrays.fill(danger[r], 0);
		}
		
		// Compute the dangerousity of each cell
		for(int r=0; r<percepts.length; r++) {
			for(int c=0; c<percepts[r].length; c++) {
				if (percepts[r][c]!=null) {
					int cellDanger = (percepts[r][c]==PerceptionType.THENEANT) ? maxDistance/2 : maxDistance;
					fillDangerGrid(danger,c,r,size,percepts.length,cellDanger);
				}
			}
		}
		
		
		return danger;
	}

}