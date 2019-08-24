package clinet;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.swing.*;

import clinet.ReadMessageFromClientThread;
import user.UserInformation;
import clinet.ClientToClientConnectionStream;
import user.MyMap;

public class ChatRoomClientFrame extends JFrame implements ActionListener{
    private static final long serialVersionUID = 2L;
    private JTextArea chatTextArea;
    private JButton send;
    private JButton sendf;
    private JButton History;
    private JTextArea inputField;
    private JLabel currentUserNameTitleLabel;
    private JTextArea userInfoListArea;
    private SimpleDateFormat simpleDateFormat;
    private UserInformation myUserInfo;
    private UserInformation toUserInfo;
    private JLabel userSignatureLable;
    private BufferedReader reader;
    private ClientToClientConnectionStream userDataCS;
    private ReadMessageFromClientThread readMessageFromClientThread;
    private Thread readMessageThread;
    private MyMap isOpenMap;
    private JPanel userInfoPanel;
    public ChatRoomClientFrame(UserInformation toUserInfo, UserInformation myUserInfo, MyMap isOpenMap)
            throws HeadlessException
    {
        super("正在与" + toUserInfo.getName() + "(" + toUserInfo.getAccount() + ")" + "聊天中...");
        this.toUserInfo = toUserInfo;
        this.myUserInfo = myUserInfo;
        this.isOpenMap = isOpenMap;
        DatagramSocket dataSocket = null;
        try
        {
            dataSocket = new DatagramSocket();

        } catch (SocketException e)
        {
            e.printStackTrace();
        }
        try
        {
            userDataCS = new ClientToClientConnectionStream(dataSocket, InetAddress.getByName(toUserInfo.getIP()),
                    toUserInfo.getPort());
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        userDataCS.send("%TEST%");
        simpleDateFormat = (SimpleDateFormat) DateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy年MM月dd日HH时mm分ss秒");
        createFrame();
        addEventHandler();
    }

    private void createFrame()
    {
        chatTextArea = new JTextArea(15, 30);
        chatTextArea.setEditable(false);
        JScrollPane centerScrollPane = new JScrollPane(chatTextArea);
        send = new JButton("发送");
        JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
        button.add(send);

        History = new JButton("历史消息");
        History.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HisTory(myUserInfo.getPort());
            }
        });
        sendf = new JButton("选择文件");
        sendf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser jChooser = new JFileChooser();
                jChooser.setDialogTitle("打开");
                int returnVal = jChooser.showOpenDialog(null);
                java.io.File file =jChooser.getSelectedFile();
                if (JFileChooser.APPROVE_OPTION == returnVal) {
                    String strPath = file.getAbsolutePath();
                    System.out.println(strPath);
                    File sendfile = new File(strPath);
                    System.out.println("strPath              "+strPath);
                    FileInputStream fis = null;
                    if (!sendfile.exists()) {
                        System.out.println("客户端：要发送的文件不存在");
                        return;
                    }
                    try {
                        fis = new FileInputStream(sendfile);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        System.out.println("fis.available()文件总大小："+sendfile.length());
                    }catch (Exception e1) {
                        e1.printStackTrace();
                    }


                    String time3 = simpleDateFormat.format(new Date());
                    if (userDataCS != null) {
                        try {
                            String x=String.valueOf(sendfile.length());
                            userDataCS.send(myUserInfo.getName() + "-" + myUserInfo.getAccount() + "-"
                                    + myUserInfo.getUserPortraitNum() + "-" + myUserInfo.getRecenIP() + "-"
                                    + myUserInfo.getRecentPort() + "-" + "(" + time3 + ")\n" + "%wenji%"+sendfile.getName()+ "/" + x);
                        }catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        inputField.setText("");
                    }
                    byte[] buf = new byte[1024];
                    int i = 0;
                    int m=0;
                    DatagramPacket userReceivePacket=null;
                    try {
                        while ((i = fis.read(buf, 0, 1024)) !=-1) {
                            m+=1;
                            System.out.println(i);
                            System.out.println("循环次数：  "+m);
                            String buffer = new String(buf,"UTF-8");
                            Thread.sleep(100);    //简单的防止丢包现象
                            userDataCS.send(buffer);

                        }
                        chatTextArea.append("我" + "(" + time3 + ")\n" + "文件已发送成功!" + "\n");


                        try {
                            if (fis != null) {
                                fis.close();
                            }
                        } catch (IOException e5) {
                            System.out.println("客户端文件关闭出错");
                        }//catch (IOException e)
                    } catch (Exception e1) {
                        e1.printStackTrace();

                    }
                }
            }
        });




        button.add(send);
        button.add(sendf);
        button.add(History);
        inputField = new JTextArea(1,60);
        currentUserNameTitleLabel = new JLabel(toUserInfo.getName() + "(" + toUserInfo.getAccount() + ")");
        currentUserNameTitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 25));
        Random random = new Random();

        ImageIcon image1 = new ImageIcon(getClass().getResource("man.jpg"));
        ImageIcon image2 = new ImageIcon(getClass().getResource("woman.jpg"));
        JLabel picture = new JLabel(image1);
        JLabel picture2 = new JLabel(image2);
        userInfoPanel = new JPanel(new GridLayout(2, 2, 5, 6));
        userInfoPanel.add(currentUserNameTitleLabel);


        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JPanel southPanel = new JPanel(new BorderLayout());
        northPanel.add(userInfoPanel);//对方信息
        southPanel.add(inputField,BorderLayout.CENTER);
        southPanel.add(button,BorderLayout.SOUTH);//按钮位置
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(picture2, BorderLayout.NORTH);//图片位置
        southPanel.add(picture, BorderLayout.EAST);
        add(eastPanel, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);
        add(centerScrollPane, BorderLayout.CENTER);//输出狂
        add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String message = "%wenzi%"+inputField.getText();
        System.out.println("myUserInfo的端口号   "+myUserInfo.getPort());
        // System.out.println("userDataCS.getPort()的端口号   "+userDataCS.getUserReceivePacket().getPort());
        System.out.println("toUserInfo的端口号   "+toUserInfo.getPort());
        if (message == null || message.trim().equals(""))
        {
            JOptionPane.showMessageDialog(this, "不能发送空消息！");
        } else
        {
            String time = simpleDateFormat.format(new Date());
            if (userDataCS != null)
            {
                userDataCS.send(myUserInfo.getName() + "-" + myUserInfo.getAccount() + "-"
                        + myUserInfo.getUserPortraitNum() + "-" + myUserInfo.getRecenIP() + "-"
                        + myUserInfo.getRecentPort() + "-" + "(" + time + ")\n" + message);
                message=message.substring(7).trim();
                String cont = "我" + "(" + time + ")\r\n" + message + "\r\n";

                FileWriter writer;
                try {
                    System.out.println("在写入了！");
                    writer = new FileWriter("D:\\冬季学期\\JAVA\\最终版\\" +myUserInfo.getPort () + ".txt", true);
                    writer.write(cont);
                    writer.flush();
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
//             }
                }
                chatTextArea.append("我" + "(" + time + ")\n" + message + "\n");
                inputField.setText("");
            }
        }
    }

    private void addEventHandler()
    {
        //inputField.addActionListener(this);
        send.addActionListener(this);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int t = JOptionPane.showConfirmDialog(null, "确认要退出聊天室吗？", "确认退出", JOptionPane.OK_CANCEL_OPTION);
                if (t == JOptionPane.OK_OPTION)
                {
                    userDataCS.send("I_HAVE_EXIT_THE_WINDOW");
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    readMessageThread.interrupt();
                    readMessageFromClientThread.stopMe();
                    isOpenMap.replace(toUserInfo.getAccount(), false);

                    System.out.println("isOpenMap.replace(toUserInfo.getAccount()?" + toUserInfo.getAccount() + "--》"
                            + isOpenMap.getValue(toUserInfo.getAccount()));
                    System.out.println("窗口已经关闭！");
                    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                }
            }
        });
    }

    public ChatRoomClientFrame showMe()
    {
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        readMessageFromClientThread = new ReadMessageFromClientThread(userDataCS, chatTextArea, toUserInfo);
        readMessageThread = new Thread(readMessageFromClientThread);
        readMessageThread.start();
        return this;
    }

    public JTextArea getChatTextArea()
    {
        return chatTextArea;
    }

    public void setChatTextArea(JTextArea chatTextArea)
    {
        this.chatTextArea = chatTextArea;
    }

    public ClientToClientConnectionStream getUserDataCS()
    {
        return userDataCS;
    }

    public void setUserDataCS(ClientToClientConnectionStream userDataCS)
    {
        this.userDataCS = userDataCS;
    }
}


