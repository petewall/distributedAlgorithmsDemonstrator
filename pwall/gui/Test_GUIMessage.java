package pwall.gui;

import pwall.GlobalNetwork;
import pwall.Process;
import pwall.gui.GUINetwork;
import pwall.gui.GUIProcessWrapper;
import pwall.simple.SimpleProcess;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test_GUIMessage {
    public static void main(String[] a) {
        JFrame window = new JFrame();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        GUINetwork net = new GUINetwork(0, 1000, 2000, false, contentPanel);
        GlobalNetwork.setNetwork(net);

        Process proc1 = new SimpleProcess("GUITest 1", 20, 500);
        net.addToNetwork(proc1);
        contentPanel.add(new GUIProcessWrapper(proc1, 30, 100));

        Process proc2 = new SimpleProcess("GUITest 2", 0, 0);
        net.addToNetwork(proc2);
        contentPanel.add(new GUIProcessWrapper(proc2, 640, 30));

        Process proc3 = new SimpleProcess("GUITest 3", 0, 0);
        net.addToNetwork(proc3);
        contentPanel.add(new GUIProcessWrapper(proc3, 640, 180));

        window.setBackground(Color.WHITE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, 800, 300);
        window.setContentPane(contentPanel);
        window.setVisible(true);

        System.out.print("Starting in 1 second... ");
        try {Thread.sleep(1000);} catch (Exception e) { }
        System.out.println("Started!");

        proc1.startWork();
    }
}

