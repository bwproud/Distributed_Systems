package a6.gipc;


import java.io.EOFException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Scanner;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import StringProcessors.HalloweenCommandProcessor;
import a6.ConsoleListenerTwo;
import main.BeauAndersonFinalProject;
import util.trace.Tracer;

public class GIPCClient {
	public static void main(String args[]) throws RemoteException, EOFException{
		Tracer.showWarnings(false);
		Random rand = new Random();
		GIPCRegistry registry = GIPCLocateRegistry.getRegistry("localhost", 9009, "client"+rand.nextDouble());
		GIPCServerObject serverProxy = (GIPCServerObject) registry.lookup(GIPCServerObject.class, "clientRegistry");
		HalloweenCommandProcessor hcp = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, 0, 400, 765, 0, 0);
		GIPCClientObject clientObject = new GIPCClientObjectImpl(hcp, serverProxy);
		serverProxy.addClient(clientObject);
		ConsoleListenerTwo consoleListener = new ConsoleListenerTwo(hcp, clientObject,serverProxy);
		Thread cl = new Thread(consoleListener);
		cl.start();
	}
}
