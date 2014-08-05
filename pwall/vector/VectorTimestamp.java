package pwall.vector;

public class VectorTimestamp
{
    public VectorTimestamp(int size)
    {
        vectorClock = new int[size];
        for (int i = 0; i < size; ++i) {
            vectorClock[i] = 0;
        }   
    }

    public VectorTimestamp(VectorTimestamp other)
    {
        this(other.size());
        for (int i = 0; i < size(); ++i) {
            vectorClock[i] = other.get(i);
        }   
    }
    
    public VectorTimestamp copy()
    {
        return new VectorTimestamp(this);
    }

    public String toString()
    {
        return toString(-1);
    }

    public String toString(int index)
    {
        String result = "{";
        for (int i = 0; i < vectorClock.length; ++i) {
            if (i == index)
                result += "[" + vectorClock[i] + "],";
            else
                result += vectorClock[i] + ",";
        }
        result = result.substring(0, result.length() - 1);
        result += "}";
        return result;
    }

    public int get(int index)
    {
        return vectorClock[index];
    }

    public void set(int index, int value)
    {
        vectorClock[index] = value;
    }

    public void increment(int index)
    {
        vectorClock[index]++;
    }

    public int size()
    {
        return vectorClock.length;
    }

    /**
     * Ta = Tb iff for all i, Ta[i] = Tb[i]
     * @param other The VectorTimestamp to compare to.
     * @return The equality result.
     */
    public boolean equals(VectorTimestamp other)
    {
        for (int i = 0; i < size(); ++i) {
            if (get(i) != other.get(i))
                return false;
        }
        return true;
    }

    /**
     * Ta ≤ Tb iff for all i, Ta[i] ≤ Tb[i]
     * @param other The VectorTimestamp to compare to.
     * @return The ≤ result.
     */
    public boolean lessThanEquals(VectorTimestamp other)
    {
        for (int i = 0; i < size(); ++i) {
            if (get(i) > other.get(i))
                return false;
        }
        return true;
    }

    /**
     * Ta &lt; Tb iff Ta ≤ Tb and Ta ≠ Tb
     * @param other The VectorTimestamp to compare to.
     * @return The &lt; result.
     */
    public boolean lessThan(VectorTimestamp other)
    {
        if (!equals(other) && lessThanEquals(other))
            return true;
        return false;
    }

    /**
     * Ta || Tb iff Ta &lt; Tb and Tb &lt; Ta
     * @param other The VectorTimestamp to compare to.
     * @return The concurrency result.
     */
    public boolean concurrent(VectorTimestamp other)
    {
        if (this.lessThan(other) && other.lessThan(this))
            return true;
        return false;
    }

    private int[] vectorClock;
}

