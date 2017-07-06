package a6.gipc;

import java.rmi.Remote;
import java.rmi.RemoteException;

import a6.ResponseHandler;

public interface GIPCClientObject extends ResponseHandler{
	void processCommand(String command);
	boolean setConnected(boolean connected);
	void setChanging(boolean changing);
}
