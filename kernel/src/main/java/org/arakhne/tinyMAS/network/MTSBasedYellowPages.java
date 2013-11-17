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

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.KernelIdentifier;
import org.arakhne.tinyMAS.core.YellowPages;

/** This yellow page system uses the Message Transport Service
 * to synchronise the service's list along a set of kernel.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class MTSBasedYellowPages extends YellowPages {

	/** Register a service provided by the specified agent.
	 * 
	 * @param service is the name of the service.
	 * @param agent is the agent that provides the service
	 */	
	@Override
	public void registerService(String service, AgentIdentifier agent) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		assert(agent!=null);
		super.registerService(service,agent);
		// Informs the other kernels about this registration
		Kernel<?,?,?,?> kernel = Kernel.getSingleton();
		if (!kernel.isOnThisKernel(agent)) {
			getMTS(NetworkMessageTransportService.class).broadcast(new KernelMessage(YellowPages.class,KernelMessage.Type.YELLOW_PAGE_REGISTERING,service,agent));
		}
	}

	/** Remove the specified agent-service association. 
	 * 
	 * @param service is the name of the service.
	 * @param agent is the agent that no more provides the service
	 */
	@Override
	public void unregisterService(String service, AgentIdentifier agent) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		assert(agent!=null);
		super.unregisterService(service,agent);
		// Informs the other kernels about this registration
		Kernel<?,?,?,?> kernel = Kernel.getSingleton();
		if (!kernel.isOnThisKernel(agent)) {
			getMTS(NetworkMessageTransportService.class).broadcast(new KernelMessage(YellowPages.class,KernelMessage.Type.YELLOW_PAGE_UNREGISTERING,service,agent));
		}		
	}

	/** Remove a service from the system.
	 * <p>
	 * All associated agents will be unlinked to the specified service.
	 */
	@Override
	public void unregisterService(String service) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		super.unregisterService(service);
		// Informs the other kernels about this registration
		getMTS(NetworkMessageTransportService.class).broadcast(new KernelMessage(YellowPages.class,KernelMessage.Type.YELLOW_PAGE_UNREGISTERING,service,null));
	}

	/** Remove the specified agent from the yellow pages.
	 */
	@Override
	public void unregisterServices(AgentIdentifier agent) {
		assert(agent!=null);
		super.unregisterServices(agent);
		// Informs the other kernels about this registration
		Kernel<?,?,?,?> kernel = Kernel.getSingleton();
		if (!kernel.isOnThisKernel(agent)) {
			getMTS(NetworkMessageTransportService.class).broadcast(new KernelMessage(YellowPages.class,KernelMessage.Type.YELLOW_PAGE_UNREGISTERING,null,agent));
		}		
	}

	/** Invoked by the kernel to treat a low-level message.
	 */
	void onKernelMessage(KernelMessage msg) {
		assert(msg!=null);
		NetworkMessageTransportService mts = getMTS(NetworkMessageTransportService.class);
		switch(msg.TYPE) {
		
		case YELLOW_PAGE_REGISTERING:
			if ((msg.IDENTIFIER!=null)&&
				(msg.SERVICE!=null)&&
				(!isRegisteredAgent(msg.SERVICE,(AgentIdentifier)msg.IDENTIFIER))) {
				NetworkKernel.displayKernelMessage("Registration of " //$NON-NLS-1$
						+msg.IDENTIFIER.toString()
						+" for service " //$NON-NLS-1$
						+msg.SERVICE);
				registerService(msg.SERVICE,(AgentIdentifier)msg.IDENTIFIER);
				mts.broadcast(msg);
			}
			break;
			
		case YELLOW_PAGE_UNREGISTERING:
			if ((msg.IDENTIFIER!=null)&&
				(msg.SERVICE!=null)&&
				(isRegisteredAgent(msg.SERVICE,(AgentIdentifier)msg.IDENTIFIER))) {
				NetworkKernel.displayKernelMessage("Unregistration of " //$NON-NLS-1$
						+msg.IDENTIFIER.toString()
						+" for service " //$NON-NLS-1$
						+msg.SERVICE);
				unregisterService(msg.SERVICE,(AgentIdentifier)msg.IDENTIFIER);
				mts.broadcast(msg);
			}
			else if ((msg.IDENTIFIER==null)&&
					 (msg.SERVICE!=null)&&
					 (isRegisteredService(msg.SERVICE))) {
				NetworkKernel.displayKernelMessage("Unregistration of the service " //$NON-NLS-1$
						+msg.SERVICE);
				unregisterService(msg.SERVICE);
				mts.broadcast(msg);
			}
			else if ((msg.IDENTIFIER!=null)&&
					 (msg.SERVICE==null)&&
					 (isRegisteredAgent((AgentIdentifier)msg.IDENTIFIER))) {
				NetworkKernel.displayKernelMessage("Unregistration of the agent " //$NON-NLS-1$
						+msg.SERVICE
						+" for all there services"); //$NON-NLS-1$
				unregisterServices((AgentIdentifier)msg.IDENTIFIER);
				mts.broadcast(msg);
			}
			break;
			
		case KERNEL_PRESENTATION:
			if (msg.IDENTIFIER!=null) {
				NetworkKernel.displayKernelMessage("Forwarding service definitions to " //$NON-NLS-1$
						+msg.IDENTIFIER.toString());
				String[] services = getAllServices();
				for(int idxServ=0; idxServ<services.length; idxServ++) {
					AgentIdentifier[] agents = getAgents(services[idxServ]);
					for(int idxAg=0; idxAg<agents.length; idxAg++) {
						KernelMessage m = new KernelMessage(
								YellowPages.class,
								KernelMessage.Type.YELLOW_PAGE_REGISTERING,
								services[idxServ],
								agents[idxAg]);
						mts.send((KernelIdentifier)msg.IDENTIFIER,m);
					}
				}
			}
			break;

		default:
			// Ignore all other kernel messages
		}
	}

}