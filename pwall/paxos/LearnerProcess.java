package pwall.paxos;

import pwall.Message;
import pwall.Process;

import java.util.Collection;
import java.util.HashMap;

public class LearnerProcess extends Process
{
    public LearnerProcess(int index, int numberOfAcceptors)
    {
        super("Learner " + index);
        this.numberOfAcceptors = numberOfAcceptors;
        this.chosenProposal = null;
        this.acceptedProposals = new HashMap<Process, Object>();
    }

    public void reset()
    {
        this.chosenProposal = null;
        this.acceptedProposals.clear();
        notifyObservers();
    }

    public void doWork()
    {

    }

    public void deliver(Message message)
    {
        if (message.getPayload().getClass().getName() == "pwall.paxos.AcceptPayload") {
            Object acceptedProposal = ((AcceptPayload)message.getPayload()).getProposal();
            if (chosenProposal != null && !chosenProposal.equals(acceptedProposal)) {
                logger.log("Differing value (" + acceptedProposal + ") given when a value has already been chosen (" + chosenProposal + ")");
            }

            acceptedProposals.put(message.getSender(), acceptedProposal);
            if (chosenProposal == null && acceptedProposals.size() >= numberOfAcceptors / 2 + 1) {
                Collection<Object> acceptedValues = acceptedProposals.values();
                int commonProposalCount = 0;

                for (Object proposal : acceptedValues) {
                    if (proposal.equals(acceptedProposal))
                        commonProposalCount++;
                }
                if (commonProposalCount >= numberOfAcceptors / 2 + 1) {
                    chosenProposal = acceptedProposal;
                    logger.log("CHOSEN! " + chosenProposal);
                }
            }   
        }   
        notifyObservers();
    }

    public String toString()
    {
        return getName();
    }

    public String getDetails()
    {
        if (chosenProposal == null)
            return "";
        return "CHOSEN: " + chosenProposal;
    }

    public String getMoreDetails()
    {
        return "";
    }

    private int numberOfAcceptors;
    private Object chosenProposal;
    private HashMap<Process, Object> acceptedProposals;
}

