package a6.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import a6.consensus.Consensus;
import a6.consensus.Consensus.states;

public interface RMIServerObject extends Remote {
	void broadcast(String command, Remote sender) throws RemoteException, InterruptedException;
	void addClient(RMIClientObject client) throws RemoteException;
	void toggleMode(Remote sender) throws RemoteException;
	void changeIPC(states state, Remote sender) throws RemoteException;
	void toggleConsensus() throws RemoteException;
	boolean getConsensus() throws RemoteException;
	void toggleBroadcast() throws RemoteException, InterruptedException;
	boolean getBroadcast() throws RemoteException;
}
