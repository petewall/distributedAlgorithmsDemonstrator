package pwall.gui;

import pwall.Message;
import pwall.Network;

import pwall.gui.GUIMessage;

import java.awt.Component;
import javax.swing.JPanel;

public class GUINetwork extends Network
{
    public GUINetwork(int failurePercent, int lowerBound, int upperBound, boolean atomicBroadcast, JPanel componentPanel)
    {
        super(failurePercent, lowerBound, upperBound, atomicBroadcast);
        this.componentPanel = componentPanel;
    }

    public GUINetwork(Network otherNet, JPanel componentPanel)
    {
        this(otherNet.getFailurePercent(),
             otherNet.getLowerBound(),
             otherNet.getUpperBound(),
             otherNet.atomicBroadcastSupported(),
             componentPanel
        );
    }

    protected synchronized void route(Message message)
    {
        message.setVelocity(100 * timeBetweenUpdates / (float)getMessageDelay());

        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;
        for (Component comp : componentPanel.getComponents()) {
            if (comp instanceof GUIProcessWrapper) {
                if (((GUIProcessWrapper)comp).getProcess() == message.getSender()) {
                    startX = ((GUIProcessWrapper)comp).getX();
                    startY = ((GUIProcessWrapper)comp).getY();
                }
                if (((GUIProcessWrapper)comp).getProcess() == message.getDestination()) {
                    endX = ((GUIProcessWrapper)comp).getX();
                    endY = ((GUIProcessWrapper)comp).getY();
                }
            }
        }
        GUIMessage guiMessage = new GUIMessage(message, startX, startY, endX, endY, componentPanel);
        messagesToSend.add(guiMessage);
    }

    protected synchronized void routeNow(Message message)
    {
        message.setVelocity(100);
        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;
        for (Component comp : componentPanel.getComponents()) {
            if (comp instanceof GUIProcessWrapper) {
                if (((GUIProcessWrapper)comp).getProcess() == message.getSender()) {
                    startX = ((GUIProcessWrapper)comp).getX();
                    startY = ((GUIProcessWrapper)comp).getY();
                }
                if (((GUIProcessWrapper)comp).getProcess() == message.getDestination()) {
                    endX = ((GUIProcessWrapper)comp).getX();
                    endY = ((GUIProcessWrapper)comp).getY();
                }
            }
        }
        GUIMessage guiMessage = new GUIMessage(message, startX, startY, endX, endY, componentPanel);
        messagesToSend.add(guiMessage);
    }

    private JPanel componentPanel;
}

