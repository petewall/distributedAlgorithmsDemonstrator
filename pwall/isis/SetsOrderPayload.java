package pwall.isis;

import pwall.isis.ISISPayload;
import pwall.vector.VectorTimestamp;

public class SetsOrderPayload extends ISISPayload
{
    public SetsOrderPayload(VectorTimestamp timestamp, VectorTimestamp orderedMessage)
    {
        super(ISISPayload.SETSORDER, timestamp);
        this.orderedMessage = orderedMessage;
    }

    public VectorTimestamp getOrderedMessage()
    {
        return orderedMessage;
    }

    private VectorTimestamp orderedMessage;
}

