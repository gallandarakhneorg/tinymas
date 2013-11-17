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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.KernelAdapter;
import org.arakhne.tinyMAS.core.Probe;
import org.arakhne.tinyMAS.core.ProbeValueNotDefinedException;

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
public class GUI extends JFrame {

	private static final long serialVersionUID = 3939643459108505034L;
	
	protected static final int ANIMATION_SPEED = 1;
	protected static final int ANIMATION_STEPS = 10;
	
	protected static final Icon RABBIT_ICON;
	protected static final Icon LION_ICON;	
	protected static final Icon SNAKE_ICON;	
	protected static final Icon UP_ICON;
	protected static final Icon DOWN_ICON;	
	protected static final Icon LEFT_ICON;
	protected static final Icon RIGHT_ICON;	
	protected static final int ICON_WIDTH;
	protected static final int ICON_HEIGHT;
	
	static {
		URL url = GUI.class.getResource("/org/arakhne/tinyMAS/demo/preypredator1/rabbit.png"); //$NON-NLS-1$
		assert(url!=null);
		RABBIT_ICON = new ImageIcon(url);
		url = GUI.class.getResource("/org/arakhne/tinyMAS/demo/preypredator1/lion.png"); //$NON-NLS-1$
		assert(url!=null);
		LION_ICON = new ImageIcon(url);
		url = GUI.class.getResource("/org/arakhne/tinyMAS/demo/preypredator1/snake.png"); //$NON-NLS-1$
		assert(url!=null);
		SNAKE_ICON = new ImageIcon(url);
		url = GUI.class.getResource("/org/arakhne/tinyMAS/demo/preypredator1/go-up.png"); //$NON-NLS-1$
		assert(url!=null);
		UP_ICON = new ImageIcon(url);
		url = GUI.class.getResource("/org/arakhne/tinyMAS/demo/preypredator1/go-down.png"); //$NON-NLS-1$
		assert(url!=null);
		DOWN_ICON = new ImageIcon(url);
		url = GUI.class.getResource("/org/arakhne/tinyMAS/demo/preypredator1/go-previous.png"); //$NON-NLS-1$
		assert(url!=null);
		LEFT_ICON = new ImageIcon(url);
		url = GUI.class.getResource("/org/arakhne/tinyMAS/demo/preypredator1/go-next.png"); //$NON-NLS-1$
		assert(url!=null);
		RIGHT_ICON = new ImageIcon(url);
		
		ICON_WIDTH = Math.max(SNAKE_ICON.getIconWidth(),
				Math.max(RABBIT_ICON.getIconWidth(), LION_ICON.getIconWidth()));
		ICON_HEIGHT = Math.max(SNAKE_ICON.getIconHeight(),
				Math.max(RABBIT_ICON.getIconHeight(), LION_ICON.getIconHeight()));
	}
	
    public static Image mergeImages(Image top_icon, Image bottom_icon, int x, int y) {
    	int imgWidth = bottom_icon.getWidth(null);
    	int imgHeight = bottom_icon.getHeight(null);
    	BufferedImage img = new BufferedImage(
    			imgWidth, imgHeight,
    			Transparency.BITMASK);
    	Graphics g = img.getGraphics();
    	g.setClip(0,0,imgWidth,imgHeight);
    	g.drawImage(bottom_icon,0,0,null);
    	if (x<0) x = imgWidth + x;
    	if (y<0) y = imgHeight + y;
    	g.drawImage(top_icon,x,y,null);
    	return Toolkit.getDefaultToolkit().createImage(img.getSource());
    }

    public static Icon mergeIcons(Icon top_icon, Icon bottom_icon, int x, int y) {
        if ((top_icon instanceof ImageIcon)&&(bottom_icon instanceof ImageIcon)) {
        	Image new_image = mergeImages(
    				((ImageIcon)top_icon).getImage(),
    				((ImageIcon)bottom_icon).getImage(),
    				x, y);
        	if (new_image!=null)
        		return new ImageIcon(new_image);        	
        }
        return null;
    }                       

    protected final Grid grid;
	protected final RefreshThread refresher;
	private final WeakReference<Kernel<?,?,?,?>> kernel;	
	protected Map<AgentIdentifier,Dimension> positions;
	protected Map<AgentIdentifier,Dimension> nextPositions;
	
	public GUI(Kernel<?,?,?,?> kernel) {
		this.refresher = new RefreshThread();
		
		setTitle("Prey and Predators v3"); //$NON-NLS-1$
		this.kernel = new WeakReference<Kernel<?,?,?,?>>(kernel);
		
		Container content = getContentPane();
		
		content.setLayout(new BorderLayout());
		
		this.grid = new Grid();

		JScrollPane scroll = new JScrollPane(this.grid);
		content.add(BorderLayout.CENTER,scroll);
		
		JButton closeBt = new JButton("Quit"); //$NON-NLS-1$
		closeBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getKernel().stop();
			}
		});
		content.add(BorderLayout.SOUTH, closeBt);
		
		setPreferredSize(new Dimension(11*50,12*50));
		
		kernel.addKernelListener(new KernelAdapter() {
			@Override
			public void kernelRefreshAllowed(Kernel<?, ?, ?, ?> kernel) {
				if (kernel==GUI.this.getKernel()) {
					changeState();
				}
			}
			@Override
			public void kernelStarted(Kernel<?, ?, ?, ?> kernel) {
				if (kernel==GUI.this.getKernel()) {
					changeState();
				}
			}			
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Kernel<?,?,?,?> kernel = GUI.this.getKernel();
				kernel.stop();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				Kernel<?,?,?,?> kernel = GUI.this.getKernel();
				kernel.stop();
			}
		});
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		Executors.newSingleThreadExecutor().execute(this.refresher);
		
		pack();
	}
	
	protected WorldGrid getEnvironment() {
		return (WorldGrid)getKernel().getEnvironment();
	}
	
	protected void changeState() {
		WorldGrid world = getEnvironment();
		if (world!=null) {
			this.refresher.addMoves(world.getState());
		}
	}
	
	protected void refreshGUI(Map<AgentIdentifier,Dimension> moves) {
		this.positions = moves;
		this.grid.setStep(-1);
		this.grid.repaint();
	}

	protected void refreshGUI(int moveStep, Map<AgentIdentifier,Dimension> moves) {
		this.nextPositions = moves;
		this.grid.setStep(moveStep);
		this.grid.repaint();
	}
	
	protected void refreshGUI(int moveStep) {
		if (moveStep<0) {
			this.positions = this.nextPositions;
			//debugPositions(getKernel(), this.positions, "NEW POSITION"); //$NON-NLS-1$
		}
		this.grid.setStep(moveStep);
		this.grid.repaint();
	}
	
	protected Kernel<?,?,?,?> getKernel() {
		return this.kernel.get();
	}
	
	protected static void debugPositions(Kernel<?,?,?,?> kernel, Map<AgentIdentifier,Dimension> positions, String str) {
		AgentIdentifier[] world = kernel.getServiceProviders("WORLD_GRID"); //$NON-NLS-1$
		if (world.length>0) {
			Probe probe = kernel.getProbe(world[0]);
			try {
				int width = probe.getProbeInt("WIDTH"); //$NON-NLS-1$
				int height = probe.getProbeInt("HEIGHT"); //$NON-NLS-1$
				
				int[] size = new int[width];
				Arrays.fill(size, 0);
				
				AgentIdentifier[][] grid = new AgentIdentifier[height][width];
				for (Entry<AgentIdentifier, Dimension> entry : positions.entrySet()) {
					int h = entry.getValue().height;
					int w = entry.getValue().width;
					assert(grid[h][w]==null);
					grid[h][w] = entry.getKey();
					if (size[w]<grid[h][w].getString().length())
						size[w] = grid[h][w].getString().length();
				}
				
				System.out.println(">>"+str+"<<"); //$NON-NLS-1$ //$NON-NLS-2$
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
				
				return;
			}
			catch(ProbeValueNotDefinedException _) {
				//
			}			
		}
		throw new Error("UNABLE TO OBTAIN A PROBE"); //$NON-NLS-1$
	}
	
	@Deprecated
	private class RefreshThread implements Runnable {

		private final Queue<Map<AgentIdentifier,Dimension>> queue = new LinkedList<Map<AgentIdentifier,Dimension>>(); 
		
		private int moveStep = -2;

		public RefreshThread() {
			//
		}
		
		public void run() {
			while (true) {
				if (this.moveStep<0) {
					Map<AgentIdentifier,Dimension> moves = this.queue.poll();
					if (moves!=null) {
						if (this.moveStep==-1) {
							this.moveStep = 0;
							GUI.this.refreshGUI(this.moveStep, moves);
						}
						else if (this.moveStep==-2) {
							this.moveStep = -1;
							GUI.this.refreshGUI(moves);
						}
					}
				}
				else {
					try {
						Thread.sleep((1000*ANIMATION_SPEED)/ANIMATION_STEPS);
					}
					catch (InterruptedException e) {
						//
					}
					if (this.moveStep>=ANIMATION_STEPS) {
						this.moveStep = -1;
					}
					else {
						this.moveStep++;
					}
					GUI.this.refreshGUI(this.moveStep);
				}
				Thread.yield();
			}
		}
		
		public void addMoves(Map<AgentIdentifier,Dimension> moves) {
			if (moves!=null) {
				Map<AgentIdentifier,Dimension> old = this.queue.peek();
				if ((old==null)||(!old.equals(moves))) {
					this.queue.offer(moves);
				}
			}
		}
		
	}
	
	@Deprecated
	private class Grid extends JPanel {
		
		private static final long serialVersionUID = 4361140442107583935L;
		
		private int moveStep = -1;
		
		public Grid() {
			//
		}
		
		public void setStep(int step) {
			this.moveStep = step;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D)g;
			
			Dimension currentDim = getPreferredSize();
			Dimension desiredDim = computeDesiredDim();
			
			if ((desiredDim!=null)&&(!currentDim.equals(desiredDim))) {
				setPreferredSize(desiredDim);
				revalidate();
				repaint();
				return;
			}
			
			if (this.moveStep>=0) {
				drawMovingAgents(g2d, currentDim);
			}
			else {
				drawAgents(g2d, currentDim);					
			}
			
			drawGrid(g2d, currentDim);
		}
		
		private void drawMovingAgents(Graphics2D g2d, Dimension currentDim) {
			if ((this.moveStep<0)||
				(GUI.this.nextPositions==null)||
				(GUI.this.positions==null)) drawAgents(g2d, currentDim);
			
			int x, y, deltaX, deltaY;
			Dimension d;
			Dimension nextD;
			AgentIdentifier id;
			
			deltaX = ((this.moveStep * ICON_WIDTH) / ANIMATION_STEPS);
			deltaY = ((this.moveStep * ICON_HEIGHT) / ANIMATION_STEPS);
			
			Icon ic;

			for (Entry<AgentIdentifier, Dimension> entry : GUI.this.positions.entrySet()) {
				id = entry.getKey();
				d = entry.getValue();
				WorldGrid grid = getEnvironment();
				if ((grid!=null)&&(GUI.this.nextPositions.containsKey(id))) {
					nextD = GUI.this.nextPositions.get(id);
					x = d.width * ICON_WIDTH;
					y = d.height * ICON_HEIGHT;
					
					if (nextD.width<d.width) {
						x -= deltaX;
					}
					else if (nextD.width>d.width) {
						x += deltaX;
					}
										
					if (nextD.height<d.height) {
						y -= deltaY;
					}
					else if (nextD.height>d.height) {
						y += deltaY;
					}

					ic = getIcon(grid.getAgentBody(id).getType(), getLastDirection(id));
					assert(ic!=null);
					ic.paintIcon(this, g2d, x, y);
				}
			}
		}
		
		private void drawAgents(Graphics2D g2d, Dimension currentDim) {
			if (GUI.this.positions==null) return;
			int x, y;
			Dimension d;
			AgentIdentifier id;
			Icon ic;
			WorldGrid grid = getEnvironment();
			if ((grid!=null)&&(grid.isAlive())&&(!Kernel.getSingleton().isShutdown())) {
				for (Entry<AgentIdentifier, Dimension> entry : GUI.this.positions.entrySet()) {
					d = entry.getValue();
					id = entry.getKey();
					x = d.width * ICON_WIDTH;
					y = d.height * ICON_HEIGHT;
					
					ic = getIcon(grid.getAgentBody(id).getType(), getLastDirection(entry.getKey()));
					assert(ic!=null);
					ic.paintIcon(this, g2d, x, y);
				}
			}
		}
		
		private void drawGrid(Graphics2D g2d, Dimension currentDim) {
			int x, y;
			g2d.setColor(Color.BLACK);
			for(x=0; x<=currentDim.width; x+=ICON_WIDTH) {
				g2d.drawLine(x, 0, x, currentDim.height); 
			}
			for(y=0; y<=currentDim.height; y+=ICON_WIDTH) {
				g2d.drawLine(0, y, currentDim.width, y); 
			}
		}
		
		private Dimension computeDesiredDim() {
			WorldGrid world = getEnvironment();
			if (world!=null) {
				int width = world.getWidth();
				int height = world.getHeight();
				
				return new Dimension(width*GUI.ICON_WIDTH, height*GUI.ICON_HEIGHT);
			}
			return null;
		}
		
		private MoveDirection getLastDirection(AgentIdentifier id) {
			Kernel<?,?,?,?> kernel = GUI.this.getKernel();
			Probe probe = kernel.getProbe(id);
			if (probe==null) return MoveDirection.NONE;
			MoveDirection d = probe.getProbeValue("LAST_MOVE", MoveDirection.class); //$NON-NLS-1$
			return (d==null) ? MoveDirection.NONE : d;
		}
		
		private Icon getIcon(AnimalType animalType, MoveDirection direction) {
			Icon ic = null;
			switch(animalType) {
			case RABBIT:
				ic = RABBIT_ICON;
				break;
			case LION:
				ic = LION_ICON;
				break;
			case SNAKE:
				ic = SNAKE_ICON;
				break;
			}
			
			assert(ic!=null);
			
			if (direction!=null) {
				switch(direction) {
				case UP:
					ic = mergeIcons(UP_ICON, ic, 
							(ic.getIconWidth()-UP_ICON.getIconWidth())/2, 0);
					break;
				case DOWN:
					ic = mergeIcons(DOWN_ICON, ic, 
							(ic.getIconWidth()-DOWN_ICON.getIconWidth())/2,
							ic.getIconHeight() - DOWN_ICON.getIconHeight());
					break;
				case LEFT:
					ic = mergeIcons(LEFT_ICON, ic,
							0,
							(ic.getIconHeight()-LEFT_ICON.getIconHeight())/2);
					break;
				case RIGHT:
					ic = mergeIcons(RIGHT_ICON, ic, 
							ic.getIconWidth() - RIGHT_ICON.getIconWidth(),
							(ic.getIconHeight()-RIGHT_ICON.getIconHeight())/2);
					break;
				case NONE:
				}
			}
			
			return ic;
		}
	}
	
}