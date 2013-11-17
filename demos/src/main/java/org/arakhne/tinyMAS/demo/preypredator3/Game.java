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

import org.arakhne.tinyMAS.core.Agent;
import org.arakhne.tinyMAS.core.Kernel;

/** JEU DE LA PROIE ET DES PREDATEURS.
 * <p>
 * Travaux pratiques d'IA54.<br>
 * Universit&eacute; de Technologie de Belfort-monb&eacute;liard.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
@SuppressWarnings("unchecked")
public class Game extends Kernel {

	private static final int SNAKE_COUNT = 5;
	private static final int LION_COUNT = 5;
	
	private static final int WORLD_SIZE_X = 10;
	private static final int WORLD_SIZE_Y = 10;
	
	public Game() {
		super();
	}
	
	/** Programme Principal
	 */	
	public static void main(String[] argv) {
		
		System.out.println("Prey and Predator demonstrator #3"); //$NON-NLS-1$
		System.out.println("Copyright (c) 2005-2009 Stéphane GALLAND and Nicolas GAUD "); //$NON-NLS-1$
		System.out.println("Thanks to Julia Nikolaeva aka. Flameia for the icons <flameia@zerobias.com>"); //$NON-NLS-1$
		
		Game j = new Game() ;
		
		j.setWaitingDuration(1500);
		
		WorldGrid g = new WorldGrid(WORLD_SIZE_X,WORLD_SIZE_Y) ;
		j.setEnvironment(g) ;

		Agent a = new Rabbit() ;		
		j.addAgent(a) ;

		for(int i=0; i<SNAKE_COUNT; i++) {
			a = new Snake() ;		
			j.addAgent(a) ;
		}

		for(int i=0; i<LION_COUNT; i++) {
			a = new Lion() ;		
			j.addAgent(a) ;
		}

		GUI gui = new GUI(j);
		gui.setVisible(true);
		
		j.run() ;
		
		// Force quit
		System.exit(0);
		
	}

}