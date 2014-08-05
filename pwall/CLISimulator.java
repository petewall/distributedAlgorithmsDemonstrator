package pwall;

import pwall.Simulator;

public class CLISimulator
{
    public static void main(String [] args)
    {
        Simulator simulator = new Simulator();
        simulator.setupSimulationEnvironment();
        simulator.chooseSimulation();
        simulator.runSimulation();
    }
}

