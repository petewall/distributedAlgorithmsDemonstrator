package pwall;

import pwall.GlobalNetwork;
import pwall.Network;
import pwall.Process;
import java.util.Iterator;
import java.util.LinkedList;

public class CommunicationSubsystem
{
    public CommunicationSubsystem(Process process)
    {
        this(process, GlobalNetwork.getInstance());
    }

    public CommunicationSubsystem(Process process, Network network)
    {
        this.process = process;
        this.network = network;
        this.delayQueue = new LinkedList<Message>();
    }

    public void send(Message message, Process destination)
    {
        network.send(message);
    }

    public void send(Message message, ProcessGroup destinations)
    {
        for (Process destination : destinations) {
            Message messageToSend = message.copy();
            messageToSend.setDestination(destination);
            send(messageToSend, destination);
        }
    }

    public void broadcast(Message message)
    {
        network.broadcast(message);
    }

    public void receive(Message message)
    {
        if (process.shouldDelay(message)) {
            delayQueue.add(message);
        } else {
            process.deliver(message);
            message.delivered();
        }

        Iterator<Message> messageIter = delayQueue.iterator();
        while (messageIter.hasNext()) {
            Message delayedMessage = messageIter.next();
            if (!process.shouldDelay(delayedMessage)) {
                messageIter.remove();
                process.deliver(delayedMessage);
                delayedMessage.delivered();
            }
        }
    }

    public String toString()
    {
        return process.getName();
    }

    /**
     * The process this communication subsystem belongs to.
     */
    private Process process;

    /**
     * The network this process is connected to.
     */
    private Network network;
    private LinkedList<Message> delayQueue;
}

