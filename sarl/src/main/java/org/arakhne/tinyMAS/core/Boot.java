package org.arakhne.tinyMAS.core;

import org.arakhne.afc.vmutil.VMCommandLine;

import io.sarl.core.AgentKilled;
import io.sarl.core.AgentSpawned;
import io.sarl.lang.core.Address;

@SuppressWarnings({"rawtypes", "deprecation"})
public class Boot extends Kernel {

	private final TMDefaultSpace defaultSpace;
	
	@SuppressWarnings("unchecked")
	Boot(TMDefaultSpace defaultSpace, MessageTransportService mts, final WhitePages whitePages, YellowPages yellowPages) {
		super(mts, whitePages, yellowPages);
		this.defaultSpace = defaultSpace;
		this.defaultSpace.setKernelID(getKernelId());
		addKernelListener(new KernelAdapter() {
			@Override
			public void kernelRefreshAllowed(Kernel<?, ?, ?, ?> kernel) {
				for (TMSarlAgent agent : Boot.this.defaultSpace.consumeAgentToLaunch()) {
					Spawner.spawn(Boot.this, agent);
				}
			}
			@Override
			public void kernelAgentAdded(Kernel<?, ?, ?, ?> kernel, Agent agent, AgentIdentifier id) {
				final Address source = new Address(
						Boot.this.defaultSpace.getSpaceID(),
						TMDefaultSpace.TINYMAS_DEFAULT_SPACE_ID);
				final String agentType;
				if (agent instanceof TMSarlAgent) {
					agentType = ((TMSarlAgent) agent).getSarlAgent().getClass().getName();
				} else {
					agentType = null;
				}
				final AgentSpawned spawnEvent = new AgentSpawned(source,
						Identifiers.toUUID(id),
						agentType);
				Boot.this.defaultSpace.emit(spawnEvent);
				kernelAgentAdded(kernel, id);
			}
			@Override
			public void kernelAgentRemoved(Kernel<?, ?, ?, ?> kernel, Agent agent, AgentIdentifier id) {
				final Address source = new Address(
						Boot.this.defaultSpace.getSpaceID(),
						TMDefaultSpace.TINYMAS_DEFAULT_SPACE_ID);
				final String agentType;
				if (agent instanceof TMSarlAgent) {
					agentType = ((TMSarlAgent) agent).getSarlAgent().getClass().getName();
				} else {
					agentType = null;
				}
				final AgentKilled spawnEvent = new AgentKilled(source,
						Identifiers.toUUID(id),
						agentType);
				Boot.this.defaultSpace.emit(spawnEvent);
				kernelAgentRemoved(kernel, id);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		VMCommandLine.saveVMParametersIfNotSet(Boot.class, args);
		
		WhitePages whitePages = new WhitePages<>();
		YellowPages yellowPages = new YellowPages();
		MessageTransportService mts = new MessageTransportService();
		
		TMDefaultSpace defaultSpace = new TMDefaultSpace(whitePages, mts);

		Boot kernel = new Boot(defaultSpace, mts, whitePages, yellowPages);
		
		TMAgentContext context = new TMAgentContext(defaultSpace);
		defaultSpace.setAgentContext(context);
		
		final String agentName = VMCommandLine.shiftCommandLineParameters();
		Class<?> agentType = Class.forName(agentName);
		Object[] params = new Object[VMCommandLine.getCommandLineParameters().length];
		for (int i = 0; i < params.length; ++i) {
			params[i] = VMCommandLine.getCommandLineParameters()[i];
		}
		if (io.sarl.lang.core.Agent.class.isAssignableFrom(agentType)) {
			Spawner.spawn(kernel, defaultSpace,
					(Class<? extends io.sarl.lang.core.Agent>) agentType,
					TMDefaultSpace.TINYMAS_DEFAULT_SPACE_ID,
					null,
					params);
		}
		
		kernel.run();
		
		System.exit(0);
	}
	
}
