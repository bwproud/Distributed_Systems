package a6.gipc;

import java.io.EOFException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;

public class GIPCServer {
	public static void main(String args[]) throws RemoteException, EOFException{
		GIPCRegistry registry = GIPCLocateRegistry.createRegistry(9009);
		Object serverObject = new GIPCServerObjectImpl();
		registry.rebind("clientRegistry", serverObject);
	}
}
