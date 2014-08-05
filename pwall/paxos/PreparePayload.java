package pwall.paxos;

public class PreparePayload
{
    public PreparePayload(int number)
    {
        this.proposalNumber = number;
    }

    public int getProposalNumber()
    {
        return proposalNumber;
    }

    public String toString()
    {
        return "PRE " + proposalNumber;
    }

    private int proposalNumber;
}

