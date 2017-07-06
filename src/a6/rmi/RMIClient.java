
package a6.rmi;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import StringProcessors.HalloweenCommandProcessor;
import a6.ConsoleListener;
import main.BeauAndersonFinalProject;
import util.trace.Tracer;

public class RMIClient {
	public static void main(String args[]) throws RemoteException, NotBoundException{
		Tracer.showWarnings(false);
		Registry registry = LocateRegistry.getRegistry(9090);
		RMIServerObject serverProxy = (RMIServerObject) registry.lookup("clientRegistry");
		HalloweenCommandProcessor hcp = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, 0, 400, 765, 0, 0);
		RMIClientObject clientObject = new RMIClientObjectImpl(hcp, serverProxy);
		serverProxy.addClient(clientObject);
		//ConsoleListener consoleListener = new ConsoleListener(hcp, clientObject,serverProxy, 100);
		//Thread cl = new Thread(consoleListener);
		//cl.start();
	}
}
