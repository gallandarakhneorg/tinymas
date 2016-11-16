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

package org.arakhne.tinyMAS.core;

/** Listener kernel events.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class KernelAdapter implements KernelListener {

	/** Invoked when the specified kernal has been started.
	 */
	public void kernelStarted(Kernel<?,?,?,?> kernel) {
		//
	}

	/** Invoked when the specified kernal has been stoped.
	 */
	public void kernelStopped(Kernel<?,?,?,?> kernel) {
		//
	}

	/** Invoked when the specified kernal has been paused.
	 */
	public void kernelPaused(Kernel<?,?,?,?> kernel) {
		//
	}

	/** Invoked when the specified kernal has been restarted after paused.
	 */
	public void kernelRestarted(Kernel<?,?,?,?> kernel) {
		//
	}

	/** Invoked when an agent was removed from the kernel.
	 */
	public void kernelAgentRemoved(Kernel<?,?,?,?> kernel, Agent agent, AgentIdentifier id) {
		kernelAgentRemoved(kernel, id);
	}

	/** Invoked when an agent was removed from the kernel.
	 */
	public void kernelAgentRemoved(Kernel<?,?,?,?> kernel, AgentIdentifier... id) {
		//
	}

	/** Invoked when an agent was added inside the kernel.
	 */
	public void kernelAgentAdded(Kernel<?,?,?,?> kernel, Agent agent, AgentIdentifier id) {
		kernelAgentAdded(kernel, id);
	}

	/** Invoked when an agent was added inside the kernel.
	 */
	public void kernelAgentAdded(Kernel<?,?,?,?> kernel, AgentIdentifier... id) {
		//
	}

	/** Invoked when the kernel allows external refresh.
	 */
	public void kernelRefreshAllowed(Kernel<?,?,?,?> kernel) {
		//
	}

}