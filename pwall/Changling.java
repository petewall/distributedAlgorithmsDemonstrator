package pwall;

import pwall.ChangeObserver;

public interface Changling
{
    public void registerObserver(ChangeObserver observer);

    public void notifyObservers();
}

