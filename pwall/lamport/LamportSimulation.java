package pwall.lamport;

import pwall.GlobalNetwork;
import pwall.Network;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.Simulation;
import pwall.lamport.LamportProcess;

public class LamportSimulation extends Simulation
{
    public LamportSimulation(int numberOfProcesses, int internalEventPercent, int timeBetweenMessages)
    {
        processes = new ProcessGroup();
        for (int i = 1; i <= numberOfProcesses; ++i) {
            Process process = new LamportProcess("Process " + i, internalEventPercent, timeBetweenMessages * i);
            processes.add(process);
            process.start();
        }
        for (int i = 0; i < numberOfProcesses; ++i) {
            for (int j = 0; j < numberOfProcesses; ++j) {
                if (i == j) continue;
                processes.get(i).addTarget(processes.get(j));
            }
        }
        GlobalNetwork.getInstance().addToNetwork(processes);
    }
}

