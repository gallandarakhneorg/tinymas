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

import java.io.Serializable;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Identifier;
import org.arakhne.tinyMAS.core.KernelIdentifier;


/** Low level messages exchanged by kernels.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
class KernelMessage implements Serializable {

	private static final long serialVersionUID = -6655160153599661898L;

	@Deprecated
	public enum Type {
		YELLOW_PAGE_REGISTERING,
		YELLOW_PAGE_UNREGISTERING,
		KERNEL_PRESENTATION,
		KERNEL_PRESENTATION_ACK,
		KERNEL_DELETION;
	}
	
	public final Class<?> TARGET;
	
	public final Type TYPE;
	
	public final String SERVICE; 
	
	public final Identifier IDENTIFIER; 

	public KernelMessage(Class<?> target, Type type, String service, AgentIdentifier agent) {
		assert(target!=null);
		assert(type!=null);
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		assert(agent!=null);
		this.TARGET = target;
		this.TYPE = type;
		this.SERVICE = service;
		this.IDENTIFIER = agent;
	}
		
	public KernelMessage(Type type, KernelIdentifier kernel) {
		assert(type!=null);
		assert(kernel!=null);
		this.TARGET = null;
		this.TYPE = type;
		this.SERVICE = null;
		this.IDENTIFIER = kernel;
	}

}