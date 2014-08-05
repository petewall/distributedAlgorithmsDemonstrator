package pwall.simple;

import pwall.Logger;
import pwall.Message;
import pwall.Process;

public class SimpleProcess extends Process
{
    public SimpleProcess(String name, int timeBetweenWork)
    {
        this(name, Integer.MAX_VALUE, timeBetweenWork);
    }

    public SimpleProcess(String name, int messagesToSend, int timeBetweenWork)
    {
        super(name, timeBetweenWork);
        this.messagesSent = 0;
        this.messagesToSend = messagesToSend;
        this.messagesReceived = 0;
    }

    public void doWork()
    {
        if (messagesSent < messagesToSend) {
            Message message = new Message(new String("Msg " + (messagesSent + 1) + " from " + getName()), this);

            // Send the message
            getComs().broadcast(message);
            messagesSent++;
        }
        notifyObservers();
    }

    public void deliver(Message message)
    {
        logger.log("Message received: " + message);
        messagesReceived++;
        notifyObservers();
    }

    public String toString()
    {
        return getName();
    }

    public String getDetails()
    {
        if (messagesToSend == Integer.MAX_VALUE)
            return "Sent: " + messagesSent;
        return "Sent: " + messagesSent + "/" + messagesToSend;
    }

    public String getMoreDetails()
    {
        return "Received: " + messagesReceived;
    }

    public void reset()
    {
        messagesSent = 0;
        messagesReceived = 0;
        notifyObservers();
    }

    public int messagesSent;
    public int messagesToSend;
    public int messagesReceived;
}

