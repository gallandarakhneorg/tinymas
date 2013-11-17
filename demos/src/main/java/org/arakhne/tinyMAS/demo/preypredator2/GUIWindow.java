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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.arakhne.tinyMAS.core.Kernel;

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
public class GUIWindow extends JFrame {

	private static final long serialVersionUID = 3939643459108505034L;
	
    protected final GUI gui;
	
	public GUIWindow(Kernel<?,?,?,?> kernel) {
		this.gui = new GUI();
		this.gui.setKernel(kernel);
		
		Container content = getContentPane();
		
		content.setLayout(new BorderLayout());
		
		content.add(BorderLayout.CENTER,this.gui);
		
		setPreferredSize(new Dimension(300,350));
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Kernel<?,?,?,?> kernel = GUIWindow.this.getKernel();
				kernel.stop();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				Kernel<?,?,?,?> kernel = GUIWindow.this.getKernel();
				kernel.stop();
			}
		});
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		pack();
		
		this.gui.launchRefresher();
	}
	
	protected Kernel<?,?,?,?> getKernel() {
		return this.gui.getKernel();
	}
	
}