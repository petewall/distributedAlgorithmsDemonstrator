package pwall.isis;

import pwall.Message;
import pwall.isis.ISISPayload;
import pwall.vector.VectorProcess;
import pwall.vector.VectorTimestamp;

import java.util.LinkedList;

public class ISISProcess extends VectorProcess
{
    /**
     * Constructor used for CBCAST-only simulations.
     */
    public ISISProcess(String name, int numberOfProcesses, int index, int timeBetweenMessages)
    {
        this(name, numberOfProcesses, index, timeBetweenMessages, 0, false);
    }

    /**
     * Constructor used for CBCAST or ABCAST simulations.
     */
    public ISISProcess(String name, int numberOfProcesses, int index, int timeBetweenMessages, int percentABCAST, boolean tokenHolder)
    {
        super(name, numberOfProcesses, index, 0, timeBetweenMessages);
        this.percentABCAST = percentABCAST;
        this.tokenHolder = tokenHolder;
        this.setOrder = new LinkedList<VectorTimestamp>();
        this.actionString = new String();
    }

    public void reset()
    {
        this.setOrder = new LinkedList<VectorTimestamp>();
        this.actionString = new String();
        super.reset();
    }

    /**
     * Broadcasts a CBCAST or ABCAST message to the system.
     */
    protected void sendAMessage()
    {
        Message message;
        if (tokenHolder) {
            return;
        } else {
            int messageType = ISISPayload.CBCAST;
            if (percentABCAST != 0 && randomizer.nextInt(100) < percentABCAST) {
                messageType = ISISPayload.ABCAST;
            }
            clock.increment(index);
            message = new Message(new ISISPayload(messageType, clock), this);
        }

        logger.log("Creating a message, stamped " + clock.toString(index) + ".");
        getComs().broadcast(message);
    }

    /**
     * Determines if this message has arrived before another that we are expecting.
     * This is done by comparing our current clock with the one contained in the
     * message.  In order to deliver this message, three properties must hold:
     *   1. The sender's entry in the vector timestamp must be 1 larger than our
     *      current value for the sender.  This ensures that we have seen all
     *      messages from the sender before this one.
     *   2. For all other processes in the vector, the message's timestamp must be
     *      less than or equal to our own.  This ensures that we have seen all of
     *      the messages the sender saw before sending this message.
     *   3. The message is an ABCAST message and we are the token holder.
     *   4. The message is an ABCAST message, we are not the token holder, but we
     *      have received the SetsOrder message from the token holder, and this
     *      message is next.
     * When these are true, this message can be delivered.  If not, we will queue
     * it and wait for the earlier messages to arrive.
     * @param message The message that might have to wait.
     * @return true if this message should be delayed until other messages arrive.
     */
    public boolean shouldDelay(Message message)
    {
        ISISPayload messagePayload = (ISISPayload)message.getPayload();
        int senderIndex = ((ISISProcess)message.getSender()).getIndex();
        logger.log("checking : " + messagePayload.toString(senderIndex));

        if (!tokenHolder) {
            if (messagePayload.getType() == ISISPayload.ABCAST)
            {
                if (setOrder.size() > 0 && messagePayload.equals(setOrder.peek())) {
                    setOrder.pop();
                    return false;
                }
                return true;
            } else if (messagePayload.getType() == ISISPayload.SETSORDER) {
                if (messagePayload.get(senderIndex) == clock.get(senderIndex) + 1) {
                    return false;
                }
                return true;
            }
        }

        if (senderIndex == index) {
            return false;
        }
        if (messagePayload.get(senderIndex) == clock.get(senderIndex) + 1) {
            for (int i = 0; i < clock.size(); ++i) {
                if (i == senderIndex)
                    continue;
                if (messagePayload.get(i) > clock.get(i)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    public void deliver(Message message)
    {
        int senderIndex = ((ISISProcess)message.getSender()).getIndex();
        ISISPayload messagePayload = (ISISPayload)message.getPayload();

        if (messagePayload.getType() != ISISPayload.SETSORDER) {
            actionString = Integer.toString(senderIndex).concat("," + actionString);
        }

        if (index == senderIndex) {
            return;
        }

        if (tokenHolder) {
            if (messagePayload.getType() == ISISPayload.ABCAST) {
                clock.increment(index);
                logger.log("Creating a message, stamped " + clock.toString(index) + ".");
                getComs().broadcast(new Message(new SetsOrderPayload(clock, messagePayload), this));
            }
        } else {
            if (messagePayload.getType() == ISISPayload.SETSORDER) {
                setOrder.add(((SetsOrderPayload)messagePayload).getOrderedMessage());
            }
            else if (messagePayload.getType() == ISISPayload.ABCAST) {
                return;
            }
        }

        super.deliver(message, false, false);
    }

    public String getMoreDetails()
    {
        return actionString;
    }

    private int percentABCAST;

    /**
     * True if this ISIS Process is the token holder for organizing ABCAST messages.
     */
    private boolean tokenHolder;

    private LinkedList<VectorTimestamp> setOrder;

    private String actionString;
}

