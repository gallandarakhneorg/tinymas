/* 
 * $Id$
 * 
 * Copyright (C) 2004-2009 St&eacute;phane GALLAND, Nicolas GAUD
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.KernelIdentifier;
import org.arakhne.tinyMAS.core.Message;
import org.arakhne.tinyMAS.core.MessageTransportService;
import org.arakhne.tinyMAS.core.YellowPages;

/** This components extends the basic Message Transport Service
 * with a network support.
 * <p>
 * This implementation of a MTS is able to communicated with
 * other network MTS, ie other kernels.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class NetworkMessageTransportService extends MessageTransportService {

	protected final List<KernelIdentifier> __other_kernels = new ArrayList<KernelIdentifier>();
	
	private final InetAddress __other_kernel_adr;
	private final int __other_kernel_port;
	
	protected final ServerSocket __server_socket;
	protected final ExecutorService __server_thread;
	
	protected volatile boolean __mts_presented = false;
	
	private Map<String, Socket> sockets = new TreeMap<String, Socket>();

	public NetworkMessageTransportService(int socket_port) {
		this(socket_port,null,0);
	}
		
	public NetworkMessageTransportService(int socket_port, InetAddress other_kernel_adr, int other_kernel_port) {
		try {
			this.__server_socket = new ServerSocket(socket_port);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		ThreadFactory factory = new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread th = new Thread(r,
						(r instanceof ConnexionListener) ?
								"MTS listener" : //$NON-NLS-1$
								"MTS message parser"); //$NON-NLS-1$
				th.setDaemon(true);
				return th;
			}
			
		};
		this.__server_thread = Executors.newCachedThreadPool(factory);
		this.__other_kernel_adr = other_kernel_adr;
		this.__other_kernel_port = other_kernel_port;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected KernelIdentifier getKernelId() {
		return new KernelIdentifier(this.__server_socket.getLocalPort());
	}

	/** {@inheritDoc}
	 */
	void broadcast(KernelMessage m) {
		assert(m!=null);
		KernelIdentifier[] tab = new KernelIdentifier[this.__other_kernels.size()];
		this.__other_kernels.toArray(tab);
		
		for(int i=0; i<tab.length; i++) {
			send(tab[i],m);
		}
		
		tab = null;
	}

	/** {@inheritDoc}
	 */
	boolean send(KernelIdentifier kernel, KernelMessage m) {
		assert(kernel!=null);
		assert(m!=null);
		return sendSocket(
				kernel.getKernelAddress().getAddress(),
				kernel.getKernelAddress().getPort(),
				m);
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean send(Message m) {
		assert(m!=null);
		KernelIdentifier target_kernel = m.TO.getKernelId();
		
		// Check if the message is for a "local" agent
		if (target_kernel.equals(Kernel.getSingleton().getKernelId())) {
			return super.send(m);
		}
		
		return sendSocket(
				target_kernel.getKernelAddress().getAddress(),
				target_kernel.getKernelAddress().getPort(),
				m);
	}

	/** Send a message with a socket.
	 */
	protected boolean sendSocket(InetAddress adr, int port, Serializable m) {
		assert(adr!=null);
		String socketId = adr.getHostAddress()+":"+port; //$NON-NLS-1$
		try {
			//Get the kernel address of the kernel on which the target agent is located
			//and open a socket to the other kernel
			Socket socket = this.sockets.get(socketId);
			if (socket==null) {
				socket = new Socket(adr,port);
				socket.shutdownInput();
				this.sockets.put(socketId, socket);
			}
			
			// Serialize the message
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(m);
			oos.flush();
			
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			// Close the socket
			this.sockets.remove(socketId);
		}
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void stopMTS() {
		this.__server_thread.shutdownNow();
		broadcast(new KernelMessage(KernelMessage.Type.KERNEL_DELETION,Kernel.getSingleton().getKernelId()));
		this.__other_kernels.clear();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void startMTS() {
		NetworkKernel.displayKernelMessage("\tMTS listening on port " //$NON-NLS-1$
				+this.__server_socket.getLocalPort());
		this.__server_thread.submit(new ConnexionListener());
		
		
		if (this.__other_kernel_adr!=null) {
			NetworkKernel.displayKernelMessage("\twaiting for the ack of the community"); //$NON-NLS-1$
			
			sendSocket(
					this.__other_kernel_adr,this.__other_kernel_port,
					new KernelMessage(KernelMessage.Type.KERNEL_PRESENTATION,Kernel.getSingleton().getKernelId()));
			while (!this.__mts_presented) {
				// waiting
				Thread.yield();
			}
		}
	}

	/** Socket server waiting for agent's messages.
	 * 
	 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * @deprecated See {@link "http://www.janus-project.org"}
	 */
	@Deprecated
	class ConnexionListener implements Runnable {

		public ConnexionListener() {
			//
		}
		
		public void run() {
			while(true) {
				try {
					Socket client = NetworkMessageTransportService.this.__server_socket.accept();
					
					if (client!=null) {
						NetworkMessageTransportService.this.__server_thread.submit(
								new RemoteKernelListener(client));
						client = null;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/** Component that treat messages arriving form another kernel.
	 * 
	 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * @deprecated See {@link "http://www.janus-project.org"}
	 */
	@Deprecated
	class RemoteKernelListener implements Runnable {

		private final Socket __client;
		
		public RemoteKernelListener(Socket client) {
			assert(client!=null);
			this.__client = client;
		}
		
		public void run() {
			try {
				while (true) {
			
					// UnSerialize the message
					ObjectInputStream oos = new ObjectInputStream(this.__client.getInputStream());
					Object obj = oos.readObject();
				
					// Treat the red object
					if ((obj!=null)&&
						(obj instanceof Message)) {
						treatAgentMessage((Message)obj);
					}
					else if ((obj!=null)&&
							 (obj instanceof KernelMessage)) {
						treatKernelMessage((KernelMessage)obj);
					}
				}
			}
			catch(Throwable _) {
				// close the connexion silently
				try {
					this.__client.close();
				}
				catch(Throwable __) {
					//
				}
			}
		}
		
		/** Treating agent's message.
		 */
		@SuppressWarnings("synthetic-access")
		protected void treatAgentMessage(Message msg) {
			assert(msg!=null);
			if (msg.TO.getKernelId().equals(Kernel.getSingleton().getKernelId())) {
				// The message is for this kernel
				NetworkMessageTransportService.this.registerInLocalBoxes(msg);
			}
			else {
				NetworkKernel.displayKernelMessage("Ignoring network message from:"+msg.FROM.toString()); //$NON-NLS-1$
			}
		}

		/** Treating low-level message.
		 */
		protected void treatKernelMessage(KernelMessage msg) {
			assert(msg!=null);
			switch(msg.TYPE) {
			
			case KERNEL_DELETION:
				NetworkKernel.displayKernelMessage("Shutdown of the kernel " //$NON-NLS-1$
						+msg.IDENTIFIER.toString());
				NetworkMessageTransportService.this.__other_kernels.remove(msg.IDENTIFIER);
				break;
				
			case KERNEL_PRESENTATION:
				NetworkKernel.displayKernelMessage("Presentation of the kernel " //$NON-NLS-1$
						+msg.IDENTIFIER.toString());
				KernelIdentifier ki = (KernelIdentifier)msg.IDENTIFIER;
				NetworkMessageTransportService.this.__other_kernels.add(ki);
				NetworkMessageTransportService.this.sendSocket(
						ki.getKernelAddress().getAddress(),
						ki.getKernelAddress().getPort(),
						new KernelMessage(KernelMessage.Type.KERNEL_PRESENTATION_ACK,
								Kernel.getSingleton().getKernelId()));
				NetworkKernel.getSingleton().getYellowPageSystem().onKernelMessage(msg);
				break;
				
			case KERNEL_PRESENTATION_ACK:
				NetworkKernel.displayKernelMessage("\tI'm beeing presented to the community"); //$NON-NLS-1$
				NetworkMessageTransportService.this.__other_kernels.add((KernelIdentifier)msg.IDENTIFIER);
				NetworkMessageTransportService.this.__mts_presented = true;
				break;
				
			default:
				try {
					if ((msg.TYPE!=null)&&
						(YellowPages.class.asSubclass(msg.TARGET)!=null)) {
						NetworkKernel.getSingleton().getYellowPageSystem().onKernelMessage(msg);
					}
				}
				catch(Exception _) {
					//
				}
				break;
			}
		}

	}

}