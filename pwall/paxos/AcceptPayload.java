package pwall.paxos;

public class AcceptPayload
{
    public AcceptPayload(int proposalNumber, Object proposal)
    {
        this.proposalNumber = proposalNumber;
        this.proposal = proposal;
    }

    public int getProposalNumber()
    {
        return proposalNumber;
    }

    public Object getProposal()
    {
        return proposal;
    }

    public String toString()
    {
        return "ACC " + getProposalNumber() + ": " + getProposal();
    }

    private int proposalNumber;

    private Object proposal;
}

