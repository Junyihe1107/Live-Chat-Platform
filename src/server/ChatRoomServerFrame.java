package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import server.ServerClientConnectionStream;
import user.UserInformation;


public class ChatRoomServerFrame extends JFrame{
    private static final long serialVersionUID = 1L;
    private Map<String, ServerClientConnectionStream> userMap;
    private Set<UserInformation> userSet;
    private ServerSocket serverSocket;
    private JButton start;
    private JLabel welcome;
    private JTextArea hintInfo;
    private JTextArea onlineUserInfo;
    private JTextArea startInfoTextArea;

    public ChatRoomServerFrame()
    {
        super();
        userMap = new HashMap<String, ServerClientConnectionStream>();
        userSet = new HashSet<UserInformation>();
        welcome = new JLabel("服务器端");
        hintInfo = new JTextArea("当前状态：服务器未启动！\n");
        startInfoTextArea = new JTextArea();
        startInfoTextArea.setEditable(false);
        hintInfo.setEditable(false);
        onlineUserInfo = new JTextArea();
        onlineUserInfo.setEditable(false);
        createFrame();
        addEventHandler();
    }

    private void createFrame()
    {
        start = new JButton("启动服务器");
        JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        JScrollPane hintPanel = new JScrollPane(hintInfo);
        JScrollPane onlineUserInfoPanel = new JScrollPane(onlineUserInfo);
        JScrollPane startInfoScrollPanel = new JScrollPane(startInfoTextArea);
        JPanel welcomePanel = new JPanel();
        welcomePanel.add(welcome);
        JPanel southPanel = new JPanel();
        southPanel.add(start);
        jTabbedPane.add("状态", hintPanel);
        jTabbedPane.add("运行详情", startInfoScrollPanel);
        jTabbedPane.add("在线用户详情", onlineUserInfoPanel);
        setSize(400, 600);
        setVisible(true);
        add(welcomePanel, BorderLayout.NORTH);
        add(jTabbedPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int height = getHeight();
        int width = getWidth();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        setLocation(screenWidth - width / 2, screenHeight - height / 2);
    }

    public void addEventHandler()
    {
        start.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                try
                {
                    serverSocket = new ServerSocket(9000);
                    new Thread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            startServer();
                        }
                    }).start();
                    JOptionPane.showMessageDialog(null, "服务器已启动！");
                } catch (IOException e)
                {
                    JOptionPane.showMessageDialog(null, "服务器已启动！");
                    e.printStackTrace();
                }
            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int t = JOptionPane.showConfirmDialog(null, "确认要退出服务器吗？", "确认退出", JOptionPane.OK_CANCEL_OPTION);
                if (t == JOptionPane.OK_OPTION)
                {
                    System.exit(0);
                }
            }
        });
    }
    private void startServer()
    {
        while (true)
        {
            try
            {
                System.out.println("开始监听客户端连接...");
                startInfoTextArea.append("开始监听客户端连接...\n");
                printServerInfo();
                Socket socket = serverSocket.accept();
                startInfoTextArea.append("已从客户端IP：" + socket.getInetAddress().getHostAddress() + ",端口号(TCP)："
                        + socket.getPort() + "接收到数据...\n");
                new Thread(new ChatRoomServerThread(new ServerClientConnectionStream(socket), userSet, userMap,
                        hintInfo, startInfoTextArea, onlineUserInfo)).start();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } // while
    }

    private void printServerInfo()
    {
        hintInfo.setText("当前状态：开始监听客户端连接...");
        try
        {
            hintInfo.append("\n服务器名称：" + InetAddress.getLocalHost().getHostName());
            hintInfo.append("\n服务器IP地址：" + InetAddress.getLocalHost().getHostAddress());
            hintInfo.append("\n监听端口：9000");
            hintInfo.append("\n当前在线人数：" + userMap.size());
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }
}
