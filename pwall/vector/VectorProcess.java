package pwall.vector;

import pwall.Logger;
import pwall.Message;
import pwall.Process;
import pwall.vector.VectorTimestamp;

/**
 * VectorProcess is a simulated implementation of a distributed process that uses vector clocks
 * to order events on the system.  This is based on the work by André Schiper, Jorge Eggli and
 * Alain Sandoz.
 * @see http://dl.acm.org/citation.cfm?id=675010
 */
public class VectorProcess extends Process
{
    public VectorProcess(String name, int numberOfProcesses, int index, int internalEventPercent, int timeBetweenMessages)
    {
        super(name, timeBetweenMessages);

        this.clock = new VectorTimestamp(numberOfProcesses);
        this.index = index;
        this.internalEventPercent = internalEventPercent;
    }

    public void doWork()
    {
        if (randomizer.nextInt(100) < internalEventPercent) {
            clock.increment(index);
            logger.log("Internal event.");
        }
        else {
            sendAMessage();
        }
        notifyObservers();
    }

    protected void sendAMessage()
    {
        Process target = pickTarget();
        if (target != null) {
            clock.increment(index);
            Message message = new Message(clock.copy(), this, target);
            logger.log("Creating a message, stamped " + clock.toString(index) + ". Sending to " + target.getName());
            getComs().send(message, target);
        }
    }

    public void deliver(Message message)
    {
        this.deliver(message, true, false);
    }

    public void deliver(Message message, boolean deliveryIsAnEvent, boolean overIncrementSender)
    {
        if (deliveryIsAnEvent)
            clock.increment(index);

        int senderIndex = ((VectorProcess)(message.getSender())).getIndex();
        VectorTimestamp messageTimestamp = (VectorTimestamp)message.getPayload();
        logger.log("Message received. Timestamp: " + messageTimestamp);

        // Check for causal delivery order violations
        if (messageTimestamp.lessThan(clock))
            logger.log("CAUSAL DELIVERY ORDER VIOLATION! " + messageTimestamp + " < " + clock);

        // Advance the clock entry for each entry
        boolean advanced = false;
        for (int i = 0; i < clock.size(); ++i) {
            if (clock.get(i) < messageTimestamp.get(i)) {
                advanced = true;
                clock.set(i, messageTimestamp.get(i));
            }
            if (overIncrementSender && i == senderIndex && clock.get(i) == messageTimestamp.get(i)) {
                clock.increment(i);
            }
        }
        if (advanced)
            logger.log("Clock advanced to " + clock.toString(index));
        notifyObservers();
    }

    public String toString()
    {
        return getName() + " " + clock.toString(index);
    }

    public String getDetails()
    {
        return clock.toString(index);
    }

    public String getMoreDetails()
    {
        return "";
    }

    public void reset()
    {
        clock = new VectorTimestamp(clock.size());
        notifyObservers();
    }

    /**
     * Returns the index of this process within the vector clocks on the system.
     * @return The index of this process within the vector clocks on the system.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * The heart of this process is the Vector clock.  An array of timestamps where
     * each element represents a process.  The element for this process is
     * incremented every time an event occurs on this process.  The element for the
     * other processes are incremented when we receive a message with a number greater
     * than the one we have remembered.
     */
    protected VectorTimestamp clock;

    /**
     * The index for this process into the vector clock.
     */
    protected int index;

    /**
     * The likelyhood of internal events.  Must be within the range of 0 - 100.
     */
    private int internalEventPercent;

}

