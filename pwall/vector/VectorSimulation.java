package pwall.vector;

import pwall.GlobalNetwork;
import pwall.Process;
import pwall.ProcessGroup;
import pwall.Simulation;
import pwall.vector.VectorProcess;

public class VectorSimulation extends Simulation
{
    public VectorSimulation(int numberOfProcesses, int internalEventPercent, int timeBetweenMessages)
    {
        processes = new ProcessGroup();
        for (int i = 1; i <= numberOfProcesses; ++i) {
            Process process = new VectorProcess("Process " + i, numberOfProcesses, i - 1, internalEventPercent, timeBetweenMessages);
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

