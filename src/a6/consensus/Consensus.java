package a6.consensus;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Consensus{
	public static enum states{NIO, RMI, GIPC, CONSENSUS_MECHANISM, NONATOMICASYNC, NONATOMICSYNC, CENTRALIZEDASYNC, CENTRALIZEDSYNC, SEQUENTIALPAXOS}
	private static ConsensusAtomic atomicMonitor=new ConsensusAtomic();
	private static ConsensusIPC ipcMonitor=new ConsensusIPC();
	private static ConsensusBroadcast broadcastMonitor=new ConsensusBroadcast();
	private static boolean consensus=true;
	private static boolean local = false;
	
	public static ConsensusAtomic getAtomic(){
		return atomicMonitor;
	}
	
	public static ConsensusIPC getIPC(){
		return ipcMonitor;
	}
	
	public static ConsensusBroadcast getBroadcast(){
		return broadcastMonitor;
	}
	
	public static boolean getConsensus(){
		return consensus;
	}
	
	public static void setConsensus(boolean connected){
		consensus=connected;
	}
	public static boolean getLocal(){
		return local;
	}
	
	public static void setLocal(boolean connected){
		local=connected;
	}
	
}
