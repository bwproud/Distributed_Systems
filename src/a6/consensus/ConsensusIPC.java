package a6.consensus;

import a6.consensus.Consensus.states;
public class ConsensusIPC implements ConsensusMonitor{
	private static states state;
	private static boolean changing;
	
	public ConsensusIPC(){
		state=states.GIPC;
		changing=false;
	}
	
	public synchronized void setState(states mechanism){
		state=mechanism;
	}
	
	public synchronized states getState() throws InterruptedException{
		while(changing){
			wait(); //waiting for changing to be set to false
		}
		return state;
	}
	
	public synchronized void setChanging(boolean state){
		changing=state;
		if(!state){
			notify();
		}
	}
	
	public synchronized boolean getChanging(){
		return changing;
	}
	
	
	
}
