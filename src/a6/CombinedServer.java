package a6;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import a6.gipc.GIPCServerObjectImpl;
import a6.nio.EchoWorker;
import a6.nio.NioServer;
import a6.rmi.RMIServerObjectImpl;
import a4.ACustomDuplexObjectInputPortFactory;
import a4.ACustomDuplexReceivedCallInvokerFactory;
import a4.ACustomSentCallCompleterFactory;
import a4.AnAsynchronousCustomDuplexReceivedCallInvokerFactory;
import inputport.datacomm.duplex.object.DuplexObjectInputPortSelector;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import inputport.rpc.duplex.DuplexReceivedCallInvokerSelector;
import inputport.rpc.duplex.DuplexSentCallCompleterSelector;
import port.trace.nio.NIOTraceUtility;

public class CombinedServer {
	public static void setFactories() {
//		DuplexReceivedCallInvokerSelector.setReceivedCallInvokerFactory(
//				new ACustomDuplexReceivedCallInvokerFactory());
		DuplexReceivedCallInvokerSelector.setReceivedCallInvokerFactory(
				new AnAsynchronousCustomDuplexReceivedCallInvokerFactory());
		DuplexSentCallCompleterSelector.setDuplexSentCallCompleterFactory(
				new ACustomSentCallCompleterFactory());
		DuplexObjectInputPortSelector.setDuplexInputPortFactory(
				new ACustomDuplexObjectInputPortFactory());
	}
	
	public CombinedServer() throws RemoteException, IOException{
		String[] placeholder=null;
		port.sessionserver.ASessionServerLauncher.main(placeholder);
		//setFactories();
		gipcStart();
		rmiStart();
		nioStart();
	}
	
	public static void main(String args[]) throws RemoteException, IOException{
		CombinedServer server = new CombinedServer();
	}
	
	public void gipcStart() throws RemoteException{
		//NIOTraceUtility.setTracing();
		GIPCRegistry registry = GIPCLocateRegistry.createRegistry(9009);
		Object serverObject = new GIPCServerObjectImpl();
		registry.rebind("clientRegistry", serverObject);
	}
	
	public void rmiStart() throws RemoteException{
		Registry registry = LocateRegistry.createRegistry(9990);
		Remote serverObject = new RMIServerObjectImpl();
		registry.rebind("clientRegistry", serverObject);
	}
	
	public void nioStart() throws IOException{
		EchoWorker worker = new EchoWorker();
		new Thread(worker).start();
		new Thread(new NioServer(null, 9900, worker)).start();
	}
}
