package pwall.gui;

import pwall.GlobalNetwork;
import pwall.Network;
import pwall.ProcessGroup;
import pwall.Simulation;
import pwall.Simulator;
import pwall.gui.GUINetwork;
import pwall.gui.GUIProcessWrapper;
import pwall.isis.ABCASTSimulation;
import pwall.isis.CBCASTSimulation;
import pwall.lamport.LamportSimulation;
import pwall.paxos.PaxosSimulation;
import pwall.simple.SimpleSimulation;
import pwall.vector.VectorSimulation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class GUISimulator extends Simulator
{
    public static void main(String [] args)
    {   
        if (args.length == 1) {
            GUISimulator simulator = new GUISimulator(args[0]);
        } else if (args.length == 2) {
            GUISimulator simulator = new GUISimulator(args[0], Integer.parseInt(args[1]));
        } else {
            GUISimulator simulator = new GUISimulator();
        }
    } 

    public GUISimulator()
    {
        setupSimulationEnvironment();
        GlobalNetwork.setNetwork(new GUINetwork(GlobalNetwork.getInstance(), processPanel));
        chooseSimulation();
        runSimulation();
    }

    public GUISimulator(String protocol)
    {
        this(protocol, 4);
    }

    public GUISimulator(String protocol, int numberOfProcesses)
    {
        processPanel = new JPanel();
        if (protocol.equals("simple")) {
            GlobalNetwork.setNetwork(new GUINetwork(new Network(10, 700, 2000, false), processPanel));
            chosenSimulation = new SimpleSimulation(numberOfProcesses, 1000);
        } else if (protocol.equals("lamport")) {
            GlobalNetwork.setNetwork(new GUINetwork(new Network(10, 700, 2000, false), processPanel));
            chosenSimulation = new LamportSimulation(numberOfProcesses, 30, 2000);
        } else if (protocol.equals("vector")) {
            GlobalNetwork.setNetwork(new GUINetwork(new Network(10, 700, 4000, false), processPanel));
            chosenSimulation = new VectorSimulation(numberOfProcesses, 30, 2000);
        } else if (protocol.equals("cbcast")) {
            GlobalNetwork.setNetwork(new GUINetwork(new Network(0, 700, 4000, false), processPanel));
            chosenSimulation = new CBCASTSimulation(numberOfProcesses, 4000);
        } else if (protocol.equals("abcast")) {
            GlobalNetwork.setNetwork(new GUINetwork(new Network(0, 700, 2000, false), processPanel));
            chosenSimulation = new ABCASTSimulation(numberOfProcesses, 7000, 100);
        } else if (protocol.equals("paxos")) {
            GlobalNetwork.setNetwork(new GUINetwork(new Network(0, 700, 2000, false), processPanel));
            chosenSimulation = new PaxosSimulation(numberOfProcesses);
        }
        runSimulation();
    }

    public void runSimulation()
    {
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(setupProcessPanel(chosenSimulation), BorderLayout.CENTER);
        content.add(setupButtonPanel(chosenSimulation), BorderLayout.SOUTH);

        JFrame window = new JFrame("Distributed Protocol Simulation");
        window.setContentPane(content);
        window.setSize(800,800);
        window.setLocation(100,100);
        window.setVisible(true);
    }

    public JPanel setupProcessPanel(Simulation sim)
    {
        processPanel.setLayout(null);
        ProcessGroup processes = sim.getProcesses();

        int radius = 280;
        processPanel.setMinimumSize(new Dimension(radius * 2, radius * 2));

        int count = processes.size();
        double radiansOffset = 2.0 * Math.PI / count;
        for (int i = 0; i < count; ++i) {
            // Coordinates determined by:
            //      [radius *] scale up to the size we want
            //      [Math.sin(radiansOffset * i)] convert from radians to x
            //      [Math.cos(radiansOffset * i + Math.PI] convert from radians to y
            //          Rotate by Pi radians so that the first process is top center
            //      [+ 1] make all values positive (the sin/cos result ranges from 0 - 2 now)
            //      [+ 20] Add some fluff for the margins.
            int x = (int)(radius * (Math.sin(radiansOffset * i          ) + 1)) + 20;
            int y = (int)(radius * (Math.cos(radiansOffset * i + Math.PI) + 1)) + 20;
            processPanel.add(new GUIProcessWrapper(processes.get(i), x, y));
        }

        return processPanel;
    }

    public JPanel setupButtonPanel(Simulation sim)
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton startStopButton = new JButton("Start");
        startStopButton.addActionListener(new StartStopButtonHandler(sim, startStopButton));
        buttonPanel.add(startStopButton);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ResetButtonHandler(sim));
        buttonPanel.add(resetButton);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new QuitButtonHandler(sim));
        buttonPanel.add(quitButton);
        return buttonPanel;
    }

    /**
     * The StartStopButtonHandler will pause or resume the simulation
     */
    private class StartStopButtonHandler implements ActionListener {
        StartStopButtonHandler(Simulation sim, JButton button) { this.sim = sim; this.button = button; }

        public void actionPerformed(ActionEvent e) {
            if (sim.getState()) {
                sim.pause();
                GlobalNetwork.getInstance().pauseWork();
                button.setText("Start");
            }
            else {
                GlobalNetwork.getInstance().startWork();
                sim.start();
                button.setText("Stop");
            }
        }

        Simulation sim;
        JButton button;
    }

    /**
     * The ResetButtonHandler resets the processes to their default states.
     */
    private class ResetButtonHandler implements ActionListener {
        ResetButtonHandler(Simulation sim) { this.sim = sim; }

        public void actionPerformed(ActionEvent e) {
            sim.reset();
        }

        Simulation sim;
    }

    /**
     * The QuitButtonHandler exist the simulation.
     */
    private class QuitButtonHandler implements ActionListener {
        QuitButtonHandler(Simulation sim) { this.sim = sim; }

        public void actionPerformed(ActionEvent e) {
            GlobalNetwork.getInstance().stopWork();
            sim.stop();
            System.exit(0);
        }

        Simulation sim;
    }

    public JPanel getProcessPanel()
    {
        return processPanel;
    }

    private JPanel processPanel;
}

