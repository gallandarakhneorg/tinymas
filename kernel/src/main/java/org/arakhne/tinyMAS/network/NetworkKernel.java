/* 
 * $Id$
 * 
 * Copyright (C) 2004-2007 St&eacute;phane GALLAND, Nicolas GAUD
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

package org.arakhne.tinyMAS.network;


import java.net.InetAddress;

import org.arakhne.tinyMAS.core.Agent;
import org.arakhne.tinyMAS.core.DefaultScheduler;
import org.arakhne.tinyMAS.core.Environment;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.Scheduler;

/** Kernel of the TinyMAS application.
 * <p>
 * The kernel supports network communications.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class NetworkKernel<AT extends Agent, ET extends Environment<AT>, YP extends MTSBasedYellowPages, MTS extends NetworkMessageTransportService> extends Kernel<AT, ET, YP, MTS> {

	/** Create a kernel with the default components.
	 */
	@SuppressWarnings("unchecked")
	public NetworkKernel(int socketPort) {
		super(
				(MTS)new NetworkMessageTransportService(socketPort), 
				(YP)new MTSBasedYellowPages(), 
				new DefaultScheduler<AT>());
	}

	/** Create a kernel with the default components.
	 */
	@SuppressWarnings("unchecked")
	public NetworkKernel(int socketPort, InetAddress otherKernelAdr, int otherKernelPort) {
		super(
				(MTS)new NetworkMessageTransportService(socketPort, otherKernelAdr, otherKernelPort), 
				(YP)new MTSBasedYellowPages(), 
				new DefaultScheduler<AT>());
	}

	/** Create a kernel with the default components.
	 */
	@SuppressWarnings("unchecked")
	public NetworkKernel(int socketPort, Scheduler<AT> scheduler) {
		super(
				(MTS)new NetworkMessageTransportService(socketPort), 
				(YP)new MTSBasedYellowPages(), 
				scheduler);
	}

	/** Create a kernel with the default components.
	 */
	@SuppressWarnings("unchecked")
	public NetworkKernel(int socketPort, InetAddress otherKernelAdr, int otherKernelPort, Scheduler<AT> scheduler) {
		super(
				(MTS)new NetworkMessageTransportService(socketPort, otherKernelAdr, otherKernelPort), 
				(YP)new MTSBasedYellowPages(), 
				scheduler);
	}

	/** Create a kernel with the default components.
	 */
	public NetworkKernel(MTS netMTS, YP yellowPages, Scheduler<AT> scheduler) {
		super(netMTS, yellowPages, scheduler);
	}
	
	/** Create a kernel with the default components.
	 */
	public NetworkKernel(MTS netMTS, YP yellowPages) {
		super(netMTS, yellowPages, new DefaultScheduler<AT>());
	}

	/** Create a kernel with the specified scheduler and message transport service.
	 */
	@SuppressWarnings("unchecked")
	public NetworkKernel(MTS mts) {
		super(
				mts,
				(YP)new MTSBasedYellowPages(),
				new DefaultScheduler<AT>());
	}

	/** Create a kernel with the specified scheduler and message transport service.
	 */
	@SuppressWarnings("unchecked")
	public NetworkKernel(MTS mts, Scheduler<AT> scheduler) {
		super(
				mts,
				(YP)new MTSBasedYellowPages(),
				scheduler);
	}

	/** Kernel log.
	 */
	protected static void displayKernelMessage(String msg) {
		Kernel.displayKernelMessage(msg);
	}
	
	@SuppressWarnings("unchecked")
	public static NetworkKernel<?,?,? extends MTSBasedYellowPages, ? extends NetworkMessageTransportService> getSingleton() {
		return (NetworkKernel<?,?,? extends MTSBasedYellowPages, ? extends NetworkMessageTransportService>)Kernel.getSingleton();
	}

}