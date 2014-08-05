package pwall.gui;

import pwall.ChangeObserver;
import pwall.Process;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class GUIProcessWrapper extends JPanel implements ChangeObserver
{
    private static int HEIGHT = 65;
    private static int WIDTH = 150;

    GUIProcessWrapper(Process process, int x, int y)
    {
        this.x = x;
        this.y = y;
        this.process = process;
        process.registerObserver(this);
        setBounds(x, y, WIDTH, HEIGHT);

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.black, 2),
                process.getName(),
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Chicago", Font.BOLD, 12)
            ));
        setOpaque(true);
        setBackground(Color.LIGHT_GRAY);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setFont(new Font("Courier", Font.PLAIN, 12));
        g.drawString(process.getDetails(), 8, 30);
        g.drawString(process.getMoreDetails(), 8, 44);
    }

    public void notifyOfChange()
    {
        repaint();
    }

    public Process getProcess()
    {
        return process;
    }

    public int getX()   { return x; }
    public int getY()   { return y; }

    private Process process;

    private int x;
    private int y;
}

