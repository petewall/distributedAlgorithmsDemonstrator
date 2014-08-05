package pwall.paxos;

import pwall.GlobalNetwork;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.Simulation;
import pwall.paxos.AcceptorProcess;
import pwall.paxos.LearnerProcess;
import pwall.paxos.ProposerProcess;

public class PaxosSimulation extends Simulation
{
    public PaxosSimulation(int numberOfProcesses)
    {   
        processes = new ProcessGroup();

        int proposerCount = 0;
        int acceptorCount = 0;
        int learnerCount = 0;
        for (int i = 0; i < numberOfProcesses; ++i) {
            if (i % 3 == 0) { proposerCount++; }
            if (i % 3 == 1) { acceptorCount++; }
            if (i % 3 == 2) { learnerCount++;  }
        }

        /**
         * If true, the acceptors will notify the proposer when they reject an accept
         * message because the acceptor promised not to accept the proposal of that
         * number.  This allows the proposer a second chance to send a new set of
         * prepare and accept messages.
         * Observationally, this leads to many more rounds and could lead to deadlock
         * in leaderless Paxos, but this also leads to the acceptors almost alwasys
         * agreeing on the final chosen value.
         */
        boolean sendRejectionsToAccepts = false;

        ProcessGroup learners = createLearners(learnerCount, acceptorCount);
        ProcessGroup acceptors = createAcceptors(acceptorCount, sendRejectionsToAccepts, learners);
        ProcessGroup proposers = createProposers(proposerCount, acceptors);

        GlobalNetwork.getInstance().addToNetwork(processes);
    }

    private ProcessGroup createLearners(int count, int numberOfAcceptors)
    {
        ProcessGroup learners = new ProcessGroup();
        for (int i = 1; i <= count; ++i) {
            Process learner = new LearnerProcess(i, numberOfAcceptors);
            learner.start();
            learners.add(learner);
            processes.add(learner);
        }
        return learners;
    }

    private ProcessGroup createAcceptors(int count, boolean sendRejectionsToAccepts, ProcessGroup learners)
    {
        ProcessGroup acceptors = new ProcessGroup();
        for (int i = 1; i <= count; ++i) {
            Process acceptor = new AcceptorProcess(i, sendRejectionsToAccepts, learners);
            acceptor.start();
            acceptors.add(acceptor);
            processes.add(acceptor);
        }
        return acceptors;
    }

    private ProcessGroup createProposers(int count, ProcessGroup acceptors)
    {
        ProcessGroup proposers = new ProcessGroup();
        for (int i = 1; i <= count; ++i) {
            Process proposer = new ProposerProcess(i, count, acceptors);
            proposer.start();
            proposers.add(proposer);
            processes.add(proposer);
        }
        return proposers;
    }
}

