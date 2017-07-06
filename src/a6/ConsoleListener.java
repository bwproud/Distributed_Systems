package a6;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;

import StringProcessors.HalloweenCommandProcessor;
import a6.consensus.ClientConsensusObject;
import a6.consensus.Consensus;
import a6.consensus.Consensus.states;
import a6.rmi.RMIClientObject;
import a6.rmi.RMIClientObjectImpl;
import a6.rmi.RMIServerObject;

public class ConsoleListener implements Runnable {
	private HalloweenCommandProcessor hcp;
	private RMIServerObject serverProxy;
	private RMIClientObjectImpl client;
	private boolean sync;
	private int iterations;
	private ClientConsensusObject mechanism;
	
	public ConsoleListener(HalloweenCommandProcessor hcp, RMIClientObject client, RMIServerObject server,
			int anIterations, ClientConsensusObject mechanism) {
		serverProxy = server;
		this.client = (RMIClientObjectImpl) client;
		this.hcp = hcp;
		sync = true;
		iterations = anIterations;
		this.mechanism=mechanism;
	}

	public void timeTest1() throws RemoteException, InterruptedException {
		if (!client.getLocal()) {
			client.toggleLocal();
		}
		if (Consensus.getAtomic().getAtomic()) {
			serverProxy.toggleMode(client);
		}
		long start = System.currentTimeMillis();
		for (int i = 0; i < iterations / 2; i++) {
			hcp.setInputString("move 2 0");
			hcp.setInputString("move -2 0");
		}
		double execution = (System.currentTimeMillis() - start) / 1000.0;
		// hcp.setInputString("move -1000 0");
		System.out.println(Consensus.getIPC().getState().toString() + " - Time Test 1(Local mode): Execution took "
				+ execution + " seconds");
	}

	public void timeTest2() throws RemoteException, InterruptedException {
		if (client.getLocal()) {
			client.toggleLocal();
		}
		if (Consensus.getAtomic().getAtomic()) {
			serverProxy.toggleMode(client);
		}
		long start = System.currentTimeMillis();
		for (int i = 0; i < iterations / 2; i++) {
			hcp.setInputString("move 2 0");
			hcp.setInputString("move -2 0");
		}
		double execution = (System.currentTimeMillis() - start) / 1000.0;
		// hcp.setInputString("move -1000 0");
		System.out.println(Consensus.getIPC().getState().toString() + " - Time Test 2(Nonatomic mode): Execution took "
				+ execution + " seconds");
	}

	public void timeTest3() throws RemoteException, InterruptedException {
		if (client.getLocal()) {
			client.toggleLocal();
		}
		if (!Consensus.getAtomic().getAtomic()) {
			serverProxy.toggleMode(client);
		}
		long start = System.currentTimeMillis();
		for (int i = 0; i < iterations / 2; i++) {
			hcp.setInputString("move 2 0");
			hcp.setInputString("move -2 0");
		}
		double execution = (System.currentTimeMillis() - start) / 1000.0;
		// hcp.setInputString("move -1000 0");
		System.out.println(Consensus.getIPC().getState().toString() + " - Time Test 3(atomic mode): Execution took "
				+ execution + " seconds");
	}
	
	public void timeTest() throws RemoteException, InterruptedException {
		long start = System.currentTimeMillis();
		for (int i = 0; i < iterations/2; i++) {
			hcp.setInputString("move 2 0");
			hcp.setInputString("move -2 0");
		}
		double execution = (System.currentTimeMillis() - start) / 1000.0;
		// hcp.setInputString("move -1000 0");
		System.out.println(Consensus.getIPC().getState().toString() + " - Time Test: Execution took "
				+ execution + " seconds");
	}
	
	public void twoCommand() throws RemoteException, InterruptedException {
		long start = System.currentTimeMillis();
		for (int i = 0; i <1; i++) {
			hcp.setInputString("move 2 0");
			hcp.setInputString("move -2 0");
		}
		double execution = (System.currentTimeMillis() - start) / 1000.0;
		// hcp.setInputString("move -1000 0");
		System.out.println(Consensus.getIPC().getState().toString() + " - Time Test: Execution took "
				+ execution + " seconds");
	}

	public void setState(states state) throws RemoteException {
		if (sync) {
			if (!Consensus.getIPC().getChanging()) {
				if (serverProxy.getConsensus()) {
					client.setChangingState(true);
				}
				client.setState(state);
				serverProxy.changeIPC(state, client);
			}
		} else {
			System.out.println("Setting IPC mechanism to " + state.toString());
			Consensus.getIPC().setState(state);
		}
	}

	public void toggleAtomic() throws RemoteException, InterruptedException {
		if (sync) {
			if (!Consensus.getAtomic().getChanging()) {
				if (serverProxy.getConsensus()) {
					client.setChanging(true);
				}
				// Consensus.getAtomic().setAtomic(!Consensus.getAtomic().getAtomicNonBlocking());
				serverProxy.toggleMode(client);
			}
		} else {
			System.out.println("Setting atomic mode to " + !Consensus.getAtomic().getAtomic());
			Consensus.getAtomic().setAtomic(!Consensus.getAtomic().getAtomic());
		}
	}

	public void executionTest() throws RemoteException, InterruptedException {
		for (int i = 3; i < 8; i++) {
			switch (i) {
			case 0:
				setState(Consensus.states.NIO);
				break;
			case 1:
				setState(Consensus.states.RMI);
				break;
			case 2:
				setState(Consensus.states.GIPC);
				break;
			case 3:
				setState(Consensus.states.NONATOMICASYNC);
				setSimulationMethod(Consensus.states.NONATOMICASYNC);
				break;
			case 4:
				setState(Consensus.states.NONATOMICSYNC);
				setSimulationMethod(Consensus.states.NONATOMICSYNC);
				break;
			case 5:
				setState(Consensus.states.CENTRALIZEDASYNC);
				setSimulationMethod(Consensus.states.CENTRALIZEDASYNC);
				break;
			case 6:
				setState(Consensus.states.CENTRALIZEDSYNC);
				setSimulationMethod(Consensus.states.CENTRALIZEDSYNC);
				break;
			case 7:
				setState(Consensus.states.SEQUENTIALPAXOS);
				setSimulationMethod(Consensus.states.SEQUENTIALPAXOS);
				break;
			}
			timeTest();
			Thread.sleep(5000);
		}
	}
	
	public void setSimulationMethod(states state){
		switch (state) {
		case NONATOMICASYNC:
			mechanism.proposeMeaning(0);
			break;
		case NONATOMICSYNC:
			mechanism.proposeMeaning(1);
			break;
		case CENTRALIZEDASYNC:
			mechanism.proposeMeaning(2);
			break;
		case CENTRALIZEDSYNC:
			mechanism.proposeMeaning(3);
			break;
		case SEQUENTIALPAXOS:
			mechanism.proposeMeaning(4);
			break;	
		default: 
			break;
		}
	}

	public void run() {
		try {
			while (true) {
				Scanner s = new Scanner(System.in);
				String command = s.nextLine();
				command = command.toLowerCase();
				if (command.equals("test")) {
					executionTest();
				} else if (command.equals("two command")) {
					twoCommand();
				}else if (command.equals("atomic")) {
					toggleAtomic();
				} else if (command.equals("consensus")) {
					serverProxy.toggleConsensus();
				} else if (command.equals("rmi")) {
					setState(Consensus.states.RMI);
				} else if (command.equals("gipc")) {
					setState(Consensus.states.GIPC);
				} else if (command.equals("nio")) {
					setState(Consensus.states.NIO);
				}else if (command.equals("nonatomic async")) {
					setState(Consensus.states.NONATOMICASYNC);
					setSimulationMethod(Consensus.states.NONATOMICASYNC);
				}else if (command.equals("nonatomic sync")) {
					setState(Consensus.states.NONATOMICSYNC);
					setSimulationMethod(Consensus.states.NONATOMICSYNC);
				}else if (command.equals("centralized async")) {
					setState(Consensus.states.CENTRALIZEDASYNC);
					setSimulationMethod(Consensus.states.CENTRALIZEDASYNC);
				}else if (command.equals("centralized sync")) {
					setState(Consensus.states.CENTRALIZEDSYNC);
					setSimulationMethod(Consensus.states.CENTRALIZEDSYNC);
				} else if (command.equals("paxos")) {
					setState(Consensus.states.SEQUENTIALPAXOS);
					setSimulationMethod(Consensus.states.SEQUENTIALPAXOS);
				}else if (command.equals("sync")) {
					System.out.println("Changing sync mode to " + !sync);
					sync = !sync;
				} else if (command.equals("broadcast")) {
					serverProxy.toggleBroadcast();
				} else if (command.equals("local")) {
					client.toggleLocal();
				} else if (command.equals("timetest1")) {
					timeTest1();
				} else if (command.equals("timetest2")) {
					timeTest2();
				} else if (command.equals("timetest3")) {
					timeTest3();
				} else if (command.equals("mode")) {
					System.out.println("Atomic mode: " + Consensus.getAtomic().getAtomic() + " Local mode: "
							+ client.getLocal() + " IPC mode: " + Consensus.getIPC().getState() + " Consensus: "
							+ serverProxy.getConsensus() + " Synchronized: " + sync + " Broadcast: "
							+ serverProxy.getBroadcast());
				} else {
					hcp.setInputString(command);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
