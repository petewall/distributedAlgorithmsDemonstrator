package pwall;

import pwall.ProcessGroup;

public abstract class Simulation
{
    public Simulation()
    {
        running = false;
    }

    public void start()
    {
        running = true;
        processes.startAll();
    }

    public void pause()
    {
        try {
            running = false;
            processes.pauseAll();
        } catch (Exception e) { }
    }

    public void stop()
    {
        try {
            running = false;
            processes.stopAll();
        } catch (Exception e) { }
    }

    public void reset()
    {
        processes.resetAll();
    }

    public boolean getState()
    {
        return running;
    }

    public ProcessGroup getProcesses()
    {
        return processes;
    }
   
    protected boolean running;

    protected ProcessGroup processes;
}

