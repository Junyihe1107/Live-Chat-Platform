package clinet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ClientToClientConnectionStream {
    private DatagramPacket userReceivePacket;
    //    private DatagramPacket userReceivePacket;
    private DatagramSocket userDataSocket;
    private byte buffer[];
    private InetAddress hostAddress;
    private int port;

    public ClientToClientConnectionStream(DatagramSocket userDataSocket)
    {
        super();
        this.userDataSocket = userDataSocket;
        buffer = new byte[1024];
        userReceivePacket = new DatagramPacket(buffer, buffer.length);
    }

    public ClientToClientConnectionStream(DatagramSocket userDataSocket, InetAddress hostAddress, int port)
    {
        super();
        this.hostAddress = hostAddress;
        this.port = port;
        this.userDataSocket = userDataSocket;
        buffer = new byte[1024];
        userReceivePacket = new DatagramPacket(buffer, buffer.length);
    }

    public void send(String message)
    {
        try
        {
            userDataSocket.send(new DatagramPacket(message.getBytes(), message.getBytes().length, hostAddress, port));
        } catch (IOException e)
        {
            System.out.println("UDP端口被占用！");
            e.printStackTrace();
        }
    }

    public void sendf(byte[] buf)
    {
        try
        {
            userDataSocket.send(new DatagramPacket(buf, buf.length, hostAddress, port));
        } catch (IOException e)
        {
            System.out.println("UDP端口被占用！");
            e.printStackTrace();
        }
    }


    public String read()
    {
        try
        {
            if (!userDataSocket.isClosed())
            {
                userDataSocket.receive(userReceivePacket);
                //要.getLength();和21行buffer
//                System.out.println(userReceivePacket.getLength());
                System.out.print("现在端口号：" + userReceivePacket.getPort());
                String string = new String(userReceivePacket.getData(), 0, userReceivePacket.getLength(), "UTF-8");
                return string;
            }
        } catch (UnsupportedEncodingException e)
        {
            System.out.println("不支持的编码类型！");
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println(userDataSocket == null);
            e.printStackTrace();
        }
        return null;
    }




//    public int readf()
//    {
//        try
//        {
//            if (!userDataSocket.isClosed())
//            {
//                userDataSocket.receive(userReceivePacket);
//                return userReceivePacket.getLength();
//            }
//        } catch (UnsupportedEncodingException e)
//        {
//            System.out.println("不支持的编码类型！");
//            e.printStackTrace();
//        } catch (IOException e)
//        {
//            System.out.println(userDataSocket == null);
//            e.printStackTrace();
//        }
//        return 0;
//    }







    public void close()
    {
        if (userDataSocket != null)
        {
            userDataSocket.close();
        }
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getHostAddress()
    {
        return hostAddress.getHostAddress();
    }

    public void setHostAddress(InetAddress hostAddress)
    {
        this.hostAddress = hostAddress;
    }

    public int getPort()
    {
        return port;
    }

    public DatagramPacket getUserReceivePacket()
    {
        return userReceivePacket;
    }

    public InetAddress getInetAddress()
    {
        return userReceivePacket.getAddress();
    }

    public DatagramSocket getDatagramSocket()
    {
        return userDataSocket;
    }
    public int getUserPacketlen()
    {
        return userReceivePacket.getLength();
    }


    public byte[] getBuffer(){ return buffer;}
}
