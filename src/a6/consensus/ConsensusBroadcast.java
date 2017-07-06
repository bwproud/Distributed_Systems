package a6.consensus;

public class ConsensusBroadcast implements ConsensusMonitor{
	private boolean changing;
	private boolean broadcast;
	
	public ConsensusBroadcast(){
		broadcast=false;
		changing=false;
	}
	public synchronized void setBroadcast(boolean mode){
		broadcast=mode;
	}
	
	public synchronized boolean getBroadcastNonBlocking(){
		return broadcast;
	}
	
	public synchronized boolean getBroadcast() throws InterruptedException{
		while(changing){
			wait(); //waiting for changing to be set to false
		}
		return broadcast;
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
