package a6.gipc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import a6.consensus.Consensus;
import a6.consensus.Consensus.states;
import a6.rmi.RMIClientObject;

public class GIPCServerObjectImpl implements GIPCServerObject{
	private List<GIPCClientObject> clients;
	private boolean atomic;
	public GIPCServerObjectImpl() throws RemoteException {
		atomic=false;
		clients=new ArrayList<>();
	}
	
	public int broadcast(String command, Object sender) throws InterruptedException {
		if(Consensus.getBroadcast().getBroadcastNonBlocking()){
				if(Consensus.getBroadcast().getBroadcast()){
					Consensus.getBroadcast().setChanging(true);
				}
			}
			for(GIPCClientObject client: clients){
				if(client.hashCode()!=sender.hashCode()||Consensus.getAtomic().getAtomicNonBlocking()){
					try{
						client.processCommand(command);
					}catch(Exception e){
						removeClient(client);
					}
				}
			}
			if(Consensus.getBroadcast().getBroadcastNonBlocking()){
				Consensus.getBroadcast().setChanging(false);
			}
			return 0;
	}

	public void addClient(GIPCClientObject client) {
		clients.add(client);
		System.out.println("client added");
		System.out.println(clients.size());
	}

	public void removeClient(GIPCClientObject client) {
		clients.remove(client);
		System.out.println("client removed");
		System.out.println(clients.size());
	}
	
	public void toggleMode(Object sender){
		if(Consensus.getConsensus()){	
			for(GIPCClientObject client: clients){
				if(client.hashCode()!=sender.hashCode()){
					try{
						client.setChanging(true);
					}catch(Exception e){
						removeClient(client);
					}
				}
			}
		}
		atomic=!atomic;
		Consensus.getAtomic().setAtomic(atomic);
		for(GIPCClientObject client: clients){
				try{
					client.setConnected(atomic);
				}catch(Exception e){
					removeClient(client);
				}
		}
		
		if(Consensus.getConsensus()){
			for(GIPCClientObject client: clients){
					try{
						client.setChanging(false);
					}catch(Exception e){
						removeClient(client);
					}
			}
		}
}


public synchronized void toggleConsensus() {
	if(!Consensus.getAtomic().getChanging() && !Consensus.getIPC().getChanging() && !Consensus.getBroadcast().getChanging()){
		//System.out.println("Setting consensus mode to "+ !Consensus.getConsensus());
		Consensus.setConsensus(!Consensus.getConsensus());
		return;
	}
	//System.out.println("Global Consensus Object currently being modified, Consensus mode cannot be changed.");
}

public synchronized boolean getConsensus(){
	return Consensus.getConsensus();
}
}
