package pwall.isis;

import pwall.GlobalNetwork;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.Simulation;
import pwall.isis.ISISProcess;

public class CBCASTSimulation extends Simulation
{
    public CBCASTSimulation(int numberOfProcesses, int timeBetweenMessages)
    {
        processes = new ProcessGroup();
        for (int i = 1; i <= numberOfProcesses; ++i) {
            Process process = new ISISProcess("Process " + i, numberOfProcesses, i - 1, timeBetweenMessages);
            processes.add(process);
            process.start();
        }
        GlobalNetwork.getInstance().addToNetwork(processes);
    }
}

