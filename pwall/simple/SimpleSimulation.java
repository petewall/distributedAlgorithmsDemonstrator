package pwall.simple;

import pwall.GlobalNetwork;
import pwall.Network;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.Simulation;
import pwall.simple.SimpleProcess;

public class SimpleSimulation extends Simulation
{
    public SimpleSimulation(int numberOfProcesses, int timeBetweenMessages)
    {
        processes = new ProcessGroup();
        for (int i = 1; i <= numberOfProcesses; ++i) {
            Process process = new SimpleProcess("Process " + i, i * 5, timeBetweenMessages);
            processes.add(process);
            process.start();
        }
        GlobalNetwork.getInstance().addToNetwork(processes);
    }
}

