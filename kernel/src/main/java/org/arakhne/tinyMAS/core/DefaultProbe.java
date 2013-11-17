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

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** A probe permits to obtain information on agents.
 * <p>
 * A probe must not be invasive and the agent is
 * able to ignore them (ie. to not provide information).
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
class DefaultProbe implements Probe {

	private final Map<String,Object> __values = new TreeMap<String,Object>();
	
	private final WeakReference<Agent> __agent;
	
	DefaultProbe(Agent agent) {
		assert(agent!=null);
		this.__agent = new WeakReference<Agent>(agent);
	}
	
	/** Replies the names of the probed values inside the agent.
	 */
	public final String[] getProbeNames() {
		Set<String> theSet = this.__values.keySet();
		String[] tab = new String[theSet.size()];
		theSet.toArray(tab);
		return tab;
	}

	/** Replies a probed value.
	 * 
	 * @param probeName is the name of the probed value
	 * @return the value
	 */
	protected Object getProbeValue(String probeName) {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		Object o = null;
		synchronized(this.__values) {
			o = this.__values.get(probeName);
		}
		return o;
	}

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @param clazz is the type of the expected value.
	 * @return the value or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getProbeArray(String probeName, Class<T> clazz) {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		assert(clazz!=null);
		Object o = null;
		synchronized(this.__values) {
			o = this.__values.get(probeName);
		}
		if (o!=null) {
			try {
				int count = Array.getLength(o);
				T[] t = (T[])Array.newInstance(clazz,count);
				for(int i=0; i<count; i++) {
					Object lo = Array.get(o,i);
					if ((lo!=null)&&(clazz.isInstance(lo))) {
						t[i] = clazz.cast(lo);
					}
				}
				return t;
			}
			catch(Exception _) {
				//
			}
		}
		return null;
	}

	/** Store the value associated to a probe name.
	 * <p>
	 * This method is generally invoked from the agent.
	 * 
	 * @param probeName is the name of the probed value.
	 * @param value is the value of the probe.
	 */
	public void putProbeValue(String probeName, Object value) {
		synchronized(this.__values) {
			assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
			this.__values.put(probeName,value);
		}
	}

	/** Remove a value from this probe.
	 * 
	 * @param probeName is the name of the probed value.
	 */
	public void removeProbeValue(String probeName) {
		synchronized(this.__values) {
			assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
			this.__values.remove(probeName);
		}
	}

	/** Remove all the values.
	 */
	public void clearProbeValues() {
		synchronized(this.__values) {
			this.__values.clear();
		}
	}

	/** Replies if this probe contains a value.
	 */
	public boolean hasProbeValues() {
		synchronized(this.__values) {
			return !this.__values.isEmpty();
		}
	}

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @param clazz is the type of the expected value.
	 * @return the value or <code>null</code>
	 */
	public final <T> T getProbeValue(String probeName, Class<T> clazz) {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		assert(clazz!=null);
		Object o = getProbeValue(probeName);
		if (o==null) return null;
		return clazz.cast(o);
	}

	/** Replies a probed value as integer (if possible).
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 */
	public final int getProbeInt(String probeName) throws ProbeValueNotDefinedException {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		if (!hasProbeValue(probeName)) throw new ProbeValueNotDefinedException(probeName);
		Object n = getProbeValue(probeName);
		if ((n==null)||(!(n instanceof Number))) throw new ProbeValueNotDefinedException(probeName);
		return ((Number)n).intValue();
	}

	/** Replies a probed value as integer (if possible).
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 */
	public final long getProbeLong(String probeName) throws ProbeValueNotDefinedException {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		if (!hasProbeValue(probeName)) throw new ProbeValueNotDefinedException(probeName);
		Object n = getProbeValue(probeName);
		if ((n==null)||(!(n instanceof Number))) throw new ProbeValueNotDefinedException(probeName);
		return ((Number)n).longValue();
	}

	/** Replies a probed value as float (if possible).
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 */
	public final float getProbeFloat(String probeName) throws ProbeValueNotDefinedException {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		if (!hasProbeValue(probeName)) throw new ProbeValueNotDefinedException(probeName);
		Object n = getProbeValue(probeName);
		if ((n==null)||(!(n instanceof Number))) throw new ProbeValueNotDefinedException(probeName);
		return ((Number)n).floatValue();
	}

	/** Replies a probed value as double (if possible).
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 */
	public final double getProbeDouble(String probeName) throws ProbeValueNotDefinedException {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		if (!hasProbeValue(probeName)) throw new ProbeValueNotDefinedException(probeName);
		Object n = getProbeValue(probeName);
		if ((n==null)||(!(n instanceof Number))) throw new ProbeValueNotDefinedException(probeName);
		return ((Number)n).doubleValue();
	}

	/** Replies a probed value as boolean (if possible).
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 */
	public final boolean getProbeBool(String probeName) throws ProbeValueNotDefinedException {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		if (!hasProbeValue(probeName)) throw new ProbeValueNotDefinedException(probeName);
		Object n = getProbeValue(probeName);
		if ((n==null)||(!(n instanceof Boolean))) throw new ProbeValueNotDefinedException(probeName);
		return ((Boolean)n).booleanValue();
	}

	/** Replies a probed value as character (if possible).
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 */
	public final char getProbeChar(String probeName) throws ProbeValueNotDefinedException {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		if (!hasProbeValue(probeName)) throw new ProbeValueNotDefinedException(probeName);
		Object n = getProbeValue(probeName);
		if ((n==null)||(!(n instanceof Character))) throw new ProbeValueNotDefinedException(probeName);
		return ((Character)n).charValue();
	}

	/** Replies a probed value as string (if possible).
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 */
	public final String getProbeString(String probeName) throws ProbeValueNotDefinedException {
		assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
		if (!hasProbeValue(probeName)) throw new ProbeValueNotDefinedException(probeName);
		Object n = getProbeValue(probeName);
		if (n==null) return null;
		return n.toString();
	}
	
	/** Replies if the agent is alive.
	 */
	public boolean isProbedAgentAlive() {
		Agent ag = this.__agent.get();
		if (ag!=null)
			return ag.isAlive();
		return false;
	}
	
	/** Replies the identifier of the probed agent.
	 */
	public AgentIdentifier getProbedAgentId() {
		Agent ag = this.__agent.get();
		if (ag!=null)
			return ag.getId();
		return null;
	}

	/** Replies if the specified probe value exists.
	 */
	public boolean hasProbeValue(String probeName) {
		synchronized(this.__values) {
			assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
			return this.__values.containsKey(probeName);
		}
	}
	
}