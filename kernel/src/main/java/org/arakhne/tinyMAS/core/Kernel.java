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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.arakhne.afc.references.WeakArrayList;

/** Kernel of the TinyMAS application.
 * <p>
 * The kernel is the central point of the multiagent platform.
 * 
 * @param <AT> is the type of agent supported by this kernel
 * @param <ET> is the type of environment supported by this kernel
 * @param <YP> is the type of yellow page supported by this kernel
 * @param <MTS> is the type of message transport service supported by this kernel.
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class Kernel<AT extends Agent, ET extends Environment<AT>, YP extends YellowPages, MTS extends MessageTransportService> implements Runnable {

	/** This is the minimal duration which can be measured
	 * by an operating system (in milliseconds).
	 */ 
	public static final int TIME_PRECISION = 10;
	
	/** Timeout in milliseconds to wait about kernel stop.
	 */
	public static final int KERNEL_STOP_TIMEOUT = 20000;
	
	private static volatile Kernel<?,?,?,?> SINGLETON;
	
	/** Manager of the message transport.
	 */
	private final MTS __mts;
	
	/** Manager of the agent identification and
	 * of the "white pages" of the application.
	 */	
	private final WhitePages<AT> __whitepages;

	/** Service manager.
	 */	
	private final YP __yellowpages;

	/** Identifier of this kernel.
	 */
	private final KernelIdentifier __kernel_id;
	
	/** Agent scheduler used by this kernel. 
	 */
	private final Scheduler<AT> __scheduler;

	/** Environment.
	 */
	private ET __environment;

	/** flag that indicates if the kernel should stop or not.
	 */
	private volatile boolean __stopsimu = true;

	/** Indicates if the kernel is paused after its started.
	 */
	private volatile boolean __isUnderPause = false;

	/** Indicates if the kernel is currently in
	 * its shutdown process.
	 */
	private volatile boolean __in_shutdowning = false;
	
	/** Indicates if the kernel was shutdowned.
	 */
	private volatile boolean __is_shutdown = false;

	/** Simulation time.
	 */
	private long __simulation_time = 0;
	
	/** Last simulation step duration.
	 */
	private long __simulation_step_duration = 0;
	
	/** Listeners.
	 */
	private final List<KernelListener> __kernelListeners = new ArrayList<KernelListener>();
	
	/** List of agents to kill.
	 * <p>
	 * An agent should appear in this list only if it invoked {@link Agent#killMe()}.
	 */
	private final List<AgentIdentifier> __agentsToKill = new WeakArrayList<AgentIdentifier>();
	
	/** Indicates the waiting time in milliseconds between two simulation
	 * steps.
	 */
	private int __waitingTime = 0;

	/** Indicates the waiting time in milliseconds between two simulation
	 * steps.
	 */
	private long __waitingTimeCountDown = 0;

	/** Create a kernel with the default components.
	 */
	@SuppressWarnings("unchecked")
	public Kernel() {
		this(
				(MTS)new MessageTransportService(),
				new WhitePages<AT>(),
				(YP)new YellowPages(),
				new DefaultScheduler<AT>(),
				null);
	}
	
	/** Create a kernel with the specified scheduler.
	 * 
	 * @param scheduler
	 */
	@SuppressWarnings("unchecked")
	public Kernel(Scheduler<AT> scheduler) {
		this(
				(MTS)new MessageTransportService(),
				new WhitePages<AT>(),
				(YP)new YellowPages(),
				scheduler,
				null);
	}

	/** Create a kernel with the specified scheduler and message transport service.
	 * 
	 * @param mts
	 * @param scheduler
	 */
	@SuppressWarnings("unchecked")
	public Kernel(MTS mts, Scheduler<AT> scheduler) {
		this(
				mts,
				new WhitePages<AT>(),
				(YP)new YellowPages(),
				new DefaultScheduler<AT>(),
				null);
	}

	/** Create a kernel with the specified components.
	 * 
	 * @param mts
	 * @param whitepages
	 * @param yellowpages
	 */
	public Kernel(MTS mts, WhitePages<AT> whitepages, YP yellowpages) {
		this(
				mts,
				whitepages,
				yellowpages,
				new DefaultScheduler<AT>(),
				null);
	}

	/** Create a kernel with the specified components.
	 * 
	 * @param mts
	 * @param yellowpages
	 */
	public Kernel(MTS mts, YP yellowpages) {
		this(
				mts,
				new WhitePages<AT>(),
				yellowpages,
				new DefaultScheduler<AT>(),
				null);
	}

	/** Create a kernel with the specified components.
	 * 
	 * @param mts
	 * @param whitepages
	 * @param yellowpages
	 * @param scheduler
	 */
	public Kernel(MTS mts, WhitePages<AT> whitepages, YP yellowpages, Scheduler<AT> scheduler) {
		this(mts, whitepages, yellowpages, scheduler, null);
	}

	/** Create a kernel with the specified components.
	 * 
	 * @param mts
	 * @param yellowpages
	 * @param scheduler
	 */
	public Kernel(MTS mts, YP yellowpages, Scheduler<AT> scheduler) {
		this(mts, new WhitePages<AT>(), yellowpages, scheduler, null);
	}

	/** Create a kernel with the specified components.
	 * 
	 * @param env
	 */
	@SuppressWarnings("unchecked")
	public Kernel(ET env) {
		this(
				(MTS)new MessageTransportService(),
				new WhitePages<AT>(),
				(YP)new YellowPages(),
				new DefaultScheduler<AT>(),
				env);
	}

	/** Create a kernel with the specified components.
	 * 
	 * @param mts
	 * @param whitepages
	 * @param yellowpages
	 * @param scheduler
	 * @param env
	 */
	public Kernel(MTS mts, WhitePages<AT> whitepages, YP yellowpages, Scheduler<AT> scheduler, ET env) {
		assert(whitepages!=null);
		assert(yellowpages!=null);
		assert(mts!=null);
		this.__kernel_id = mts.getKernelId();
		this.__mts = mts;
		this.__whitepages = whitepages;
		this.__yellowpages = yellowpages;
		this.__scheduler = scheduler;
		this.__environment = env;
		registerAsSingleton();
	}

	/** Replies the unique instance of Kernel.
	 *
	 * @return the singleton
	 * @see #registerAsSingleton()
	 */
	public static Kernel<?,?,?,?> getSingleton() {
		return SINGLETON;
	}

	/** Register this kernel as the singleton.
	 * 
	 * @return <code>true</code> if this kernel is now the singleton, otherwise <code>false</code>
	 * @see #getSingleton()
	 */
	public boolean registerAsSingleton() {
		if ((SINGLETON==null)||(SINGLETON.__is_shutdown)) {
			SINGLETON = this;
			return true;
		}
		return false;
	}
	
	/** Replies the message transport service used by this kernel.
	 * 
	 * @return the MTS
	 */
	MTS getMTS() {
		return this.__mts;
	}	

	/** Replies the environment used by this kernel (if it exist).
	 * 
	 * @return the environment
	 */
	public ET getEnvironment() {
		return this.__environment;
	}	

	/** Set the environment that should be used by this kernel.
	 * 
	 * @param env
	 */
	public void setEnvironment(ET env) {
		this.__environment = env;
	}	

	/** Add an agent inside this kernel.
	 * <p>
	 * The kernel assumes that the specified agent
	 * was never spawn before. An unique identifier
	 * will be provided to the specified agent.
	 * 
	 * @param agent
	 */	
	@SuppressWarnings("unchecked")
	public void addAgent(Agent agent) {
		assert(agent!=null);
		AT a = (AT)agent;
		if (!this.__in_shutdowning) {
			this.__whitepages.register(a) ;
			AgentIdentifier id  = this.__whitepages.getId(a) ;
			this.__mts.registerAgent(id) ;
			if (this.__environment!=null) {
				this.__environment.putAgent(a);
			}
			if (!this.__stopsimu) {
				a.start(id);
				fireAgentAdded(agent, id);
			}			
		}
	}

	/** Add an agent inside this kernel.
	 * <p>
	 * The kernel assumes that the specified agent
	 * was already spawn before. The specified
	 * identifier will be given to the agent (if
	 * it is unique).
	 *
	 * @param id
	 * @param a
	 */	
	public void addAgent(AgentIdentifier id, AT a) {
		assert(id!=null);
		assert(a!=null);
		if (!this.__in_shutdowning) {
			this.__whitepages.register(id, a) ;
			this.__mts.registerAgent(id) ;
			if (this.__environment!=null) {
				this.__environment.putAgent(a);
			}
			if (!this.__stopsimu) {
				a.start(id);
				fireAgentAdded(a, id);
			}			
		}
	}

	/** Kill an agent.
	 * <p>
	 * This method should only be invoked by {@link Agent#killMe()}.
	 * <p>
	 * The agent will be removed at the end of the current scheduling step.
	 * 
	 * @param id
	 * @see #removeAgent(AgentIdentifier)
	 */
	void killAgent(AgentIdentifier id) {
		assert(id!=null);
		this.__agentsToKill.add(id);
	}
	
	/** Kill an agent.
	 * <p>
	 * The agent will be removed immediately.
	 * 
	 * @param id
	 * @see #killAgent(AgentIdentifier)
	 */
	void removeAgent(AgentIdentifier id) {
		assert(id!=null);
		if (this.__environment!=null) {
			this.__environment.removeAgent(id);
		}
		Agent ag = this.__whitepages.unregister(id) ;
		this.__mts.unregisterAgent(id) ;
		if (this.__yellowpages!=null)
			this.__yellowpages.unregisterServices(id) ;
		if (ag != null) {
			if (!this.__in_shutdowning) ag.stop() ;
		}
		if (!this.__stopsimu) {
			fireAgentRemoved(ag, id);
		}
	}
	
	/** Kill immediately all the agents.
	 */
	void removeAllAgents() {
		AgentIdentifier[] ags = this.__whitepages.getAllAgentIdentifiers() ;
		for (AgentIdentifier identifier : ags) {
			removeAgent(identifier);
		}
	}

	/** Replies the identifier of the specified agent.
	 *
	 * @param a
	 * @return the identifier.
	 */
	AgentIdentifier getAgentId(AT a) {
		assert(a!=null);
		return this.__whitepages.getId(a) ;
	}
	
	/** Run the kernel.
	 * <p>
	 * This method invokes:
	 * <ul>
	 * <li>{@link #runInitializationStage()}</li>
	 * <li>{@link #runAgentLifeStage()}</li>
	 * <li>{@link #runShutdownStage()}</li>
	 * </ul>
	 *
	 * @see #runInitializationStage()
	 * @see #runAgentLifeStage()
	 * @see #runShutdownStage()
	 */
	public void run() {
		runInitializationStage();
		runAgentLifeStage();
		runShutdownStage();
	}
	
	/** Replies the agents which are alive.
	 */
	private List<AT> getAliveAgents() {
		Collection<AT> c = this.__whitepages.getAllAgents();
		List<AT> l = new ArrayList<AT>();
		for (AT at : c) {
			if ((at!=null)&&(at.isAlive())) {
				l.add(at);
			}
		}
		return l;
	}

	/** Replies the agents which are registered.
	 */
	private List<AT> getRegisteredAgents() {
		Collection<AT> c = this.__whitepages.getAllAgents();
		List<AT> l = new ArrayList<AT>();
		l.addAll(c);
		return l;
	}

	/** Run the initialization stage of the kernel.
	 *
	 * @see #run()
	 * @see #runAgentLifeStage()
	 * @see #runShutdownStage()
	 */
	protected void runInitializationStage() {
		this.__in_shutdowning = false;
		this.__is_shutdown = false;
		this.__stopsimu = true;
		this.__isUnderPause = false;
		
		this.__simulation_time = 0;
		this.__simulation_step_duration = System.currentTimeMillis();
		displayKernelMessage("Initializing micro-kernel..."); //$NON-NLS-1$
		registerAsSingleton();
		displayKernelMessage("\tstarting MTS..."); //$NON-NLS-1$
		this.__mts.startMTS();
		if (this.__environment!=null) {
			displayKernelMessage("\tinitializing environment..."); //$NON-NLS-1$
			this.__environment.init() ;
		}
		displayKernelMessage("\tstarting agents..."); //$NON-NLS-1$
		startAgents(getRegisteredAgents()) ;
		this.__simulation_step_duration = System.currentTimeMillis() - this.__simulation_step_duration;
	}

	/** Run the agent live stages.
	 * 
	 * @see #run()
	 * @see #runInitializationStage()
	 * @see #runShutdownStage()
	 */
	protected void runAgentLifeStage() {
		displayKernelMessage("Running micro-kernel..."); //$NON-NLS-1$
		this.__stopsimu = false ;
		this.__simulation_time = 0;
		fireKernelStarted();
		boolean previousPauseState = this.__isUnderPause;
		while (!isStopped()) {
			if (previousPauseState!=this.__isUnderPause) {
				if (this.__isUnderPause) {
					fireKernelPaused();
				}
				else {
					fireKernelRestarted();
				}
				previousPauseState = this.__isUnderPause;
			}
			if (!this.__isUnderPause) {

				long starting_date = System.currentTimeMillis();

				if ((this.__waitingTime<=0)||
					(this.__waitingTimeCountDown<=starting_date)) {
				
					// Run the system
					if (this.__scheduler!=null) {
						List<AT> agents = getAliveAgents();
						if (this.__environment!=null)
							this.__scheduler.schedule(this,this.__environment, agents);
						else
							this.__scheduler.schedule(agents);
					}
					
					//
					// Stop the system if the environment is dead
					//
					if ((this.__environment!=null)&&(!this.__environment.isAlive())) {
						stopKernel();
					}

					//
					// Kill the agents which request it
					//
					if (!this.__agentsToKill.isEmpty()) {
						for (AgentIdentifier id : this.__agentsToKill) {
							removeAgent(id);
						}
						displayKernelMessage("Killed "+this.__agentsToKill.size()+" agent(s)"); //$NON-NLS-1$ //$NON-NLS-2$
						this.__agentsToKill.clear();
					}
					
					//
					// Compute time indicators
					//
					long ending_date = System.currentTimeMillis();
					this.__simulation_step_duration = ending_date - starting_date;
					this.__simulation_time++;
					
					if (this.__waitingTime>0)
						this.__waitingTimeCountDown = ending_date + this.__waitingTime;
					
					// Allow external refresh
					fireKernelExternalRefreshAllowed();					

				}
				
			}
		}
	}

	/** run the shutdown stage of the kernel.
	 * 
	 * @see #run()
	 * @see #runInitializationStage()
	 * @see #runAgentLifeStage()
	 */
	protected void runShutdownStage() {
		displayKernelMessage("Shutdowning micro-kernel..."); //$NON-NLS-1$
		displayKernelMessage("\tstoping agents..."); //$NON-NLS-1$
		this.__in_shutdowning = true;
		stopAgents(getRegisteredAgents()) ;
		removeAllAgents();
		if (this.__environment!=null) {
			displayKernelMessage("\tshutdown environment..."); //$NON-NLS-1$
			this.__environment.shutdown() ;
		}
		displayKernelMessage("\tstoping MTS..."); //$NON-NLS-1$
		this.__mts.stopMTS();
		this.__stopsimu = true;
		this.__simulation_time = 0;
		this.__simulation_step_duration = 0;
		displayKernelMessage("\tshutdown complete."); //$NON-NLS-1$
		this.__in_shutdowning = false;
		this.__is_shutdown = true;
		fireKernelStopped();
	}

	/** Agent lif-cycle: starts all the specified agents.
	 * 
	 * @param agents
	 */	
	protected void startAgents(Collection<AT> agents) {
		assert(agents!=null);
		for(AT agent : agents) {
			AgentIdentifier id = this.__whitepages.getId(agent) ;
			if (id!=null) {
				agent.start(id) ;
			}
		}
	}
	
	/** Set the milliseconds to wait between two simulation steps.
	 * 
	 * @param duration
	 */
	public void setWaitingDuration(int duration) {
		if (duration>=0) this.__waitingTime = duration;
	}

	/** Notify of the specified agents that they are dead.
	 * 
	 * @param agents
	 */
	protected void stopAgents(Collection<AT> agents) {
		assert(agents!=null);
		for(Agent agent : agents) {
			agent.stop() ;
		}
	}

	/** Replies if the kernel is not running.
	 * If the kernel is under pause, this function
	 * replies <code>true</code>
	 * 
	 * @return <code>true</code> if the kernel was stopped, otherwise <code>false</code>
	 */
	public boolean isStopped() {
		return ((this.__stopsimu)||
				(this.__whitepages.size()==0)||
				(this.__is_shutdown)||
				(this.__in_shutdowning)) ;
	}

	/** Replies if this kernel is under pause.
	 * 
	 * @return <code>true</code> if the kernel was paused, otherwise <code>false</code>
	 */
	public boolean isUnderPause() {
		return (this.__isUnderPause);
	}

	/** Replies if this kernel was launch, but is now shutdown.
	 * 
	 * @return <code>true</code> if the kernel was shutdown, otherwise <code>false</code>
	 */
	public boolean isShutdown() {
		return (this.__is_shutdown || this.__in_shutdowning);
	}

	/** Stop the kernel.
	 */	
	public void stop() {
		displayKernelMessage("Requesting kernel shutdown") ; //$NON-NLS-1$
		this.__stopsimu = true;
	}

	/** Stop the kernel singleton.
	 */	
	public static void stopKernel() {
		if (SINGLETON!=null) {
			SINGLETON.stop() ;
		}		
	}
	
	/** Push the current kernel under pause.
	 */	
	public void pause() {
		if (!this.__stopsimu) {
			if (!this.__isUnderPause) { 
				displayKernelMessage("Requesting kernel pause") ; //$NON-NLS-1$
			}
			this.__isUnderPause = ! this.__isUnderPause;
		}
	}

	/** Push the singleton kernel under pause.
	 */	
	public static void pauseKernel() {
		if (SINGLETON!=null) {
			SINGLETON.pause() ;
		}		
	}
	
	/** Replies a probe on the specified agent.
	 *
	 * @param agentId
	 * @return a probe or <code>null</code> if the agent
	 * could not provide a probe.
	 */
	public static Probe probe(AgentIdentifier agentId) {
		assert(agentId!=null);
		if (SINGLETON!=null) {
			SINGLETON.getProbe(agentId);
		}
		return null;
	}

	/** Replies the list of agents that provide the specified service
	 *
	 * @param serviceName
	 * @return the list of providers.
	 */
	public static AgentIdentifier[] service(String serviceName) {
		assert((serviceName!=null)&&(!"".equals(serviceName))); //$NON-NLS-1$
		if (SINGLETON!=null) {
			return SINGLETON.getServiceProviders(serviceName);
		}
		return new AgentIdentifier[0];
	}

	/** Replies the identifier of this kernel.
	 * 
	 * @return the kernel identifier
	 */
	public KernelIdentifier getKernelId() {
		return this.__kernel_id;
	}
	
	/** Replies the yellow pages used by this kernel.
	 *
	 * @return the yellow pages
	 */
	public YP getYellowPageSystem() {
		return this.__yellowpages;
	}
	
	/** Replies the list of agents that provide the specified service
	 *
	 * @param serviceName
	 * @return the providers
	 */
	public AgentIdentifier[] getServiceProviders(String serviceName) {
		assert((serviceName!=null)&&(!"".equals(serviceName))); //$NON-NLS-1$
		return this.__yellowpages.getAgents(serviceName);
	}

	/** Replies the count of agents registered inside this kernel.
	 * 
	 * @return the count of agents.
	 */
	public long getAgentCount() {
		return this.__whitepages.size();
	}

	/** Replies if this agent identifier corresponds to an
	 * agent inside this kernel.
	 * 
	 * @param agent
	 * @return <code>true</code> if the given agent is on this kernel,
	 * otherwise <code>false</code>
	 */
	public boolean isOnThisKernel(AgentIdentifier agent) {
		assert(agent!=null);
		return getKernelId().equals(agent.getKernelId());
	}
	
	/** Kernel log.
	 * 
	 * @param msg
	 */
	protected static void displayKernelMessage(String msg) {
		System.err.println("*** KERNEL *** > "+msg); //$NON-NLS-1$
	}

	/** Replies the count of simulation turns
	 * since the start of the kernel.
	 * 
	 * @return the count of turns
	 */
	public long getKernelStep() {
		return this.__simulation_time;
	}
	
	/** Replies the duration in milliseconds of one turn of the kernel.
	 * 
	 * @return the duration of a turn.
	 */
	public long getKernelStepDuration() {
		return (this.__simulation_step_duration<TIME_PRECISION) ?
			TIME_PRECISION : this.__simulation_step_duration;
	}
	
	/** Replies the simulation clock associated to this kernel.
	 * <p>
	 * This function replies the clock of the environment if it
	 * exists or an internal clock based on the kernel steps.
	 * 
	 * @return never <code>null</code>
	 * @see #getKernelStep()
	 * @see #getKernelStepDuration()
	 */
	public SimulationClock getSimulationClock() {
		SimulationClock sc = null;
		ET env = getEnvironment();
		if (env!=null) {
			sc = env.getSimulationClock();
		}
		if (sc==null) {
			// Replies the internal representation
			return new SimulationClock() {
				public double getSimulationStepDuration(TimeUnit desired_unit) {
					return desired_unit.convert(getKernelStepDuration(),TimeUnit.MILLISECONDS);
				}
				public double getSimulationStepDuration() {
					return getKernelStepDuration();
				}
				public double getSimulationTime(TimeUnit desired_unit) {
					return getKernelStep();
				}
				public double getSimulationTime() {
					return getKernelStep();
				}
				public double perTimeUnit(double value) {
					return perTimeUnit(value,TimeUnit.SECONDS);
				}
				public double perTimeUnit(double value, TimeUnit desiredUnit) {
					return value * getSimulationStepDuration(desiredUnit);
				}
			};
		}
		return sc;
	}
	
	/** Add a listener.
	 * 
	 * @param listener
	 */
	public void addKernelListener(KernelListener listener) {
		assert(listener!=null);
		this.__kernelListeners.add(listener);
	}

	/** remove a listener.
	 * 
	 * @param listener
	 */
	public void removeKernelListener(KernelListener listener) {
		assert(listener!=null);
		this.__kernelListeners.remove(listener);
	}

	/** Informs listener about the starting of the kernel.
	 */
	private void fireKernelStarted() {
		for (KernelListener listener : this.__kernelListeners) {
			if (listener!=null) {
				listener.kernelStarted(this);
			}
		}
	}

	/** Informs listener about the ending of the kernel.
	 */
	private void fireKernelStopped() {
		for (KernelListener listener : this.__kernelListeners) {
			if (listener!=null) {
				listener.kernelStopped(this);
			}
		}
	}

	/** Informs listener about external refresh.
	 */
	private void fireKernelExternalRefreshAllowed() {
		for (KernelListener listener : this.__kernelListeners) {
			if (listener!=null) {
				listener.kernelRefreshAllowed(this);
			}
		}
	}

	/** Informs listener about the pause of the kernel.
	 */
	private void fireKernelPaused() {
		for (KernelListener listener : this.__kernelListeners) {
			if (listener!=null) {
				listener.kernelPaused(this);
			}
		}
	}
	
	/** Informs listener about the return from a pause of the kernel.
	 */
	private void fireKernelRestarted() {
		for (KernelListener listener : this.__kernelListeners) {
			if (listener!=null) {
				listener.kernelRestarted(this);
			}
		}
	}

	/** Informs listener about the addition of an agent.
	 */
	private void fireAgentAdded(Agent agent, AgentIdentifier id) {
		assert(agent!=null);
		assert(id!=null);
		for (KernelListener listener : this.__kernelListeners) {
			if (listener!=null) {
				listener.kernelAgentAdded(this, agent, id);
			}
		}
	}

	/** Informs listener about the deletion of an agent.
	 */
	private void fireAgentRemoved(Agent agent, AgentIdentifier id) {
		assert(agent!=null);
		assert(id!=null);
		for (KernelListener listener : this.__kernelListeners) {
			if (listener!=null) {
				listener.kernelAgentRemoved(this, agent, id);
			}
		}
	}

	/** Replies a probe on the specified agent.
	 * 
	 * @param agentId
	 * @return a probe or <code>null</code> if the agent
	 * could not provide a probe.
	 */
	public Probe getProbe(AgentIdentifier agentId) {
		assert(agentId!=null);
		Agent ag = this.__whitepages.getAgent(agentId);
		if (ag!=null) {
			return ag.getProbe();
		}
		return null;
	}

}