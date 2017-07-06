package a6.consensus;

import StringProcessors.HalloweenCommandProcessor;
import consensus.ConcurrencyKind;
import consensus.ConsensusMechanismFactory;
import consensus.ProposalState;
import consensus.ReplicationSynchrony;
import consensus.asynchronous.sequential.AnAsynchronousConsensusMechanismFactory;
import consensus.paxos.sequential.ASequentialPaxosConsensusMechanismFactory;
import examples.gipc.consensus.AGreetingConsensusListener;
import examples.gipc.consensus.paxos.APaxosMemberLauncher;
import util.misc.ThreadSupport;

public class ClientConsensusObject  extends
APaxosMemberLauncher {
	HalloweenCommandProcessor hcp;
	ConsensusCommandListener listener;
	
	public ClientConsensusObject(String aLocalName,
			int aPortNumber, HalloweenCommandProcessor hcp) {
		super(aLocalName, aPortNumber);
		this.hcp=hcp;
		listener = new ConsensusCommandListener(hcp);
	}
	
	public void addListenersToCommandMechanism() {
		greetingMechanism.addConsensusListener(listener);
	}
	
	public void addListenersToConsensusMechanism() {
		meaningOfLifeMechanism.addConsensusListener(new ConsensusModeListener(this));
	}
	
	public  ConsensusMechanismFactory<String> greetingConsensusMechanismFactory() {
		return new ASequentialPaxosConsensusMechanismFactory<>();
	}
	
	public ConsensusMechanismFactory<Integer> meaningConsensusMechanismFactory() {
		return new AnAsynchronousConsensusMechanismFactory<>();
	}
	
	public void addListenersAndVetoersToGreetingMechanism() {
		//do nothing
	}
	
	public void addListenersAndVetoersToMeaningMechanism() {
		//do nothing	
	}
	
	protected void simulateNonAtomicAsynchronous() {
		greetingMechanism.setAcceptSynchrony(ReplicationSynchrony.ASYNCHRONOUS);
		greetingMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		greetingMechanism.setSequentialAccess(false);
	}
	protected void simulateNonAtomicSynchronous() {
		greetingMechanism.setAcceptSynchrony(ReplicationSynchrony.ALL_SYNCHRONOUS);
		greetingMechanism.setConcurrencyKind(ConcurrencyKind.NON_ATOMIC);
		greetingMechanism.setSequentialAccess(false);
	}
	protected void simulateCentralized() {
		greetingMechanism.setCentralized(true);
		greetingMechanism.setSequentialAccess(true);
	}
	protected void simulateCentralizedSynchronous() {
		simulateNonAtomicSynchronous();
		simulateCentralized();
		greetingMechanism.setSequentialAccess(false);
	}
	protected void simulateCentralizedAsynchronous() {
		simulateNonAtomicAsynchronous();
		simulateCentralized();
		greetingMechanism.setSequentialAccess(false);
	}
	protected void simulateBasicPaxos() {
		overrideRetry = true;
		greetingMechanism.setCentralized(false);
		greetingMechanism.setConcurrencyKind(ConcurrencyKind.SERIALIZABLE);
		greetingMechanism
				.setPrepareSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		greetingMechanism
				.setAcceptSynchrony(ReplicationSynchrony.MAJORITY_SYNCHRONOUS);
		greetingMechanism.setSequentialAccess(true);
	}
	protected void simulateSequentialPaxos() {
		simulateBasicPaxos();
		greetingMechanism.setSequentialAccess(true);
		overrideRetry = false;
	}	
	
	public void proposeGreeting(String aGreeting) {
		float aMeaningOfLifeProposal= greetingMechanism.propose(aGreeting);
		greetingMechanism.waitForConsensus(
				aMeaningOfLifeProposal, reProposeTime());
	}
	
	public void proposeMeaning(Integer aValue) {
		System.out.println("Making proposal of:" + aValue);
		float aMeaningOfLifeProposal = meaningOfLifeMechanism
				.propose(aValue);
		ProposalState aState = meaningOfLifeMechanism.waitForConsensus(
				aMeaningOfLifeProposal, reProposeTime());
//		if (meaningOfLifeMechanism.isPending(aMeaningOfLifeProposal)) {
//			System.out.println("Waiting for pending proposal");
//			meaningOfLifeMechanism.waitForConsensus(meaningOfLifeMechanism
//					.lastProposalNumber());
//		}
	}
	
}
