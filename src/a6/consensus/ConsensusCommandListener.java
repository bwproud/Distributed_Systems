package a6.consensus;

import StringProcessors.HalloweenCommandProcessor;
import consensus.ConsensusListener;
import consensus.ProposalState;

public class ConsensusCommandListener implements ConsensusListener<String> {
	HalloweenCommandProcessor hcp;
	
	public ConsensusCommandListener(HalloweenCommandProcessor hcp){
		this.hcp = hcp;
	}
	
	@Override
	public void newLocalProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newRemoteProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newProposalState(float aProposalNumber, String aProposal, ProposalState aProposalState) {
		// TODO Auto-generated method stub
		
	}

	
	public void newConsensusState(String aState) {
		hcp.processCommand(aState);		
	}

}
