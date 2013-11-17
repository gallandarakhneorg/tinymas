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

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.ConstantStepTimeManager;
import org.arakhne.tinyMAS.situatedEnvironment.environment.AbstractSituatedEnvironment;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;

/** 
 * GRILLE.
 * L'environnement de notre simulation.
 * <p>
 * Travaux pratiques d'IA54.<br>
 * Universit&eacute; de Technologie de Belfort-monb&eacute;liard.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class WorldGrid extends AbstractSituatedEnvironment<Animal, AnimalBody, SituatedObject, AnimalPerception, AnimalInfluence> {

	private final AnimalBody[][] world;
	private final int width;
	private final int height;
	private AnimalBody prey;
	private final Map<AgentIdentifier,Dimension> locations = new TreeMap<AgentIdentifier,Dimension>();
	private final Map<AgentIdentifier,AnimalType> types = new TreeMap<AgentIdentifier,AnimalType>();
	private final Map<AgentIdentifier,AnimalBody> bodies = new TreeMap<AgentIdentifier,AnimalBody>();

	
	public WorldGrid(int x, int y) {
		super(new ConstantStepTimeManager(1, TimeUnit.SECONDS));
		this.world = new AnimalBody[y][x];
		this.width = x;
		this.height = y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Map<AgentIdentifier,Dimension> getState() {
		return new TreeMap<AgentIdentifier,Dimension>(this.locations);
	}

	@Override
	protected void onAgentBodyAdded(AnimalBody body) {
		if ((body.getType()==AnimalType.RABBIT)&&(this.prey!=null))
			throw new IllegalArgumentException("is_prey"); //$NON-NLS-1$
		
		int x, y;
		Random rnd = new Random();
		do {
			x = rnd.nextInt(this.width);
			y = rnd.nextInt(this.height);
		}
		while (this.world[y][x]!=null);
		
		this.world[y][x] = body;
		this.locations.put(body.getAgent(), new Dimension(x,y));
		this.types.put(body.getAgent(), body.getType());
		this.bodies.put(body.getAgent(),body);
		if (body.getType()==AnimalType.RABBIT) {
			this.prey = body;
		}
	}

	@Override
	protected void onAgentBodyRemoved(AnimalBody body) {
		//
	}

	public AnimalPerception[] perceive(AgentIdentifier agent) {
		Dimension position = this.locations.get(agent);
			
		PerceptionType[][] percepts = new PerceptionType[5][5];
		for(int r=0; r<5; r++)
			Arrays.fill(percepts[r], PerceptionType.THENEANT);
		
		for(int r=Math.max(0, position.height-2); r<=Math.min(this.height-1,position.height+2); r++) {
			int animalRow = r-position.height+2;
			for(int c=Math.max(0, position.width-2); c<=Math.min(this.width-1,position.width+2); c++) {
				if (this.world[r][c]!=null) {
					percepts[animalRow][c-position.width+2] = 
						PerceptionType.fromAnimalType(this.types.get(this.world[r][c].getAgent()));
				}
				else {
					percepts[animalRow][c-position.width+2] = null;
				}
			}
		}
		
		return new AnimalPerception[] {new AnimalPerception(percepts)};
	}

	/** Calcul la position de la cellule dans laquelle un agent veut se deplacer.
	 */
	private Dimension predictMove(AgentIdentifier id, MoveDirection direction) {
		Dimension pos = this.locations.get(id);
		assert(pos!=null);

		int x = pos.width;
		int y = pos.height;
		int nx = x;
		int ny = y;
		
		switch(direction) {
		case UP:
			ny--;
			break;
		case DOWN:
			ny++;
			break;
		case LEFT:
			nx--;
			break;
		case RIGHT:
			nx++;
			break;
		case NONE:
			break;
		}

		if (((nx!=x)||(ny!=y))&&
			(nx>=0)&&(nx<this.width)&&
			(ny>=0)&&(ny<this.height)) {
			x = nx;
			y = ny;
		}
		
		return new Dimension(x,y);
	}

	@Override
	protected boolean applyInfluences(Collection<AnimalInfluence> influences) {
		// Calcul les positions desirees par les agents
		Map<AgentIdentifier, Dimension> desiredPositions = new TreeMap<AgentIdentifier,Dimension>();
		for (AnimalInfluence influence : influences) {
			AgentIdentifier id = influence.getEmitter();
			desiredPositions.put(id, predictMove(id, influence.getDirection()));
		}
			
		// Detection de conflits
		Map<Dimension, Set<AgentIdentifier>> conflicts = new HashMap<Dimension,Set<AgentIdentifier>>();
		for (Entry<AgentIdentifier,Dimension> entry1 : desiredPositions.entrySet()) {
			for (Entry<AgentIdentifier,Dimension> entry2 : desiredPositions.entrySet()) {
				if (!entry1.getKey().equals(entry2.getKey())) {
					if (entry1.getValue().equals(entry2.getValue())) {
						// Conflit
						Set<AgentIdentifier> conflictedAgents = conflicts.get(entry1.getValue());
						if (conflictedAgents==null) {
							conflictedAgents = new HashSet<AgentIdentifier>();
							conflicts.put(entry1.getValue(), conflictedAgents);
						}
						conflictedAgents.add(entry1.getKey());
						conflictedAgents.add(entry2.getKey());
					}
				}
			}
		}
		
		// Resolution des conflits
		// Choix aleatoire d'un des agents
		Random rnd = new Random();
		int idx;
		AgentIdentifier[] tab;
		Dimension current, nouvel;
		for (Entry<Dimension,Set<AgentIdentifier>> entry : conflicts.entrySet()) {
			tab = new AgentIdentifier[entry.getValue().size()];
			entry.getValue().toArray(tab);
			idx = rnd.nextInt(tab.length);
			
			current = this.locations.get(tab[idx]);
			nouvel = desiredPositions.get(tab[idx]);
			applyReaction(tab[idx],current.width, current.height,nouvel.width, nouvel.height);
			
			for (AgentIdentifier agentId : tab) {
				desiredPositions.remove(agentId);
			}
		}

		// Changement de position des agents n'ayant pas provoque de conflit
		for (Entry<AgentIdentifier,Dimension> entry : desiredPositions.entrySet()) {
			current = this.locations.get(entry.getKey());
			nouvel = entry.getValue();
			applyReaction(entry.getKey(),current.width, current.height,nouvel.width, nouvel.height);
		}
		
		conflicts.clear();
		desiredPositions.clear();
		
		analyzeEnvironmentState();
		
		return true;
	}

	private void applyReaction(AgentIdentifier id, int x, int y, int nx, int ny) {
		if (((nx!=x)||(ny!=y))&&
			(nx>=0)&&(nx<this.width)&&
			(ny>=0)&&(ny<this.height)&&
			(this.world[ny][nx]==null)) {
			this.world[ny][nx] = this.bodies.get(id);
			this.world[y][x] = null;
			this.locations.put(id, new Dimension(nx,ny));
		}			
	}

	private void analyzeEnvironmentState() {
		if (this.prey==null) return;
		
		// Recherche la proie
		Dimension pos = this.locations.get(this.prey.getAgent());
		if (pos==null) return;
		
		int xPrey = pos.width;
		int yPrey = pos.height;
		
		assert(xPrey>=0 && xPrey<this.width);
		assert(yPrey>=0 && yPrey<this.height);
		
		// Test des quatres directions
		int catched = 0;
		
		// Haut
		if ((yPrey<=0)||(this.world[yPrey-1][xPrey]!=null))
			catched++;
		// Bas
		if ((yPrey>=(this.height-1))||(this.world[yPrey+1][xPrey]!=null))
			catched++;
		// Gauchge
		if ((xPrey<=0)||(this.world[yPrey][xPrey-1]!=null))
			catched++;
		// Bas
		if ((xPrey>=(this.width-1))||(this.world[yPrey][xPrey+1]!=null))
			catched++;
		
		if (catched==4) {
			killMe();
		}
	}

}