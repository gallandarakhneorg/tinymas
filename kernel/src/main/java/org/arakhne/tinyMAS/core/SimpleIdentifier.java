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

import java.rmi.server.UID;

/** Basic implementation of an identifier.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class SimpleIdentifier implements Identifier {

	private static final long serialVersionUID = 5809385011371117305L;
	
	private final String __id;
	
	/**
	 * @param id is the value of the identifier.
	 */
	public SimpleIdentifier(String id) {
		assert((id!=null)&&(!"".equals(id))); //$NON-NLS-1$
		this.__id = id;
	}

	/**
	 * Create a random identifier.
	 */
	public SimpleIdentifier() {
		this.__id = new UID().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Long.toHexString(serialVersionUID)+":"+this.__id.toString(); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		return ((o instanceof Identifier)&&
				((this==o)||(toString().equals(o.toString()))));
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Identifier id) {
		if (id==null) return -1;
		return toString().compareTo(id.toString());
	}
	
}