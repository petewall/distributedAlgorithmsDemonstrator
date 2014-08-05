package pwall.simple;

import pwall.Message;
import pwall.simple.SimpleProcess;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Test_SimpleProcess
{
    @Test
    public void test_constructor()
    {
        SimpleProcess process = new SimpleProcess("Tester", 0, 0);
        Assert.assertNotNull(process);
    }

    @Test
    public void test_start_and_stop()
    {
        SimpleProcess process = new SimpleProcess("Tester", 0, 0);
        process.startWork();
        process.pauseWork();
    }

    @Test
    public void test_sent_one_message() throws InterruptedException
    {
        SimpleProcess process = new SimpleProcess("test_sent_one_message Tester", 1, 0);
        process.startWork();
        Thread.sleep(20);
        process.pauseWork();
        Assert.assertEquals(1, process.messagesSent);
    }

    @Test
    public void test_sent_one_message_interrupted() throws InterruptedException
    {
        SimpleProcess process = new SimpleProcess("Process test_sent_one_message_interrupted", 2, 500);
        process.startWork();
        Thread.sleep(100);
        process.stopWork();
        Assert.assertEquals(1, process.messagesSent);
    }

    @Test
    public void test_received_one_message() throws InterruptedException
    {
        SimpleProcess process = new SimpleProcess("test_received_one_message Tester", 1, 0);
        process.deliver(new Message("test_received_one_message Message", null, process));
        Assert.assertEquals(1, process.messagesReceived);
    }
}

