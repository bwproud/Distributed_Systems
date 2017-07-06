package a6.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import a6.ResponseHandler;
import a6.consensus.Consensus;
import a6.consensus.Consensus.states;

public interface RMIClientObject extends Remote, ResponseHandler{
	void processCommand(String command) throws RemoteException;
	boolean setConnected(boolean connected) throws RemoteException;
	boolean setState(states state) throws RemoteException;
	void setChanging(boolean changing) throws RemoteException;
	void setChangingState(boolean changing) throws RemoteException;
}
