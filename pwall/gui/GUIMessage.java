package pwall.gui;

import pwall.Message;
import pwall.Process;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class GUIMessage extends Message
{
    private class GUIMessageMouseHandler implements MouseListener {
        public GUIMessageMouseHandler(GUIMessage message) {
            this.message = message;
            this.velocity = message.getVelocity();
        }

        public void mouseEntered(MouseEvent e) {
            message.setVelocity(0);
        }

        public void mouseExited(MouseEvent e) {
            message.setVelocity(velocity);
        }

        public void mouseClicked(MouseEvent e) {
            message.terminate();
            message.delivered();
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) { }

        GUIMessage message;
        float velocity;
    }

    private class GUIMessageComponent extends JPanel
    {
        GUIMessageComponent(GUIMessage message, int startX, int startY, int endX, int endY)
        {
            this.payload = message.getPayload().toString();
            this.startX = startX;
            this.startY = startY + 60;
            this.endX = endX;
            this.endY = endY + 60;
            setBounds(startX, startY, 100, 20);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            setBackground(Color.WHITE);
            this.addMouseListener(new GUIMessageMouseHandler(message));
        }

        public void moveMessage(float percentComplete)
        {
            int x = (int)((endX - startX) * percentComplete) / 100 + startX;
            int y = (int)((endY - startY) * percentComplete) / 100 + startY;
            setBounds(x, y, 100, 20);
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawString(payload, 3, 12);
        }

        int startX;
        int startY;
        int endX;
        int endY;
        String payload;
    }

    GUIMessage(Message message, int startX, int startY, int endX, int endY, JPanel guiContainer)
    {
        super(message);
        this.guiContainer = guiContainer;
        this.guiComponent = new GUIMessageComponent(this, startX, startY, endX, endY);
        this.guiContainer.add(this.guiComponent);
    }

    JComponent getComponent()
    {
        return guiComponent;
    }

    public boolean update()
    {
        boolean ret = super.update();
        guiComponent.moveMessage(getPercentComplete());
        guiComponent.repaint();
        return ret;
    }

    public void delivered()
    {
        guiContainer.remove(guiComponent);
        guiContainer.repaint();
    }

    JPanel guiContainer;
    GUIMessageComponent guiComponent;
}

