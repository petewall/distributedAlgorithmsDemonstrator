package pwall.paxos;

public class ReplyPayload
{
    public ReplyPayload(int promisedNumber, Object proposal)
    {
        this.promisedNumber = promisedNumber;
        this.acceptedProposal = proposal;
    }

    public int getProposalNumber()
    {
        return promisedNumber;
    }

    public Object getAcceptedProposal()
    {
        return acceptedProposal;
    }

    public String toString()
    {
        if (acceptedProposal == null) {
            return "REP " + getProposalNumber();
        }
        return "REP " + getProposalNumber() + ": " + getAcceptedProposal();

    }

    private int promisedNumber;

    private Object acceptedProposal;
}

