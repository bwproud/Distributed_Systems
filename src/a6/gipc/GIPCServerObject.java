package a6.gipc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GIPCServerObject {
	int broadcast(String command, Object sender) throws InterruptedException;
	void addClient(GIPCClientObject client);
	void toggleMode(Object sender);
	void toggleConsensus();
	boolean getConsensus();
}
