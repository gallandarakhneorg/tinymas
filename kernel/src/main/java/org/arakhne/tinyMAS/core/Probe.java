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
public interface Probe {

	/** Replies the names of the probed values inside the agent.
	 * 
	 * @return the names of the probed values inside the agent.
	 */
	public String[] getProbeNames();

	/** Replies a probed value.
	 * 
	 * @param <T> is the type of the value to reply.
	 * @param clazz is the type of the value to reply.
	 * @param probeName is the name of the probed value
	 * @return the value
	 */
	public <T> T getProbeValue(String probeName, Class<T> clazz);

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param <T> is the type of the value to reply.
	 * @param probeName is the name of the probed value.
	 * @param clazz is the type of the expected value.
	 * @return the value or <code>null</code>
	 */
	public <T> T[] getProbeArray(String probeName, Class<T> clazz);

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeValueNotDefinedException
	 */
	public int getProbeInt(String probeName) throws ProbeValueNotDefinedException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeValueNotDefinedException
	 */
	public long getProbeLong(String probeName) throws ProbeValueNotDefinedException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeValueNotDefinedException
	 */
	public float getProbeFloat(String probeName) throws ProbeValueNotDefinedException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeValueNotDefinedException
	 */
	public double getProbeDouble(String probeName) throws ProbeValueNotDefinedException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeValueNotDefinedException
	 */
	public boolean getProbeBool(String probeName) throws ProbeValueNotDefinedException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeValueNotDefinedException
	 */
	public char getProbeChar(String probeName) throws ProbeValueNotDefinedException;

	/** Replies a probed value.
	 * <p>
	 * This method permits to force the type of the value.
	 * 
	 * @param probeName is the name of the probed value.
	 * @return the value or <code>null</code>
	 * @throws ProbeValueNotDefinedException
	 */
	public String getProbeString(String probeName) throws ProbeValueNotDefinedException;
	
	/** Replies if the agent is alive.
	 * 
	 * @return <code>true</code> if the agent associated to this probe is alive, otherwise <code>false</code>
	 */
	public boolean isProbedAgentAlive();
	
	/** Replies the identifier of the probed agent.
	 * 
	 * @return the identifier of the probed agent.
	 */
	public AgentIdentifier getProbedAgentId();

	/** Replies if this probe contains a value.
	 * 
	 * @return <code>true</code> if a probe value exists, otherwise <code>false</code>
	 */
	public boolean hasProbeValues();

	/** Replies if the specified probe value exists.
	 *
	 * @param probeName is the value name to test.
	 * @return <code>true</code> if a probe value exists, otherwise <code>false</code>
	 */
	public boolean hasProbeValue(String probeName);

}