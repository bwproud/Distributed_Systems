package a6.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import a6.consensus.Consensus;
import a6.consensus.Consensus.states;

public class RMIServerObjectImpl extends UnicastRemoteObject implements RMIServerObject{
	private List<RMIClientObject> clients;
	private boolean atomic;
	
	public RMIServerObjectImpl() throws RemoteException {
		super();
		clients=new ArrayList<>();
		atomic=false;
	}
	
	public void broadcast(String command, Remote sender) throws RemoteException, InterruptedException {
		if(Consensus.getBroadcast().getBroadcastNonBlocking()){
			if(Consensus.getBroadcast().getBroadcast()){
				Consensus.getBroadcast().setChanging(true);
			}
		}	
		for(RMIClientObject client: clients){
			if(client.hashCode()!=sender.hashCode()||atomic){
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
	}

	@Override
	public void addClient(RMIClientObject client) {
		clients.add(client);
		System.out.println("client added");
		System.out.println(clients.size());
	}

	public void removeClient(RMIClientObject client) {
		clients.remove(client);
		System.out.println("client removed");
		System.out.println(clients.size());
	}

	public void toggleMode(Remote sender){
			if(Consensus.getConsensus()){	
				for(RMIClientObject client: clients){
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
			for(RMIClientObject client: clients){
					try{
						client.setConnected(atomic);
					}catch(Exception e){
						removeClient(client);
					}
			}
			
			Consensus.getAtomic().setAtomic(atomic);
			if(Consensus.getConsensus()){
				for(RMIClientObject client: clients){
						try{
							client.setChanging(false);
						}catch(Exception e){
							removeClient(client);
						}
				}
			}
	}
	
	public synchronized void changeIPC(states state, Remote sender){
		if(Consensus.getConsensus()){
			for(RMIClientObject client: clients){
				if(client.hashCode()!=sender.hashCode()){
					try{
						client.setChangingState(true);
					}catch(Exception e){
						removeClient(client);
					}
				}
			}
		}
		for(RMIClientObject client: clients){
			if(client.hashCode()!=sender.hashCode()){
				try{
					client.setState(state);
				}catch(Exception e){
					removeClient(client);
				}
			}
		}
		if(Consensus.getConsensus()){
			for(RMIClientObject client: clients){
					try{
						client.setChangingState(false);
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

	public synchronized void toggleBroadcast() throws InterruptedException{
		boolean broadcast = Consensus.getBroadcast().getBroadcast();
		//System.out.println("Setting broadcast mode to " + !broadcast);
		Consensus.getBroadcast().setBroadcast(!broadcast);
	}
	
	public synchronized boolean getBroadcast(){
		return Consensus.getBroadcast().getBroadcastNonBlocking();
	}
}
