package a6;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;

import StringProcessors.HalloweenCommandProcessor;
import a6.consensus.Consensus;
import a6.consensus.Consensus.states;
import a6.gipc.GIPCClientObject;
import a6.gipc.GIPCClientObjectImpl;
import a6.gipc.GIPCServerObject;
import a6.rmi.RMIClientObject;
import a6.rmi.RMIClientObjectImpl;
import a6.rmi.RMIServerObject;

public class ConsoleListenerTwo implements Runnable{
	private HalloweenCommandProcessor hcp;
	private GIPCServerObject serverProxy;
	private GIPCClientObjectImpl client;
	private boolean sync;
	
	public ConsoleListenerTwo(HalloweenCommandProcessor hcp, GIPCClientObject client,GIPCServerObject server){
		serverProxy= server;
		this.client=(GIPCClientObjectImpl) client;
		this.hcp=hcp;
		sync=true;
	}
	
	public void timeTest1() throws RemoteException, InterruptedException{
		if(!client.getLocal()){
			  client.toggleLocal();
		  }if(Consensus.getAtomic().getAtomic()){
			  serverProxy.toggleMode(client);
		  }
		  long start = System.currentTimeMillis();
		  for(int i = 0; i <500; i++){
			  hcp.setInputString("move 2 0");
		  }
		  long execution = (System.currentTimeMillis()-start)/1000;
		  hcp.setInputString("move -1000 0");
		  System.out.println(Consensus.getIPC().getState().toString()+" - Time Test 1(Local mode): Execution took "+ execution+ " seconds");
	}
	
	public void timeTest2() throws RemoteException, InterruptedException{
		if(client.getLocal()){
			  client.toggleLocal();
		  }
		  if(Consensus.getAtomic().getAtomic()){
			  serverProxy.toggleMode(client);
		  }
		  long start = System.currentTimeMillis();
		  for(int i = 0; i <500; i++){
			  hcp.setInputString("move 2 0");
		  }
		  long execution = (System.currentTimeMillis()-start)/1000;
		  hcp.setInputString("move -1000 0");
		  System.out.println(Consensus.getIPC().getState().toString()+" - Time Test 2(Nonatomic mode): Execution took "+ execution+ " seconds");
	}
	
	public void timeTest3() throws RemoteException, InterruptedException{
		if(client.getLocal()){
			  client.toggleLocal();
		  }
		  if(!Consensus.getAtomic().getAtomic()){
			  serverProxy.toggleMode(client);
		  }
		  long start = System.currentTimeMillis();
		  for(int i = 0; i <500; i++){
			  hcp.setInputString("move 2 0");
		  }
		  long execution = (System.currentTimeMillis()-start)/1000;
		  hcp.setInputString("move -1000 0");
		  System.out.println(Consensus.getIPC().getState().toString()+" - Time Test 3(atomic mode): Execution took "+ execution+ " seconds");
	}
	
	public void toggleAtomic() throws RemoteException, InterruptedException{
		 if(sync){
	   		  if(!Consensus.getAtomic().getChanging()){
	   			  if(serverProxy.getConsensus()){
	   				  client.setChanging(true);
	   			  }
	   			  //Consensus.getAtomic().setAtomic(!Consensus.getAtomic().getAtomicNonBlocking());
	   			  serverProxy.toggleMode(client);
	   		  }
	   		  else{
	   			  System.out.println("Broadcast mode already being changed; Change request ignored");
	   		  }
		  }else{
			  System.out.println("Setting atomic mode to " + !Consensus.getAtomic().getAtomic());
			  Consensus.getAtomic().setAtomic(!Consensus.getAtomic().getAtomic());
		  }
	}
	public void run() {
		try{
			//executionTest();
			while(true){
		    	  Scanner s = new Scanner(System.in);
		    	  String command = s.nextLine();
		    	  command = command.toLowerCase();
		    	  if(command.equals("atomic")){
		    		 toggleAtomic();
		    	  }else if(command.equals("consensus")){
			    		 serverProxy.toggleConsensus();
		    	  }else if(command.equals("sync")){
		    		  System.out.println("Changing sync mode to " + !sync);
		    		  sync=!sync;
		    	  }else if(command.equals("local")){
		    		  client.toggleLocal();
		    	  }else if(command.equals("timetest1")){
		    		  timeTest1();
		    	  }else if(command.equals("timetest2")){
		    		  timeTest2();
		    	  }else if(command.equals("timetest3")){
		    		  timeTest3();
		    	  }else if(command.equals("mode")){
		    		  System.out.println("Atomic mode: "+ Consensus.getAtomic().getAtomic()+ " Local mode: "+ client.getLocal()+" Consensus: "+ serverProxy.getConsensus());
		    	  }else{
		    		  hcp.setInputString(command);
		    	  }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
