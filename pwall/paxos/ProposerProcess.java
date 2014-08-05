package pwall.paxos;

import pwall.Message;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.paxos.AcceptPayload;
import pwall.paxos.PreparePayload;
import pwall.paxos.ReplyPayload;

public class ProposerProcess extends Process
{
    private enum State {
        SEND_PREPARE,
        WAIT_FOR_PROMISES,
        SEND_ACCEPT,
        WAIT_FOR_REJECTIONS,
        DONE
    }

    public ProposerProcess(int index, int numberOfProposers, ProcessGroup acceptors)
    {
        super("Proposer " + index);
        this.acceptors = acceptors;
        this.state = State.SEND_PREPARE;
        this.responseCount = 0;
        this.proposalNumber = index;
        this.numberOfProposers = numberOfProposers;
        this.proposal = new String("" + index + " is best");
    }

    public void reset()
    {
        state = State.SEND_PREPARE;
        responseCount = 0;
        this.proposal = new String("" + (proposalNumber % numberOfProposers + 1) + " is best");
        notifyObservers();
    }

    public void doWork()
    {
        if (state == State.SEND_PREPARE) {
            responseCount = 0;
            proposalNumber += numberOfProposers;
            Message message = new Message(new PreparePayload(proposalNumber), this);
            getComs().send(message, acceptors.someMajority());
            state = State.WAIT_FOR_PROMISES;
            notifyObservers();
        }

        if (state == State.SEND_ACCEPT) {
            responseCount = 0;
            Message message = new Message(new AcceptPayload(proposalNumber, proposal), this);
            getComs().send(message, acceptors.someMajority());
            state = State.WAIT_FOR_REJECTIONS;
            notifyObservers();
        }
    }

    public void deliver(Message message)
    {
        if (message.getPayload().getClass().getName() == "pwall.paxos.ReplyPayload") {
            ReplyPayload response = (ReplyPayload)message.getPayload();
            if (state == State.WAIT_FOR_PROMISES) {
                if (response.getAcceptedProposal() != null) {
                    proposal = response.getAcceptedProposal();
                }

                if (response.getProposalNumber() > proposalNumber) {
                    state = State.SEND_PREPARE;
                }
                else if (response.getProposalNumber() == proposalNumber) {
                    if (++responseCount == (acceptors.size() / 2 + 1)) {
                        state = State.SEND_ACCEPT;
                    }
                }

            }

            if (state == State.WAIT_FOR_REJECTIONS) {
                if (response.getProposalNumber() > proposalNumber) {
                    state = State.SEND_PREPARE;
                    if (response.getAcceptedProposal() != null) {
                        proposal = response.getAcceptedProposal();
                    }
                } else if (response.getProposalNumber() == proposalNumber) {
                    if (++responseCount == (acceptors.size() / 2 + 1)) {
                        state = State.DONE;
                    }
                }
            }
        }
    }

    public String toString()
    {
        return getName();
    }

    public String getDetails()
    {
        return "Prop# " + proposalNumber;
    }

    public String getMoreDetails()
    {
        return "state: " + state;
    }

    private ProcessGroup acceptors;

    private State state;

    private int responseCount;

    private int proposalNumber;

    private Object proposal;

    private int numberOfProposers;
}

