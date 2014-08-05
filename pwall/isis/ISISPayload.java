package pwall.isis;

import pwall.vector.VectorTimestamp;

public class ISISPayload extends VectorTimestamp
{
    public static final int CBCAST = 0;
    public static final int ABCAST = 1;
    public static final int SETSORDER = 2;

    public ISISPayload(int type, VectorTimestamp timestamp)
    {
        super(timestamp);
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    public String toString()
    {
        return this.toString(-1);
    }

    public String toString(int index)
    {
        switch (type) {
            case ISISPayload.CBCAST:
                return "C" + super.toString(index);
            case ISISPayload.ABCAST:
                return "A" + super.toString(index);
            case ISISPayload.SETSORDER:
                return "S" + super.toString(index);
            default:
                return super.toString(index);
        }
    }

    private int type;
}

