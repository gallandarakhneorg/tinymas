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

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.arakhne.tinyMAS.core.Agent;
import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Message;

/** 
 * AGENT GRILLE.
 * L'environnement de notre simulation.
 * <p>
 * Travaux pratiques d'IA54.<br>
 * Universit&eacute; de Technologie de Belfort-monb&eacute;liard.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class WorldGrid extends Agent {

	private final AgentIdentifier[][] world;
	private final int width;
	private final int height;
	private AgentIdentifier prey;
	private final Map<AgentIdentifier,Dimension> locations = new TreeMap<AgentIdentifier,Dimension>();
	
	private final Map<AgentIdentifier,MoveDirection> awaitingMessages = new TreeMap<AgentIdentifier,MoveDirection>();
	
	/** Construit un monde.
	 * @param x taille du monde
	 * @param y taille du monde
	 */	
	public WorldGrid(int x, int y) {
		this.world = new AgentIdentifier[y][x];
		this.width = x;
		this.height = y;
	}
	
	/** Informe l'agent qu'il demarre sa vie.
	 *  <p>
	 *  Cette methode initialise les attributs de l'agent.
	 */
	@Override
	public void start() {
		forceIdentifierStringRepresentation();
		registerService("WORLD_GRID"); //$NON-NLS-1$
		setProbe("CATCHED", false); //$NON-NLS-1$
		setProbe("WIDTH", this.width); //$NON-NLS-1$
		setProbe("HEIGHT", this.height); //$NON-NLS-1$
		setStateProbe();
	}

	/** Ajoute aleatoirement un agent dans le monde.
	 * 
	 * @param id est l'identificateur de l'agent a ajouter.
	 * @param is_prey indique si l'ajout est la proie
	 */
	public void addAgent(AgentIdentifier id, boolean is_prey) {
		if ((is_prey)&&(this.prey!=null))
			throw new IllegalArgumentException("is_prey"); //$NON-NLS-1$
		
		int x, y;
		Random rnd = new Random();
		do {
			x = rnd.nextInt(this.width);
			y = rnd.nextInt(this.height);
		}
		while (this.world[y][x]!=null);
		
		this.world[y][x] = id;
		this.locations.put(id, new Dimension(x,y));
		if (is_prey) {
			this.prey = id;
			setProbe("PREY", id); //$NON-NLS-1$
		}
	}
	
	/** Traite les messages provenant des agents et deplace
	 *  les si cela est possible.
	 */
	@Override
	public void live() {
		while (hasMessage()) {
			Message msg = getNextMessage();
			if ((!this.awaitingMessages.containsKey(msg.FROM))&&
				(msg.CONTENT instanceof MoveDirection)) {
				this.awaitingMessages.put(msg.FROM, (MoveDirection)msg.CONTENT);
			}
			else {
				System.err.println("Le monde vient d'ignorer un message provenant de "+msg.FROM); //$NON-NLS-1$
			}
		}
		
		if (this.awaitingMessages.size()==this.locations.size()) {
			// Tous les agents ont envoye un message
			
			// Calcul les positions desirees par les agents
			Map<AgentIdentifier, Dimension> desiredPositions = new TreeMap<AgentIdentifier,Dimension>();
			for (Entry<AgentIdentifier,MoveDirection> entry : this.awaitingMessages.entrySet()) {
				desiredPositions.put(entry.getKey(), predictMove(entry.getKey(), entry.getValue()));
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
			for (Entry<Dimension,Set<AgentIdentifier>> entry : conflicts.entrySet()) {
				tab = new AgentIdentifier[entry.getValue().size()];
				entry.getValue().toArray(tab);
				idx = rnd.nextInt(tab.length);
				moveAgent(tab[idx], this.awaitingMessages.get(tab[idx]));
				for (AgentIdentifier agentId : tab) {
					desiredPositions.remove(agentId);
				}
			}
			
			// Changement de position des agents n'ayant pas provoque de conflit
			for (AgentIdentifier agentId : desiredPositions.keySet()) {
				moveAgent(agentId,this.awaitingMessages.get(agentId));
			}
						
			// Vide les tampons
			desiredPositions.clear();
			conflicts.clear();
			this.awaitingMessages.clear();
			
			setStateProbe();
			
			//debugPositions(this.width, this.height, this.world);
			
			// Verification de l'etat du jeu
			if (verifyifPreyCatched()) {
				setProbe("CATCHED", true); //$NON-NLS-1$
				System.out.println("The prey was catched"); //$NON-NLS-1$
				debugPositions(this.width, this.height, this.world);
				endOfGame();
			}
		}
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

	/** Fin du jeu
	 */
	private void endOfGame() {
		for (AgentIdentifier id : this.locations.keySet()) {
			sendMessage(id, GameMessage.END_OF_GAME);
		}
		killMe();
	}
	
	/** Modification de la sonde sur l'état du jeu
	 */
	private void setStateProbe() {
		Map<AgentIdentifier,Dimension> probedLocations = new TreeMap<AgentIdentifier,Dimension>();
		for (Entry<AgentIdentifier,Dimension> entry : this.locations.entrySet()) {
			probedLocations.put(entry.getKey(), 
					new Dimension(entry.getValue()));
		}
		setProbe("WORLD_STATE", probedLocations); //$NON-NLS-1$
	}
	
	/** Deplace un agent si possible.
	 */
	private void moveAgent(AgentIdentifier id, MoveDirection direction) {
		Dimension pos = this.locations.get(id);
		if (pos==null) return;

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
			(ny>=0)&&(ny<this.height)&&
			(this.world[ny][nx]==null)) {
			this.world[ny][nx] = id;
			this.world[y][x] = null;
			this.locations.put(id, new Dimension(nx,ny));
		}
	}
	
	/**
	 * Cette fonction teste si la proie est encerclé (bas,haut,gauche,droite) par des prédateurs
	 * @return vrai si la proie est encerclée
	 */
	private boolean verifyifPreyCatched() {
		if (this.prey==null) return false;
		
		// Recherche la proie
		Dimension pos = this.locations.get(this.prey);
		if (pos==null) return false;
		
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
		
		return catched==4;
	}
	
	@Override
	public String toString() {
		return "World"; //$NON-NLS-1$
	}
	
	protected static void debugPositions(int width, int height, AgentIdentifier[][] grid) {
		int[] size = new int[width];
		Arrays.fill(size, 0);
		
		for (int c=0; c<width; c++) {
			for (int r=0; r<height; r++) {
				if ((grid[r][c]!=null)&&(size[c]<grid[r][c].getString().length()))
					size[c] = grid[r][c].getString().length();
			}
		}
		
		for(int r=0; r<height; r++) {
			System.out.print("| "); //$NON-NLS-1$
			for(int c=0; c<width; c++) {
				int s = grid[r][c]==null ? 0 : grid[r][c].getString().length();
				if (grid[r][c]!=null) System.out.print(grid[r][c].getString());
				for(int k=s; k<size[c]; k++)
					System.out.print(" "); //$NON-NLS-1$
				System.out.print(" |"); //$NON-NLS-1$
			}
			System.out.println(""); //$NON-NLS-1$
		}
	}

}