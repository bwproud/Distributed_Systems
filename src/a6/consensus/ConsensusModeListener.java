package a6.consensus;

import consensus.ConsensusListener;
import consensus.ProposalState;

public class ConsensusModeListener implements ConsensusListener<Integer>{
	ClientConsensusObject mechanism;
	public ConsensusModeListener(ClientConsensusObject mechanism){
		this.mechanism=mechanism;
	}

	@Override
	public void newConsensusState(Integer mode) {
		switch (mode) {
		case 0:
			mechanism.simulateNonAtomicAsynchronous();
			break;
		case 1:
			mechanism.simulateNonAtomicSynchronous();
			break;
		case 2:
			mechanism.simulateCentralizedAsynchronous();
			break;
		case 3:
			mechanism.simulateCentralizedSynchronous();
			break;
		case 4:
			mechanism.simulateSequentialPaxos();
			break;	
		default: 
			break;
		}
	}

	@Override
	public void newLocalProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newRemoteProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newProposalState(float aProposalNumber, Integer aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

}
