package pwall.gui;

import pwall.gui.GUIProcessWrapper;
import pwall.simple.SimpleProcess;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test_GUIProcess {
    public static void main(String[] a) {
        JFrame window = new JFrame();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.add(new GUIProcessWrapper(new SimpleProcess("GUITest 1", 0), 10, 10));
        contentPanel.add(new GUIProcessWrapper(new SimpleProcess("GUITest 2", 0), 10, 70));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, 300, 300);
        window.setContentPane(contentPanel);
        window.setVisible(true);
    }
}

