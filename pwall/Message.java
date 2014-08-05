package pwall;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * The message is a simple data class that has a payload.
 */
public class Message
{
    /**
     * Construct a message with a payload.
     * @param payload The payload of this message.
     */
    public Message(Object payload, Process sender)
    {
        this(payload, sender, null);
    }

    /**
     * Construct a message with a payload.
     * @param payload The payload of this message.
     */
    public Message(Object payload, Process sender, Process destination)
    {
        this.payload = payload;
        this.sender = sender;
        this.destination = destination;
        this.percentComplete = 0;
        this.velocity = 0;
        this.terminated = false;
    }

    /**
     * Copy constructor.
     */
    public Message(Message original)
    {
        this(original.getPayload(), original.getSender(), original.getDestination()); 
        this.percentComplete = original.getPercentComplete();
        this.velocity = original.getVelocity();
        this.terminated = original.getTerminated();
    }

    public Message copy()
    {
        return new Message(this);
    }

    public Process getSender()
    {
        return this.sender;
    }

    public Process getDestination()
    {
        return this.destination;
    }

    public void setDestination(Process newDestination)
    {
        this.destination = newDestination;
    }

    public float getVelocity()
    {
        return velocity;
    }

    public void setVelocity(float newVelocity)
    {
        this.velocity = newVelocity;
    }

    public void terminate()
    {
        terminated = true;
    }

    public boolean getTerminated()
    {
        return terminated;
    }

    public boolean update()
    {
        if (percentComplete >= 100)
            return true;
        percentComplete += velocity;
        return false;
    }

    public float getPercentComplete()
    {
        return percentComplete;
    }

    public void delivered()
    {
        // Do nothing
    }

    /**
     * Print the string representation of this message.
     */
    public String toString()
    {
        return payload.toString();
    }

    public Object getPayload()
    {
        return payload;
    }

    protected Process sender;

    protected Process destination;

    protected float percentComplete;

    protected float velocity;

    private boolean terminated;

    private Object payload;
}

