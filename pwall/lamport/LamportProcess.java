package pwall.lamport;

import pwall.Logger;
import pwall.Message;
import pwall.Process;
import java.util.HashMap;

/**
 * LamportProcess is a simulated implementation of a distributed process that uses a logical clock
 * to order events on the system.  This is based on the work by Leslie Lamport.
 * @see http://research.microsoft.com/en-us/um/people/lamport/pubs/time-clocks.pdf
 */
public class LamportProcess extends Process
{
    public LamportProcess(String name)
    {
        this(name, 0, 3000);
    }

    public LamportProcess(String name, int internalEventPercent, int timeBetweenMessages)
    {
        super(name, timeBetweenMessages);
        this.clock = 0;
        this.internalEventPercent = internalEventPercent;
        this.otherClocks = new HashMap<Process, Integer>();
    }

    public void doWork()
    {
        if (randomizer.nextInt(100) < internalEventPercent) {
            clock++;
            logger.log("Internal event.");
        }
        else {
            sendAMessage();
        }
        notifyObservers();
    }

    public synchronized void sendAMessage()
    {
        Process target = pickTarget();
        if (target != null) {
            Message message = new Message(new Integer(++clock), this, target);
            logger.log("Creating a message, stamped " + clock + ". Sending to " + target.getName());
            getComs().send(message, target);
        }
    }

    public synchronized void deliver(Message message)
    {
        Integer messageTimestamp = (Integer)message.getPayload();
        logger.log("Message received. Timestamp: " + messageTimestamp);

        // Detect messages delivered out of order
        if (otherClocks.containsKey(message.getSender())) {
            if (otherClocks.get(message.getSender()) > messageTimestamp) {
                logger.log("MESSAGE DELIVERED OUT OF ORDER!");
            }
        }
        otherClocks.put(message.getSender(), new Integer(messageTimestamp));

        // Manage our clock
        if (clock < messageTimestamp) {
            clock = messageTimestamp.intValue();
            logger.log("Clock advanced to " + clock);
        }
        notifyObservers();
    }

    public String toString()
    {
        return getName() + " {" + clock + "}";
    }

    public String getDetails()
    {
        return "{" + clock + "}";
    }

    public String getMoreDetails()
    {
        return "";
    }

    public void reset()
    {
        clock = 0;
        notifyObservers();
    }

    /**
     * The heart of this process is the Lamport clock.  An internal timestamp that
     * is incremented every time an event occurs on this process: A message that is
     * sent to another process, or an internal event.
     */
    private int clock;

    /**
     * The likelyhood of internal events.  Must be within the range of 0 - 100.
     */
    private int internalEventPercent;

    /**
     * Remember the values seen from other processes, so we can detect messages
     * delivered out of order.
     */
    private HashMap<Process, Integer> otherClocks;
}

