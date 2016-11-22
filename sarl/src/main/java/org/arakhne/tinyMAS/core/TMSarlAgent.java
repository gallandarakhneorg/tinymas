package org.arakhne.tinyMAS.core;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import io.sarl.core.AgentTask;
import io.sarl.core.Behaviors;
import io.sarl.core.DefaultContextInteractions;
import io.sarl.core.Destroy;
import io.sarl.core.ExternalContextAccess;
import io.sarl.core.Initialize;
import io.sarl.core.InnerContextAccess;
import io.sarl.core.Lifecycle;
import io.sarl.core.Logging;
import io.sarl.core.Schedules;
import io.sarl.core.Time;
import io.sarl.eventdispatching.BehaviorGuardEvaluator;
import io.sarl.eventdispatching.BehaviorGuardEvaluatorRegistry;
import io.sarl.lang.core.Address;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.AgentContext;
import io.sarl.lang.core.Behavior;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.EventListener;
import io.sarl.lang.core.EventSpace;
import io.sarl.lang.core.EventSpaceSpecification;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.Skill;
import io.sarl.lang.core.Space;
import io.sarl.lang.core.SpaceID;
import io.sarl.lang.util.SynchronizedCollection;
import io.sarl.lang.util.SynchronizedSet;
import io.sarl.util.Collections3;
import io.sarl.util.Scopes;

@SuppressWarnings("deprecation")
class TMSarlAgent extends org.arakhne.tinyMAS.core.Agent implements EventListener {

	private final io.sarl.lang.core.Agent sarlAgent;
	
	private final WeakReference<TMDefaultSpace> defaultSpace;
	
	private final BehaviorGuardEvaluatorRegistry evaluatorRegistry = new BehaviorGuardEvaluatorRegistry();

	private Object[] parameters;

	private BehaviorsSkill behaviorSkill;
	private LoggingSkill loggingSkill;
	private DefaultContextInteractionsSkill spaceSkill;
	private LifecycleSkill lifeSkill;
	private SchedulesSkill scheduleSkill;
	private ExternalContextAccessSkill externContextSkill;
	private InnerContextAccessSkill innerContextSkill;
	private TimeSkill timeSkill;
	
	public TMSarlAgent(TMDefaultSpace defaultSpace, io.sarl.lang.core.Agent sarlAgent, Object[] parameters) {
		super();
		this.sarlAgent = sarlAgent;
		this.defaultSpace = new WeakReference<>(defaultSpace);
		this.parameters = parameters;
	}
	
	@Override
	public String toString() {
		return getSarlAgent().toString();
	}
	
	protected TMDefaultSpace getDefaultSpace() {
		return this.defaultSpace.get();
	}
	
	protected io.sarl.lang.core.Agent getSarlAgent() {
		return this.sarlAgent;
	}
	
	@Override
	public UUID getID() {
		return this.sarlAgent.getID();
	}

	@Override
	public void receiveEvent(Event event) {
		if (event.getSource() == null) {
			event.setSource(getDefaultSpace().getAddress(this.sarlAgent.getID()));
		}
		fireEvent(event);
	}
	
	void fireEvent(Event event) {
		Collection<BehaviorGuardEvaluator> evaluators = this.evaluatorRegistry.getBehaviorGuardEvaluators(event);
		Collection<Runnable> handlers = new ArrayList<>();
		for (BehaviorGuardEvaluator evaluator : evaluators) {
			try {
				evaluator.evaluateGuard(event, handlers);
			} catch(NoReturnCodeException e) {
				//
			} catch (Throwable e) {
				this.loggingSkill.error(e.getLocalizedMessage(), e);
			}
		}
		for (Runnable handler : handlers) {
			try {
				handler.run();
			} catch(NoReturnCodeException e) {
				//
			} catch (Throwable e) {
				this.loggingSkill.error(e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public void start() {
		super.start();
		
		UUID aid = Identifiers.toUUID(getId());
		if (!aid.equals(getSarlAgent().getID())) {
			throw new IllegalStateException("Sarl agent and TinyMAS agent have not the same ID.\nTinyMAS id: " + getId().toString() //$NON-NLS-1$
					+ "\nSARL id: " + getSarlAgent().getID().toString()); //$NON-NLS-1$
		}
		
		this.evaluatorRegistry.register(getSarlAgent());

		this.behaviorSkill = new BehaviorsSkill();
		this.loggingSkill = new LoggingSkill();
		this.spaceSkill = new DefaultContextInteractionsSkill();
		this.lifeSkill = new LifecycleSkill();
		this.scheduleSkill = new SchedulesSkill();
		this.externContextSkill = new ExternalContextAccessSkill();
		this.innerContextSkill = new InnerContextAccessSkill();
		this.timeSkill = new TimeSkill();
		
		try {
			Method method = io.sarl.lang.core.Agent.class.getDeclaredMethod("setSkill", Class.class, Skill.class); //$NON-NLS-1$
			boolean isAcc = method.isAccessible();
			try {
				method.setAccessible(true);
				method.invoke(getSarlAgent(), Behaviors.class, this.behaviorSkill);
				method.invoke(getSarlAgent(), Logging.class, this.loggingSkill);
				method.invoke(getSarlAgent(), DefaultContextInteractions.class, this.spaceSkill);
				method.invoke(getSarlAgent(), Lifecycle.class, this.lifeSkill);
				method.invoke(getSarlAgent(), Schedules.class, this.scheduleSkill);
				method.invoke(getSarlAgent(), ExternalContextAccess.class, this.externContextSkill);
				method.invoke(getSarlAgent(), InnerContextAccess.class, this.innerContextSkill);
				method.invoke(getSarlAgent(), Time.class, this.timeSkill);
			} finally {
				method.setAccessible(isAcc);
			}
		} catch (Exception e) {
			throw new Error(e);
		}

		final Initialize initializeEvent = new Initialize();
		if (this.parameters != null) {
			initializeEvent.parameters = this.parameters;
			this.parameters = null;
		}
		receiveEvent(initializeEvent);
	}
	
	@Override
	public void live() {
		for (Object beh : this.behaviorSkill.getRegistrationWaiters()) {
			this.evaluatorRegistry.register(beh);
		}
		for (Object beh : this.behaviorSkill.getUnregistrationWaiters()) {
			this.evaluatorRegistry.unregister(beh);
		}
		while (hasMessage()) {
			Message message = getNextMessage();
			Object content = message.getContent();
			if (content instanceof Event) {
				receiveEvent((Event) content);
			}
		}
		this.scheduleSkill.runTasks();
	}
	
	@Override
	public void stop() {
		receiveEvent(new Destroy());
		try {
			Method method = io.sarl.lang.core.Agent.class.getDeclaredMethod("clearSkill", Class.class); //$NON-NLS-1$
			boolean isAcc = method.isAccessible();
			try {
				method.setAccessible(true);
				method.invoke(getSarlAgent(), Time.class);
				method.invoke(getSarlAgent(), InnerContextAccess.class);
				method.invoke(getSarlAgent(), ExternalContextAccess.class);
				method.invoke(getSarlAgent(), Schedules.class);
				method.invoke(getSarlAgent(), Lifecycle.class);
				method.invoke(getSarlAgent(), DefaultContextInteractions.class);
				method.invoke(getSarlAgent(), Logging.class);
				method.invoke(getSarlAgent(), Behaviors.class);
			} finally {
				method.setAccessible(isAcc);
			}
		} catch (Exception e) {
			throw new Error(e);
		}
		this.evaluatorRegistry.unregister(getSarlAgent());
		super.stop();
	}
	
	private class DefaultContextInteractionsSkill extends Skill implements DefaultContextInteractions {

		DefaultContextInteractionsSkill() {
			//
		}
		
		@Override
		public AgentContext getDefaultContext() {
			return TMSarlAgent.this.getDefaultSpace().getAgentContext();
		}

		@Override
		public EventSpace getDefaultSpace() {
			return TMSarlAgent.this.getDefaultSpace();
		}

		@Override
		public Address getDefaultAddress() {
			return TMSarlAgent.this.getDefaultSpace().getAddress(getSarlAgent().getID());
		}

		@Override
		public void emit(Event e, Scope<Address> scope) {
			if (e.getSource() == null) {
				e.setSource(getDefaultSpace().getAddress(getID()));
			}
			TMSarlAgent.this.getDefaultSpace().emit(e, scope);
		}

		@Override
		public void emit(Event e) {
			if (e.getSource() == null) {
				e.setSource(getDefaultSpace().getAddress(getID()));
			}
			TMSarlAgent.this.getDefaultSpace().emit(e);
		}

		@Override
		@Deprecated
		public void receive(UUID receiver, Event e) {
			willReceive(receiver, e);
		}

		@Override
		public boolean isDefaultSpace(Space space) {
			return space.getID().equals(TMSarlAgent.this.getDefaultSpace().getID());
		}

		@Override
		public boolean isDefaultSpace(SpaceID space) {
			return space.equals(TMSarlAgent.this.getDefaultSpace().getID());
		}

		@Override
		public boolean isDefaultSpace(UUID space) {
			return space.equals(TMSarlAgent.this.getDefaultSpace().getID().getID());
		}

		@Override
		public boolean isInDefaultSpace(Event event) {
			return isDefaultSpace(event.getSource().getSpaceId());
		}

		@Override
		public boolean isDefaultContext(AgentContext context) {
			return context.getID().equals(TMSarlAgent.this.getDefaultSpace().getAgentContext().getID());
		}

		@Override
		public boolean isDefaultContext(UUID contextID) {
			return contextID.equals(TMSarlAgent.this.getDefaultSpace().getAgentContext().getID());
		}

		@Override
		public UUID spawn(Class<? extends io.sarl.lang.core.Agent> aAgent, Object... params) {
			return TMSarlAgent.this.getDefaultSpace().spawn(aAgent, null, params);
		}

		@Override
		public void willReceive(UUID receiver, Event event) {
			if (event.getSource() == null) {
				event.setSource(getDefaultSpace().getAddress(getID()));
			}
			TMSarlAgent.this.getDefaultSpace().emit(event,
					Scopes.addresses(TMSarlAgent.this.getDefaultSpace().getAddress(receiver)));
		}

	}

	private class LifecycleSkill extends Skill implements Lifecycle {

		LifecycleSkill() {
			//
		}
		
		@Override
		public UUID spawnInContext(Class<? extends io.sarl.lang.core.Agent> agentClass, AgentContext context,
				Object... params) {
			if (context.getID().equals(TMSarlAgent.this.getDefaultSpace().getAgentContext().getID())) {
				return TMSarlAgent.this.getDefaultSpace().spawn(agentClass, null, params);
			}
			return null;
		}

		@Override
		public UUID spawnInContextWithID(Class<? extends io.sarl.lang.core.Agent> agentClass, UUID agentID,
				AgentContext context, Object... params) {
			if (context.getID().equals(TMSarlAgent.this.getDefaultSpace().getAgentContext().getID())) {
				return TMSarlAgent.this.getDefaultSpace().spawn(agentClass, agentID, params);
			}
			return null;
		}

		@Override
		public void killMe() {
			TMSarlAgent.this.killMe();
			throw new NoReturnCodeException();
		}
		
	}
	
	private class LoggingSkill extends Skill implements Logging {

		LoggingSkill() {
			//
		}
		
		@Override
		public void setLoggingName(String message) {
			getId().setStringRepresentation(message);
		}

		@Override
		public void println(Object message) {
			System.out.println("[" + getId().getString() + "] " + message); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public boolean isErrorLogEnabled() {
			return true;
		}

		@Override
		public boolean isWarningLogEnabled() {
			return true;
		}

		@Override
		public boolean isInfoLogEnabled() {
			return true;
		}

		@Override
		public boolean isDebugLogEnabled() {
			return true;
		}

		@Override
		public int getLogLevel() {
			return 0;
		}

		@Override
		public void setLogLevel(int level) {
			//
		}

		@Override
		public void error(Object message, Throwable exception, Object... parameters) {
			System.out.println("[" + getId().getString() + "] ERROR: " + message); //$NON-NLS-1$ //$NON-NLS-2$
			if (exception != null) {
				exception.printStackTrace(System.out);
			}
		}

		@Override
		public void warning(Object message, Throwable exception, Object... parameters) {
			System.out.println("[" + getId().getString() + "] WARNING: " + message); //$NON-NLS-1$ //$NON-NLS-2$
			if (exception != null) {
				exception.printStackTrace(System.out);
			}
		}

		@Override
		public void info(Object message, Object... parameters) {
			System.out.println("[" + getId().getString() + "] INFO: " + message); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public void debug(Object message, Object... parameters) {
			System.out.println("[" + getId().getString() + "] DEBUG: " + message); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public void error(Object message, Object... parameters) {
			System.out.println("[" + getId().getString() + "] ERROR: " + message); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public void warning(Object message, Object... parameters) {
			System.out.println("[" + getId().getString() + "] WARNING: " + message); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
	}
	
	private class BehaviorsSkill extends Skill implements Behaviors {

		private final List<Behavior> behaviors = new ArrayList<>();

		private List<Object> registrationWaiters = new ArrayList<>();

		private List<Object> unregistrationWaiters = new ArrayList<>();

		private Address innerAddress;

		BehaviorsSkill() {
			//
		}
		
		@SuppressWarnings("synthetic-access")
		@Override
		protected void uninstall() {
			for (Behavior beh : this.behaviors) {
				TMSarlAgent.this.evaluatorRegistry.unregister(beh);
			}
		}
		
		public Iterable<Object> getUnregistrationWaiters() {
			final Iterable<Object> col = this.registrationWaiters;
			this.registrationWaiters = new ArrayList<>();
			return col;
		}

		public Iterable<Object> getRegistrationWaiters() {
			final Iterable<Object> col = this.unregistrationWaiters;
			this.unregistrationWaiters = new ArrayList<>();
			return col;
		}

		@Override
		public synchronized Behavior registerBehavior(Behavior attitude) {
			if (attitude != null) {
				this.behaviors.add(attitude);
				this.unregistrationWaiters.remove(attitude);
				this.registrationWaiters.add(attitude);
			}
			return attitude;
		}

		@Override
		public synchronized Behavior unregisterBehavior(Behavior attitude) {
			if (attitude != null) {
				this.behaviors.remove(attitude);
				this.registrationWaiters.remove(attitude);
				this.unregistrationWaiters.add(attitude);
			}
			return attitude;
		}
		
		@Override
		public void wake(Event evt) {
			wake(evt, null);
		}
		
		private Address getInnerAddress() {
			if (this.innerAddress == null) {
				final UUID myId = getID();
				final SpaceID spaceId = new SpaceID(myId, UUID.randomUUID(), EventSpaceSpecification.class);
				this.innerAddress = new Address(spaceId, myId);
			}
			return this.innerAddress;
		}

		@Override
		public void wake(Event evt, Scope<Address> scope) {
			if (scope == null || scope.matches(getInnerAddress())) { 
				asEventListener().receiveEvent(evt);
			}
		}

		@Override
		public EventListener asEventListener() {
			return TMSarlAgent.this;
		}
		
	}

	private class SchedulesSkill extends Skill implements Schedules {
		
		private final Map<Long, Collection<AgentTask>> tasks = new TreeMap<>();
		
		SchedulesSkill() {
			//
		}

		private void scheduleTask(long at, AgentTask task) {
			Collection<AgentTask> list = this.tasks.get(at);
			if (list == null) {
				list = new ArrayList<>();
				this.tasks.put(at, list);
			}
			list.add(task);
		}
		
		public void runTasks() {
			long currentTime = (long) getSimulationTime(TimeUnit.MILLISECONDS);
			Collection<AgentTask> list = this.tasks.remove(currentTime);
			if (list != null) {
				for (AgentTask task : list) {
					boolean canceled = false;
					if (task instanceof Task) {
						canceled = ((Task) task).isCanceled();
					}
					if (!canceled) {
						Function1<io.sarl.lang.core.Agent, Boolean> guard = task.getGuard();
						if (guard == null || Boolean.TRUE == guard.apply(getSarlAgent())) {
							Procedure1<? super io.sarl.lang.core.Agent> code = task.getProcedure();
							if (code != null) {
								code.apply(getSarlAgent());
							}
							if (task instanceof Task) {
								Task tmTask = (Task) task;
								if (tmTask.getPeriod() > 0) {
									scheduleTask(currentTime + tmTask.getPeriod(), task);
								}
							}
						}
					}
				}
			}
		}
		
		@Override
		public AgentTask in(long delay, Procedure1<? super io.sarl.lang.core.Agent> procedure) {
			return in(null, delay, procedure);
		}

		@Override
		public AgentTask in(AgentTask task, long delay, Procedure1<? super io.sarl.lang.core.Agent> procedure) {
			AgentTask theTask = task;
			if (theTask == null) {
				theTask = new Task();
				theTask.setName(UUID.randomUUID().toString());
			}
			long time = (long) (getSimulationTime(TimeUnit.MILLISECONDS) + delay);
			theTask.setProcedure(procedure);
			scheduleTask(time, theTask);
			return theTask;
		}

		@Override
		public AgentTask task(String name) {
			AgentTask theTask = new Task();
			theTask.setName(UUID.randomUUID().toString());
			return theTask;
		}

		@Override
		public boolean cancel(AgentTask task) {
			if (task instanceof Task) {
				((Task) task).cancel();
				return true;
			}
			return false;
		}

		@Override
		public boolean cancel(AgentTask task, boolean mayInterruptIfRunning) {
			if (task instanceof Task) {
				((Task) task).cancel();
				return true;
			}
			return false;
		}

		@Override
		public AgentTask every(long period, Procedure1<? super io.sarl.lang.core.Agent> procedure) {
			return every(null, period, procedure);
		}

		@Override
		public AgentTask every(AgentTask task, long period, Procedure1<? super io.sarl.lang.core.Agent> procedure) {
			AgentTask theTask = task;
			if (theTask == null) {
				theTask = new Task();
				theTask.setName(UUID.randomUUID().toString());
			}
			long time = (long) (getSimulationTime(TimeUnit.MILLISECONDS) + period);
			theTask.setProcedure(procedure);
			if (theTask instanceof Task) {
				((Task) theTask).setPeriod(period);
			}
			scheduleTask(time, theTask);
			return theTask;
		}

		@Override
		public AgentTask atFixedDelay(long delay, Procedure1<? super Agent> procedure) {
			return atFixedDelay(null, delay, procedure);
		}

		@Override
		public AgentTask atFixedDelay(AgentTask task, long delay, Procedure1<? super Agent> procedure) {
			return every(task, delay, procedure);
		}

		@Override
		public AgentTask execute(Procedure1<? super Agent> procedure) {
			return execute(null, procedure);
		}

		@Override
		public AgentTask execute(AgentTask task, Procedure1<? super Agent> procedure) {
			return in(task, (long) getSimulationStepDuration(TimeUnit.MILLISECONDS), procedure);
		}
		
	}

	private static class Task extends AgentTask {

		private boolean isCanceled = false;
		
		private long period = 0;
		
		Task() {
			//
		}
		
		public boolean isCanceled() {
			return this.isCanceled;
		}
		
		public void cancel() {
			this.isCanceled = true;
		}
		
		public long getPeriod() {
			return this.period;
		}
		
		public void setPeriod(long period) {
			this.period = period;
		}

	}

	private class ExternalContextAccessSkill extends Skill implements ExternalContextAccess {

		ExternalContextAccessSkill() {
			//
		}
		
		@SuppressWarnings("synthetic-access")
		@Override
		public SynchronizedCollection<AgentContext> getAllContexts() {
			Collection<AgentContext> contexts = Collections.singletonList(
					TMSarlAgent.this.spaceSkill.getDefaultContext());
			return Collections3.<AgentContext>synchronizedCollection(contexts, contexts);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public AgentContext getContext(UUID contextID) {
			AgentContext context = TMSarlAgent.this.spaceSkill.getDefaultContext();
			if (context.getID().equals(contextID)) {
				return context;
			}
			return null;
		}

		@Override
		public void join(UUID contextID, UUID expectedDefaultSpaceID) {
			//XXX
			throw new UnsupportedOperationException();
		}

		@Override
		public void leave(UUID contextID) {
			//XXX
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isInSpace(Event event, Space space) {
			return isInSpace(event, space.getID());
		}

		@Override
		public boolean isInSpace(Event event, SpaceID spaceID) {
			return spaceID.equals(event.getSource().getSpaceId());
		}

		@Override
		public boolean isInSpace(Event event, UUID spaceID) {
			return spaceID.equals(event.getSource().getSpaceId().getID());
		}
		
	}

	private class InnerContextAccessSkill extends Skill implements InnerContextAccess {

		InnerContextAccessSkill() {
			//
		}
		
		@Override
		public AgentContext getInnerContext() {
			//XXX
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasMemberAgent() {
			return false;
		}

		@Override
		public int getMemberAgentCount() {
			return 0;
		}

		@Override
		public SynchronizedSet<UUID> getMemberAgents() {
			return Collections3.emptySynchronizedSet();
		}

		@Override
		public boolean isInnerDefaultSpace(Space space) {
			return isInnerDefaultSpace(space.getID());
		}

		@Override
		public boolean isInnerDefaultSpace(SpaceID spaceID) {
			return false;
		}

		@Override
		public boolean isInnerDefaultSpace(UUID spaceID) {
			return false;
		}

		@Override
		public boolean isInInnerDefaultSpace(Event event) {
			return false;
		}
		
	}

	private class TimeSkill extends Skill implements Time {

		TimeSkill() {
			//
		}

		@Override
		public double getTime(TimeUnit timeUnit) {
			if (timeUnit == null) {
				timeUnit = TimeUnit.SECONDS;
			}
			return TMSarlAgent.this.getSimulationTime(timeUnit);
		}

		@Override
		public double getTime() {
			return getTime(null);
		}

		@Override
		public double getOSTimeFactor() {
			return TMSarlAgent.this.getSimulationStepDuration(TimeUnit.SECONDS);
		}

	}
	
}
