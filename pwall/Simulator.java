package pwall;

import pwall.GlobalNetwork;
import pwall.Network;
import pwall.Simulation;

import pwall.gui.GUISimulator;

import pwall.isis.ABCASTSimulation;
import pwall.isis.CBCASTSimulation;
import pwall.lamport.LamportSimulation;
import pwall.paxos.PaxosSimulation;
import pwall.simple.SimpleSimulation;
import pwall.vector.VectorSimulation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Simulator
{
    public Simulator()
    {
        chosenSimulation = null;
    }

    public void chooseSimulation()
    {
        setupSimulationParameters();
        System.out.println("Choose a simulation to run:");
        System.out.println("    1. Simple simulation");
        System.out.println("    2. Lamport clock simulation");
        System.out.println("    3. Vector clock simulation");
        System.out.println("    4. ISIS CBCAST simulation");
        System.out.println("    5. ISIS ABCAST simulation");
        System.out.println("    6. Paxos simulation");

        while (chosenSimulation == null) {
            int selection = askForAnInt(1, 6);
            switch (selection)
            {
                case 1:
                    chosenSimulation = new SimpleSimulation(processCount, timeBetweenMessages);
                    break;
                case 2:
                    chosenSimulation = new LamportSimulation(processCount, setupInternalEventParameter(), timeBetweenMessages);
                    break;
                case 3:
                    chosenSimulation = new VectorSimulation(processCount, setupInternalEventParameter(), timeBetweenMessages);
                    break;
                case 4:
                    chosenSimulation = new CBCASTSimulation(processCount, timeBetweenMessages);
                    break;
                case 5:
                    chosenSimulation = new ABCASTSimulation(processCount, timeBetweenMessages, setupABCASTPercentageParameter());
                    break;
                case 6:
                    chosenSimulation = new PaxosSimulation(processCount);
                    break;
                default:
                    System.err.println("Please select a simulation from the list above.");
                    break;
            }
        }
    }

    /**
     * Asks additional questions to configure the environment.  Specifically, it
     * asks what percentage of the messages sent will fail (lost), what is the delay
     * (in ms) for messages, and are atomic broadcasts supported?  This configures
     * the GlobalNetwork's static instance.
     */
    public void setupSimulationEnvironment()
    {
        System.out.println("Setup simulation environment:");
        System.out.print("Percentage of message failure (0%): ");
        int faultPercentage = askForAnInt(0, 100, 0);

        System.out.print("Lower bound of message delay (500ms): ");
        int lowerBound = askForAnInt(0, 60000, 500);

        System.out.print("Upper bound of message delay (5000ms): ");
        int upperBound = askForAnInt(lowerBound, 60000, 5000);

        System.out.print("Are atomic broadcasts supported? (1) ");
        boolean atomicBroadcast = (1 == askForAnInt(0, 1, 1));

        GlobalNetwork.setNetwork(new Network(faultPercentage, lowerBound, upperBound, atomicBroadcast));
    }

    public void setupSimulationParameters()
    {
        System.out.print("How many processes (5): ");
        processCount = askForAnInt(0, 50, 5);

        System.out.print("How long between events (3000ms): ");
        timeBetweenMessages = askForAnInt(0, 60000, 3000);
    }

    public int setupInternalEventParameter()
    {
        System.out.print("Percent internal events (80%): ");
        return askForAnInt(0, 100, 80);
    }

    public int setupABCASTPercentageParameter()
    {
        System.out.print("Percent ABCAST events (80%): ");
        return askForAnInt(0, 100, 80);
    }

    public void runSimulation()
    {
        try {
            InputStreamReader in = new InputStreamReader(System.in);

            char selection;
            System.out.println(simulationControls());
            while ((selection = (char)in.read()) != 'q') {
                if (selection == 's') {
                    if (chosenSimulation.getState()) {
                        chosenSimulation.pause();
                        GlobalNetwork.getInstance().pauseWork();
                    } else {
                        GlobalNetwork.getInstance().startWork();
                        chosenSimulation.start();
                    }
                }
                else if (selection == 'r') {
                    chosenSimulation.reset();
                }
            }
            GlobalNetwork.getInstance().stopWork();
            chosenSimulation.stop();
        } catch (Exception e) {
            System.err.println("Caught an exception. " + e.getMessage());
            return;
        }
    }

    public String simulationControls()
    {
        return " (s) Start/stop\n" +
               " (r) Reset\n" +
               " (q) Quit";
    }

    public int askForAnInt(int min, int max)
    {
        return askForAnInt(min, max, max+1);
    }

    public int askForAnInt(int min, int max, int defaultValue)
    {
        int selection = 0;
        while (true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String value = reader.readLine();
                if (value.length() == 0)
                    return defaultValue;
                selection = Integer.parseInt(value);
            }
            catch (IOException e) {
                System.err.println("Unable to parse your response");
                selection = 0;
                continue;
            }
            catch (NumberFormatException e) {
                System.err.println("Please enter a number");
                selection = 0;
                continue;
            }
            if (selection > max || selection < min) {
                System.err.println("Please enter a number between " + min + " and " + max);
                continue;
            }
            break;
        }
        return selection;
    }

    protected Simulation chosenSimulation;

    protected int processCount;
    protected int timeBetweenMessages;
}

