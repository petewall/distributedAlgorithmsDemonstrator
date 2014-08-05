package pwall.lamport;

import pwall.lamport.LamportProcess;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Test_LamportProcess
{
    private LamportProcess process;

    @Before
    public void setup()
    {
        process = new LamportProcess("Tester");
    }

    @After
    public void tearDown()
    {
        process = null;
    }

    @Test
    public void test_constructor()
    {
        Assert.assertNotNull(process);
    }

    @Test
    public void test_start_and_stop()
    {
        process.startWork();
        process.stopWork();
    }
}

