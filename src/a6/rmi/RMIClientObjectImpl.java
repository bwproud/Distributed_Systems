package a6.rmi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import StringProcessors.HalloweenCommandProcessor;
import a6.consensus.Consensus;
import a6.consensus.Consensus.states;
import port.trace.nio.LocalCommandObserved;

public class RMIClientObjectImpl extends UnicastRemoteObject implements RMIClientObject{
	private HalloweenCommandProcessor hcp;
	private RMIServerObject server;
	private boolean local;
	
	public RMIClientObjectImpl(HalloweenCommandProcessor hcp, Remote server) throws RemoteException{
		super();
		this.hcp=hcp;
		this.server=(RMIServerObject)server;
		local=false;
	}
	
	public void processCommand(String command) {
		handleResponse(command);
	}
	
	public void handleResponse(String command) {
		hcp.processCommand(command);
	}
	
	public void toggleLocal(){
		local=!local;
		//System.out.println("Setting local mode to "+ local);
		Consensus.setLocal(local);
	}
	
	public boolean getLocal(){
		return Consensus.getLocal();
	}

	public boolean setConnected(boolean connected){
		//System.out.println("atomic mode set to "+ connected);
		Consensus.getAtomic().setAtomic(connected);
		return true;
	}
	
	public boolean setState(states state){
		//System.out.println("IPC mode set to "+ state.toString());
		Consensus.getIPC().setState(state);
		return true;
	}
	
	public void setChanging(boolean changing){
		if(changing){
			//System.out.println("Entering waiting mode for atomicity change");
		}else{
			//System.out.println("Exiting waiting mode for atomicity change");
		}
		Consensus.getAtomic().setChanging(changing);
	}
	
	public void setChangingState(boolean changing){
		if(changing){
			//System.out.println("Entering waiting mode for IPC mechanism change");
		}else{
			//System.out.println("Exiting waiting mode for IPC mechanism change");
		}
		Consensus.getIPC().setChanging(changing);
	}
}
