package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientConnectionStream {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ServerClientConnectionStream(Socket socket)
    {
        super();
        this.socket = socket;
        getStream();
    }

    private void getStream()
    {
        try
        {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
        if (socket != null)
        {
            try
            {
                socket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void send(String message)
    {
        writer.println(message);
        writer.flush();
    }

    public String read()
    {
        try
        {
            return reader.readLine();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
