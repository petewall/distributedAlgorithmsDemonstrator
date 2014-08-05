package pwall;

import pwall.Changling;
import pwall.CommunicationSubsystem;
import pwall.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public abstract class Process extends Thread implements Changling
{
    public Process(String name)
    {
        this(name, 0);
    }

    public Process(String name, int timeBetweenWork)
    {
        super(name);
        this.running = true;
        this.working = false;
        this.timeBetweenWork = timeBetweenWork;
        this.coms = new CommunicationSubsystem(this);
        this.targets = new ArrayList<Process>();
        this.randomizer = new Random();

        this.logger = new Logger(name.replace(" ", "_") + ".log", this);
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

    public void run()
    {
        try {
            sleep((int)(randomizer.nextFloat() * (float)timeBetweenWork));
        } catch (InterruptedException e) {
            logger.log("Startup sleeping was interrupted.");
        }

        logger.log("Started");
        while (running) {
            if (working) {
                doWork();

                if (timeBetweenWork > 0) {
                    try {
                        sleep(timeBetweenWork + (int)(randomizer.nextFloat() * timeBetweenWork));
                    } catch (InterruptedException e) {
                        logger.log("Sleeping was interrupted.");
                    }
                }
            }
            try {
                sleep(10);
            } catch (InterruptedException e)
            {
                logger.log("Sleeping was interrupted.");
            }
        }
        logger.log("Stopped");
        logger.close();
    }

    public void addTarget(Process targetProcess)
    { 
        targets.add(targetProcess);
    } 

    protected Process pickTarget()
    {   
        if (targets.size() == 0)
            return null;
        return targets.get(randomizer.nextInt(targets.size()));
    }

    public abstract void reset();

    public abstract void doWork();

    public CommunicationSubsystem getComs()
    {
        return coms;
    }

    public boolean shouldDelay(Message message)
    {
        return false;
    }

    public abstract void deliver(Message message);

    public abstract String getDetails();

    public abstract String getMoreDetails();

    /**
     * This indicates whether the process should continue running or not.
     */
    protected boolean running;

    /**
     * This indicates whether the process should start its work or not.
     */
    protected boolean working;

    protected int timeBetweenWork;

    /**
     * The communication subsystem for this process.  This system handles
     * the "networking" layer, and is responsible for sending, receiving,
     * and delivering messages, in the correct order.
     */
    private CommunicationSubsystem coms;

    protected Random randomizer;

    protected ArrayList<Process> targets;

    protected Logger logger;

    public final void registerObserver(ChangeObserver observer)
    {
        this.observer = observer;
    }

    public final void notifyObservers()
    {
        if (observer != null)
            observer.notifyOfChange();
    }

    protected ChangeObserver observer;
}

