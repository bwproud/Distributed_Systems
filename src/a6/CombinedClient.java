package a6;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import StringProcessors.HalloweenCommandProcessor;
import a6.consensus.ClientConsensusObject;
import a6.consensus.Consensus;
import a6.consensus.Consensus.states;
import a6.gipc.GIPCClientObject;
import a6.gipc.GIPCClientObjectImpl;
import a6.gipc.GIPCServerObject;
import a6.nio.NioClient;
import a6.nio.RspHandler;
import a6.rmi.RMIClientObject;
import a6.rmi.RMIClientObjectImpl;
import a6.rmi.RMIServerObject;
import a4.ACustomDuplexObjectInputPortFactory;
import a4.ACustomDuplexReceivedCallInvokerFactory;
import a4.ACustomSentCallCompleterFactory;
import a4.ACustomSerializerFactory;
import a4.AnAsynchronousCustomDuplexReceivedCallInvokerFactory;
import inputport.datacomm.duplex.object.DuplexObjectInputPortSelector;
import inputport.datacomm.simplex.buffer.nio.AScatterGatherSelectionManager;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import inputport.rpc.duplex.DuplexReceivedCallInvokerSelector;
import inputport.rpc.duplex.DuplexSentCallCompleterSelector;
import main.BeauAndersonFinalProject;
import port.trace.nio.LocalCommandObserved;
import port.trace.nio.NIOTraceUtility;
import serialization.SerializerSelector;
import util.trace.TraceableBus;
import util.trace.Tracer;

public class CombinedClient implements PropertyChangeListener{
	HalloweenCommandProcessor hcp;
	NioClient client;
	GIPCServerObject gipcServer;
	RMIServerObject rmiServer;
	ResponseHandler[] handlers;
	static String hostName="localhost";
	static int iterations=500;
	ClientConsensusObject mechanism;
	
	public static void setFactories() {
		DuplexReceivedCallInvokerSelector.setReceivedCallInvokerFactory(
				new ACustomDuplexReceivedCallInvokerFactory());
//		DuplexReceivedCallInvokerSelector.setReceivedCallInvokerFactory(
//				new AnAsynchronousCustomDuplexReceivedCallInvokerFactory());
		DuplexSentCallCompleterSelector.setDuplexSentCallCompleterFactory(
				new ACustomSentCallCompleterFactory());
		DuplexObjectInputPortSelector.setDuplexInputPortFactory(
				new ACustomDuplexObjectInputPortFactory());
	}
	
	public CombinedClient(HalloweenCommandProcessor hcp, ClientConsensusObject mechanism) throws NotBoundException, UnknownHostException, IOException{
		hcp.addPropertyChangeListener(this);
		hcp.setConnectedToSimulation(false);
		port.trace.consensus.ConsensusTraceUtility.setTracing();
		this.hcp=hcp;
		this.mechanism=mechanism;
		handlers = new ResponseHandler[3];
		//setFactories();
		gipcStart(hcp);
		rmiStart(hcp, mechanism);
		nioStart(hcp);
	}
	
	public static void main(String args[]) throws NotBoundException, UnknownHostException, IOException{
		Tracer.showWarnings(false);
		if(args.length>0){
			hostName=args[0];
		}
		if(args.length>1){
			iterations=Integer.parseInt(args[1]);
		}
		HalloweenCommandProcessor hcp = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, 0, 400, 765, 0, 0);
		CombinedClient client = new CombinedClient(hcp);
	}

	
	public void gipcStart(HalloweenCommandProcessor hcp) throws RemoteException{
		Random rand = new Random();
		AScatterGatherSelectionManager.setMaxOutstandingWrites(iterations);
		//TraceableBus.addTraceableListener(new ATraceableListener());
		//NIOTraceUtility.setTracing();
		GIPCRegistry registry = GIPCLocateRegistry.getRegistry(hostName, 9009, "client"+rand.nextDouble());
		gipcServer = (GIPCServerObject) registry.lookup(GIPCServerObject.class, "clientRegistry");
		GIPCClientObject clientObject = new GIPCClientObjectImpl(hcp, gipcServer);
		handlers[2]=clientObject;
		gipcServer.addClient(clientObject);
//		ConsoleListenerTwo consoleListener = new ConsoleListenerTwo(hcp, clientObject,gipcServer);
//		Thread cl = new Thread(consoleListener);
//		cl.start();
	}
	
	public void rmiStart(HalloweenCommandProcessor hcp, ClientConsensusObject mechanism) throws RemoteException, NotBoundException{
		Registry registry = LocateRegistry.getRegistry(hostName,9990);
		rmiServer = (RMIServerObject) registry.lookup("clientRegistry");
		RMIClientObject clientObject = new RMIClientObjectImpl(hcp, rmiServer);
		handlers[0]=clientObject;
		rmiServer.addClient(clientObject);
		ConsoleListener consoleListener = new ConsoleListener(hcp, clientObject,rmiServer, iterations, mechanism);
		Thread cl = new Thread(consoleListener);
		cl.start();
	}
	
	public void nioStart(HalloweenCommandProcessor hcp) throws UnknownHostException, IOException{
		  client = new NioClient(InetAddress.getByName(hostName), 9900);
	      Thread t = new Thread(client);
	      t.setDaemon(true);
	      t.start();
	      t.setName("Selector Thread");
	      RspHandler handler = new RspHandler(hcp);
	      handlers[1]=handler;
	      client.setup(handler);
		  Thread handle = new Thread(handler);
		  handle.setName("Remote Operations Thread");
		  handle.start();
	}

	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		try {
			if (!anEvent.getPropertyName().equals("InputString")) return;
			String newCommand = (String) anEvent.getNewValue();
			if(!Consensus.getLocal()){
				if(!Consensus.getAtomic().getChanging()){
					//if(!Consensus.getAtomic().getAtomic() && Consensus.getIPC().getState()!=states.CONSENSUS_MECHANISM){
					//	hcp.processCommand(newCommand);
					//}
					switch(Consensus.getIPC().getState()){
						case GIPC:
							gipcServer.broadcast(newCommand, handlers[2]);
							break;
						case RMI:
							rmiServer.broadcast(newCommand, (Remote)handlers[0]);
							break;
						case NIO:
							LocalCommandObserved.newCase(this, newCommand);
							client.send(newCommand.getBytes(), (RspHandler)handlers[1]);
							break;
						case NONATOMICASYNC:
						case NONATOMICSYNC:
						case CENTRALIZEDASYNC:
						case CENTRALIZEDSYNC:
						case SEQUENTIALPAXOS:
							mechanism.proposeGreeting(newCommand);
					}					
				}else{
						System.out.println("Atomic mode changing, cannot complete command");
				}
			}else{
				hcp.processCommand(newCommand);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
