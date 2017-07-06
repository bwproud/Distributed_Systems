package a6.gipc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import StringProcessors.HalloweenCommandProcessor;
import a6.consensus.Consensus;
import port.trace.nio.LocalCommandObserved;

public class GIPCClientObjectImpl implements GIPCClientObject{
	private HalloweenCommandProcessor hcp;
	private GIPCServerObject server;
	private boolean local;
	
	public GIPCClientObjectImpl(HalloweenCommandProcessor hcp, Object server){
		this.hcp=hcp;
		this.server=(GIPCServerObject)server;
		local=false;
	}
	
	public void processCommand(String command) {
		hcp.processCommand(command);
	}

	public void handleResponse(String command){
	}
	
	public void toggleLocal(){
		local=!local;
	}
	
	public boolean getLocal(){
		return local;
	}

	public boolean setConnected(boolean connected){
		System.out.println("atomic mode set to "+ connected);
		Consensus.getAtomic().setAtomic(connected);
		return true;
	}
	
	public void setChanging(boolean changing){
		if(changing){
			System.out.println("Entering waiting mode");
		}else{
			System.out.println("Exiting waiting mode");
		}
		Consensus.getAtomic().setChanging(changing);
	}	
}
