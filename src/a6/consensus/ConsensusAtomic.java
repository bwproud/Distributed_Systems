package a6.consensus;

public class ConsensusAtomic implements ConsensusMonitor{
	
	private boolean changing;
	private boolean atomic;
	
	public ConsensusAtomic(){
		atomic=false;
		changing=false;
	}
	public synchronized void setAtomic(boolean mode){
		atomic=mode;
	}
	
	public synchronized boolean getAtomicNonBlocking(){
		return atomic;
	}
	
	public synchronized boolean getAtomic() throws InterruptedException{
		while(changing){
			wait(); //waiting for changing to be set to false
		}
		return atomic;
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
