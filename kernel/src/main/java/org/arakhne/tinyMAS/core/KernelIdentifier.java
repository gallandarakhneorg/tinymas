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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


/** Unique identifier of a kernel.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class KernelIdentifier implements Identifier {

	private static final long serialVersionUID = -8945321864190782646L;
	
	private final InetSocketAddress __kernel_address;
	
	public KernelIdentifier() {
		this((int)(Math.random()*1000)+1000);
	}

	public KernelIdentifier(int port) {
        InetAddress lastAdr = null;
        InetAddress loopBackAddresses = null;
        try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                NetworkInterface netInterface;
                while (interfaces.hasMoreElements() && lastAdr==null) {
                        netInterface = interfaces.nextElement();
                        if (!netInterface.isLoopback()) {
                                lastAdr = extractAddress(netInterface.getInetAddresses());
                        }
                        else if (loopBackAddresses==null) {
                                loopBackAddresses = extractAddress(netInterface.getInetAddresses());
                        }
                }
        }
        catch (Exception e) {
                e.printStackTrace();
        }
        
        if (lastAdr==null) {
                lastAdr = loopBackAddresses;
        }
        
        this.__kernel_address = new InetSocketAddress(lastAdr,port);
	}
	
    private InetAddress extractAddress(Enumeration<InetAddress> addresses) {
        InetAddress adr;
        while (addresses.hasMoreElements()) {
                adr = addresses.nextElement();
                if (adr instanceof Inet4Address) return adr;
        }
        return null;
    }
    
    @Override
	public String toString() {
		return Long.toHexString(serialVersionUID)+":"+this.__kernel_address.toString(); //$NON-NLS-1$
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Identifier) {
			return compareTo((Identifier)o)==0;
		}
		return false;
	}

	public int compareTo(Identifier id) {
		if (id==null) return -1;
		return toString().compareTo(id.toString());
	}

	/** Retourne l'adresse reseau du Kernel correspondant à cet identificateur.
	 */
	public InetSocketAddress getKernelAddress() {
		return this.__kernel_address;
	}

}