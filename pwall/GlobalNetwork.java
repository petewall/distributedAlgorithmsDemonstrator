package pwall;

public class GlobalNetwork
{
    /**
     * Private constructor to prevent creating this object.
     */
    private GlobalNetwork() { }

    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    public static synchronized void setNetwork(Network newNet)
    {
        network = newNet;
    }

    public static synchronized Network getInstance()
    {
        if (network == null) {
            network = new Network();
        }
        return network;
    }

    private static Network network;
}

