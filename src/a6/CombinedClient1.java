package a6;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;

import StringProcessors.HalloweenCommandProcessor;
import a6.consensus.ClientConsensusObject;
import examples.gipc.consensus.Member1;
import main.BeauAndersonFinalProject;
import util.trace.Tracer;

public class CombinedClient1 extends CombinedClient implements Member1{
	
	static String hostName="localhost";
	static int iterations=500;
	
	public CombinedClient1(HalloweenCommandProcessor hcp, ClientConsensusObject mechanism) throws NotBoundException, UnknownHostException, IOException{
		super(hcp, mechanism);
	}
	
	public static void main(String args[]) throws NotBoundException, UnknownHostException, IOException{
		Tracer.showWarnings(false);
		if(args.length>0){
			hostName=args[0];
		}
		if(args.length>1){
			iterations=Integer.parseInt(args[1]);
		}
		
		HalloweenCommandProcessor hcp = BeauAndersonFinalProject.createSimulation(
				"SIMULATION1_PREFIX", 0, 0, 400, 765, 0, 0);
		System.out.println(hcp);
		ClientConsensusObject mechanism = new ClientConsensusObject(MY_NAME, MY_PORT_NUMBER, hcp);
		mechanism.addListenersToCommandMechanism();
		mechanism.addListenersToConsensusMechanism();
		CombinedClient client = new CombinedClient1(hcp, mechanism);
	}
}
