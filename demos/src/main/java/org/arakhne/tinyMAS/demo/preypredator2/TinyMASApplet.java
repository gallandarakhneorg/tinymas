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

import java.awt.BorderLayout;

import javax.swing.JApplet;

import org.arakhne.tinyMAS.core.Agent;

/** 
 * Afficheur de l'état du monde
 * <p>
 * Travaux pratiques d'IA54.<br>
 * Universit&eacute; de Technologie de Belfort-monb&eacute;liard.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class TinyMASApplet extends JApplet {

	private static final long serialVersionUID = 1L;

	private static final int SNAKE_COUNT = 5;
	private static final int LION_COUNT = 5;
	
	private static final int WORLD_SIZE_X = 10;
	private static final int WORLD_SIZE_Y = 10;
	
	volatile Game game;
	final GUI gui;
	
	public TinyMASApplet() {
		this.game = null;
		this.gui = new GUI();
		setLayout(new BorderLayout());
		add(this.gui, BorderLayout.CENTER);
	}
	
	void initGame() {
		this.game = new Game() ;
		
		this.game.setWaitingDuration(3000);
		
		WorldGrid g = new WorldGrid(WORLD_SIZE_X,WORLD_SIZE_Y) ;
		this.game.addAgent(g) ;

		Agent a = new Prey() ;		
		this.game.addAgent(a) ;
		g.addAgent(a.getId(),AnimalType.PREY) ;		 

		for(int i=0; i<SNAKE_COUNT; i++) {
			a = new Snake() ;		
			this.game.addAgent(a) ;
			g.addAgent(a.getId(),AnimalType.SNAKE) ;
		}

		for(int i=0; i<LION_COUNT; i++) {
			a = new Lion() ;		
			this.game.addAgent(a) ;
			g.addAgent(a.getId(),AnimalType.LION) ;
		}
		
		this.gui.setKernel(this.game);
		this.gui.launchRefresher();
		
		new Thread() {
			@Override
			public void run() {
				Game game = TinyMASApplet.this.game;
				game.run();
				destroyGame();
			}
		}.start();
	}
	
	void destroyGame() {
		this.gui.stopRefresher();
		this.gui.setKernel(null);
		this.game = null;
	}

	@Override
	public void destroy() {
		if (this.game==null) this.game.stop();
		super.destroy();
	}

	@Override
	public void start() {
		super.start();
		if (this.game==null) {
			initGame();
		}
		else {
			this.game.pause();
		}
	}

	@Override
	public void stop() {
		if (this.game!=null) {
			this.game.stop();
		}
		super.stop();
	}
	
	
	
}