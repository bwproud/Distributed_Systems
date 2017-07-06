package a6.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
	public static void main(String args[]) throws RemoteException{
		Registry registry = LocateRegistry.createRegistry(9090);
		Remote serverObject = new RMIServerObjectImpl();
		registry.rebind("clientRegistry", serverObject);
	}
}
