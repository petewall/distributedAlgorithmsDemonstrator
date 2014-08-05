package pwall.isis;

import pwall.GlobalNetwork;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.Simulation;
import pwall.isis.ISISProcess;

public class ABCASTSimulation extends Simulation
{
    public ABCASTSimulation(int numberOfProcesses, int timeBetweenMessages, int percentABCAST)
    {
        processes = new ProcessGroup();
        Process process = new ISISProcess("Token Holder", numberOfProcesses, 0, timeBetweenMessages / 4, 0, true);
        processes.add(process);
        process.start();

        for (int i = 1; i < numberOfProcesses; ++i) {
            process = new ISISProcess("Process " + i, numberOfProcesses, i, timeBetweenMessages, percentABCAST, false);
            processes.add(process);
            process.start();
        }
        GlobalNetwork.getInstance().addToNetwork(processes);
    }
}

