package pwall;

import pwall.CommunicationSubsystem;
import pwall.Logger;
import pwall.Message;
import pwall.Process;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * The Network class simulates a network that connects processes.  When processes send
 * messages to each other, they flow through this network.
 * @author Pete Wall
 */
public class Network extends Thread
{
    protected static int timeBetweenUpdates = 10;

    /**
     * Creates a new empty network.
     */
    public Network()
    {
        this(0, 0, 0, true);
    }

    /**
     * Creates a new empty network with the given parameters.
     * @param failurePercent The percentage of messages that will be lost (0-100).
     * @param lowerBound The shortest delay from when the message is sent to when it is
     *                   delivered to its destination
     * @param upperBound The longest delay from when the message is sent to when it is
     *                   delivered to its destination
     * @param atomicBroadcast If true, then a broadcast is treated as a single send, and
     *                        all copies have the same chance for failure.  If false,
     *                        then the broadcast is simulated by sending the message as
     *                        individual messages, one after the other.
     */
    public Network(int failurePercent, int lowerBound, int upperBound, boolean atomicBroadcast)
    {
        this.failurePercent = failurePercent;
        this.delayLowerBound = lowerBound;
        this.delayUpperBound = upperBound;
        this.atomicBroadcast = atomicBroadcast;
        this.registeredProcesses = new HashSet<Process>();
        this.randomizer = new Random();
        this.messages = new LinkedList<Message>();
        this.messagesToSend = new LinkedList<Message>();
        this.logger = new Logger("Network.log", this);
        this.running = true;
        this.working = true;
        this.start();
    }

    public void startWork()
    {
        working = true;
    }

    public void pauseWork()
    {
        working = false;
    }

    public void stopWork()
    {
        pauseWork();
        running = false;
        this.interrupt();
        try {
            this.join();
        }   
        catch (Exception e) {
            logger.log("Caught an exception when stopping");
        }   
    }   

    /**
     * The Network thread manages messages in the message queue.  Any that are ready
     * to be delivered will be dequeued and sent to the destination.
     */
    public void run()
    {
        while (running) {
            if (working) {
                try {
                    sendMessages();
                    updateMessages();
                    Thread.sleep(timeBetweenUpdates);
                }
                catch (InterruptedException e)
                {
                    logger.log("Sleeping was interrupted.");
                }
            }
            try {
                sleep(10);
            } catch (InterruptedException e)
            {   
                logger.log("Sleeping was interrupted.");
            }   
        }
    }

    protected synchronized void sendMessages()
    {
        if (messagesToSend.size() != 0) {
            messages.addAll(messagesToSend);
            messagesToSend.clear();
        }
    }

    protected synchronized void updateMessages()
    {
        if (messages.size() != 0) {
            Iterator<Message> messageIter = messages.iterator();
            while (messageIter.hasNext()) {
                Message message = messageIter.next();
                if (message.getTerminated()) {
                    logger.log("Message lost: " + message + " from " + message.getSender().getName() + " to " + message.getDestination().getName());
                    messageIter.remove();
                }
                if (readyToDeliver(message)) {
                    logger.log("Time to deliver " + message + " from " + message.getSender().getName() + " to " + message.getDestination().getName());
                    messageIter.remove();
                    message.getDestination().getComs().receive(message);
                }
            }
        }
    }

    protected boolean readyToDeliver(Message message)
    {
        return message.update();
    }

    /**
     * Sends a message to a single registered process.
     * @param message The message to send.
     * @param destination The process to receive the message.
     * @throws IllegalArgumentException if either paramter is null, or if the destionation
     *                                  is not registered.
     */
    public void send(Message message)
    {
        if (message == null) {
            throw new IllegalArgumentException("The message cannot be null");
        }
        if (message.getDestination() == null) {
            throw new IllegalArgumentException("The destination cannot be null");
        }
        if (registeredProcesses.contains(message.getDestination())) {
            if (messageFailed(message))
                return;

            route(message);
        }
        else {
            throw new IllegalArgumentException("The destination is not known by this network");
        }
    }

    /**
     * Sends a message to all registered processes.
     * @param message The message to send.
     * @throws IllegalArgumentException if message is NULL
     */
    public void broadcast(Message message)
    {
        if (message == null) {
            throw new IllegalArgumentException("The message cannot be null");
        }

        if (atomicBroadcast && messageFailed(message))
            return;

        for (Process dest : registeredProcesses) {
            Message messageToSend = message.copy();
            messageToSend.setDestination(dest);

            if (message.getSender() == dest) {
                routeNow(messageToSend);
            } else {
                if (atomicBroadcast) {
                    route(messageToSend);
                } else {
                    send(messageToSend);
                }
            }
        }
    }

    /**
     * The route method is used internally by the network when sending the message
     * to the destination.
     * @param message The message to send
     */
    protected synchronized void route(Message message)
    {
        message.setVelocity(100 * timeBetweenUpdates / (float)getMessageDelay());
        messagesToSend.add(message);
    }

    protected synchronized void routeNow(Message message)
    {
        message.setVelocity(100);
        messagesToSend.add(message);
    }

    /**
     * Chooses occasionally to fail a message.
     * @param The message that might fail (needed for the message value).
     * @return True if the message should fail.  False if it should not.
     */
    private boolean messageFailed(Message message)
    {
        if (failurePercent != 0 && randomizer.nextInt(100) < failurePercent) {
            logger.log("Message lost: " + message);
            return true;
        }
        return false;
    }

    /**
     * Calculates the random message delay based on the lower and upper bounds.
     * @return The delay (in ms) for this message.
     */
    protected int getMessageDelay() 
    {
        int delay = delayLowerBound + (delayUpperBound - delayLowerBound == 0 ? 0 : randomizer.nextInt(delayUpperBound - delayLowerBound));
        return delay;
    }

    /**
     * Adds a process to this network.
     * @param processes The process to add.
     */
    public void addToNetwork(Process process)
    {
        registeredProcesses.add(process);
    }

    /**
     * Adds all processes in the given process group to this network.
     * @param processes The list of processes to add.
     */
    public void addToNetwork(ProcessGroup processes)
    {
        for (Process process : processes) {
            addToNetwork(process);
        }
    }

    /**
     * Removes the given process from this network.
     * @param process The process to remove.
     */
    public void removeFromNetwork(Process process)
    {
        registeredProcesses.remove(process);
    }

    /**
     * Removes the given list of processes from this network.
     * @param processes The list of processes to remove.
     */
    public void removeFromNetwork(ProcessGroup processes)
    {
        for (Process process : processes) {
            removeFromNetwork(process);
        }
    }

    public String toString()
    {
        return "Network";
    }

    public int getFailurePercent()
    {
        return failurePercent;
    }

    public int getLowerBound()
    {
        return delayLowerBound;
    }

    public int getUpperBound()
    {
        return delayUpperBound;
    }

    public boolean atomicBroadcastSupported()
    {
        return atomicBroadcast;
    }

    /**
     * The list of processes known by this network.
     */
    private HashSet<Process> registeredProcesses;

    private int delayLowerBound;
    private int delayUpperBound;
    private int failurePercent;
    private boolean atomicBroadcast;
    private Random randomizer;

    private LinkedList<Message> messages;
    protected LinkedList<Message> messagesToSend;

    private Logger logger;

    private boolean running;
    private boolean working;
}

