package pwall;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger
{
    public Logger(String filename, Object source)
    {
        this.source = source;
        try {
            this.writer = new PrintWriter(filename);
        }
        catch (FileNotFoundException e) {
            System.err.println("Could not create a writer.  Will proceed without logging.");
        }
    }

    public void log(String message)
    {
        System.out.println("[" + source.toString() + "] " + message);
        if (writer != null) {
            writer.println(message);
            writer.flush();
        }   
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }
    }

    private PrintWriter writer;

    private Object source;
}
