package pwall;

import pwall.Message;
import pwall.Network;
import pwall.simple.SimpleProcess;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Test_Network {

    @Test
    public void test_constructor()
    {
        Network net = new Network();
        Assert.assertNotNull(net);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_nullMessageException()
    {
        Network net = new Network();
        net.send(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_nullDestinationException()
    {
        Network net = new Network();
        net.send(new Message("Testing", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_noDestinationException()
    {
        Network net = new Network();
        SimpleProcess proc = new SimpleProcess("Tester", 0, 0);
        net.send(new Message("Testing", proc));
    }

    @Test
    public void test_successfulSend() throws InterruptedException
    {
        Network net = new Network();
        SimpleProcess proc = new SimpleProcess("Tester", 0, 0);
        net.addToNetwork(proc);
        net.send(new Message("Testing", null, proc));
        Thread.sleep(10);
        Assert.assertEquals(proc.messagesReceived, 1);
    }

    @Test
    public void test_successfulBroadcast() throws InterruptedException
    {
        Network net = new Network();
        SimpleProcess proc1 = new SimpleProcess("Tester 1", 0, 0);
        SimpleProcess proc2 = new SimpleProcess("Tester 2", 0, 0);
        net.addToNetwork(proc1);
        net.addToNetwork(proc2);
        net.broadcast(new Message("Test broadcast message", proc1));
        Thread.sleep(10);
        Assert.assertEquals(proc1.messagesReceived, 0);
        Assert.assertEquals(proc2.messagesReceived, 1);
    }

    @Ignore
    public void test_threeWaySend() throws InterruptedException
    {
        Network net = new Network();
        SimpleProcess proc1 = new SimpleProcess("test_threeWaySend Tester 1", 1, 0);
        SimpleProcess proc2 = new SimpleProcess("test_threeWaySend Tester 2", 1, 0);
        SimpleProcess proc3 = new SimpleProcess("test_threeWaySend Tester 3", 1, 0);
        ProcessGroup processes = new ProcessGroup();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);
        net.addToNetwork(processes);

        processes.startAll();
        processes.pauseAll();

        Assert.assertEquals(1, proc1.messagesSent);
        Assert.assertEquals(1, proc2.messagesSent);
        Assert.assertEquals(1, proc3.messagesSent);

        Assert.assertEquals(2, proc1.messagesReceived);
        Assert.assertEquals(2, proc2.messagesReceived);
        Assert.assertEquals(2, proc3.messagesReceived);
    }
}

