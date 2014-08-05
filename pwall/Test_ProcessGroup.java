package pwall;

import pwall.simple.SimpleProcess;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Test_ProcessGroup
{
    @Test
    public void test_constructor()
    {
        ProcessGroup processes = new ProcessGroup();
        Assert.assertNotNull(processes);
    }

    @Test
    public void test_start_and_stop() throws InterruptedException
    {
        ProcessGroup processes = new ProcessGroup();
        SimpleProcess proc1 = new SimpleProcess("Tester", 0, 0);
        SimpleProcess proc2 = new SimpleProcess("Tester", 0, 0);
        processes.add(proc1);
        processes.add(proc2);
        processes.startAll();
        processes.pauseAll();
    }

    @Test
    public void test_start_and_stop_with_messages() throws InterruptedException
    {
        Network net = GlobalNetwork.getInstance();
        SimpleProcess proc1 = new SimpleProcess("test_start_and_stop_with_messages Tester 1", 1, 0); 
        SimpleProcess proc2 = new SimpleProcess("test_start_and_stop_with_messages Tester 2", 1, 0); 
        SimpleProcess proc3 = new SimpleProcess("test_start_and_stop_with_messages Tester 3", 1, 0); 
        ProcessGroup processes = new ProcessGroup();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);
        net.addToNetwork(processes);

        processes.startAll();
        Thread.sleep(100);
        processes.pauseAll();

        Assert.assertEquals(1, proc1.messagesSent);
        Assert.assertEquals(1, proc2.messagesSent);
        Assert.assertEquals(1, proc3.messagesSent);

        Assert.assertEquals(2, proc1.messagesReceived);
        Assert.assertEquals(2, proc2.messagesReceived);
        Assert.assertEquals(2, proc3.messagesReceived);
    }

    @Test
    public void test_start_and_stop_with_messages_interrupted() throws InterruptedException
    {
        Network net = GlobalNetwork.getInstance();
        SimpleProcess proc1 = new SimpleProcess("test_threeWaySend Tester 1", 1, 100);
        SimpleProcess proc2 = new SimpleProcess("test_threeWaySend Tester 2", 1, 100);
        SimpleProcess proc3 = new SimpleProcess("test_threeWaySend Tester 3", 1, 100);
        ProcessGroup processes = new ProcessGroup();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);
        net.addToNetwork(processes);

        processes.startAll();
        Thread.sleep(50);
        processes.pauseAll();

        Assert.assertEquals(1, proc1.messagesSent);
        Assert.assertEquals(1, proc2.messagesSent);
        Assert.assertEquals(1, proc3.messagesSent);

        Assert.assertEquals(2, proc1.messagesReceived);
        Assert.assertEquals(2, proc2.messagesReceived);
        Assert.assertEquals(2, proc3.messagesReceived);
    }
}

