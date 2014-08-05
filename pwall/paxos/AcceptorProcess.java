package pwall.paxos;

import pwall.Message;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.paxos.AcceptPayload;
import pwall.paxos.PreparePayload;
import pwall.paxos.ReplyPayload;

public class AcceptorProcess extends Process
{
    public AcceptorProcess(int index, boolean sendRejectionsToAccepts, ProcessGroup learners)
    {
        super("Acceptor " + index);
        this.learners = learners;
        this.promisedProposalNumber = 0;
        this.acceptedProposal = null;
        this.sendRejectionsToAccepts = sendRejectionsToAccepts;
    }

    public void reset()
    {
        promisedProposalNumber = 0;
        acceptedProposal = null;
        notifyObservers();
    }

    public void doWork()
    {

    }

    public void deliver(Message message)
    {
        logger.log("received payload: " + message.getPayload().getClass().getName());

        if (message.getPayload().getClass().getName() == "pwall.paxos.PreparePayload") {
            int proposalNumber = ((PreparePayload)message.getPayload()).getProposalNumber();
            if (proposalNumber > promisedProposalNumber) {
                promisedProposalNumber = proposalNumber;
                logger.log("New promised number: " + promisedProposalNumber);
            }
            Message response = new Message(new ReplyPayload(promisedProposalNumber, acceptedProposal), this, message.getSender());
            getComs().send(response, message.getSender());
        }
        else if (message.getPayload().getClass().getName() == "pwall.paxos.AcceptPayload") {
            int proposalNumber = ((AcceptPayload)message.getPayload()).getProposalNumber();
            if (proposalNumber < promisedProposalNumber) {
                if (sendRejectionsToAccepts) {
                    Message response = new Message(new ReplyPayload(promisedProposalNumber, acceptedProposal), this, message.getSender());
                    getComs().send(response, message.getSender());
                }
            } else {
                acceptedProposal = ((AcceptPayload)message.getPayload()).getProposal();
                Message response = new Message(message.getPayload(), this);
                getComs().send(response, learners);
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
        return "Promised: " + promisedProposalNumber;
    }

    public String getMoreDetails()
    {
        return "Accepted: " + (acceptedProposal == null ? "N/A" : acceptedProposal.toString());
    }

    private ProcessGroup learners;
    private int promisedProposalNumber;
    private Object acceptedProposal;
    private boolean sendRejectionsToAccepts;
}

