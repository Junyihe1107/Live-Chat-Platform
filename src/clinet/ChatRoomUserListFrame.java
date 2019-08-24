package clinet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import server.ReadMessageFromServerThread;
import clinet.ClientToClientConnectionStream;
import server.ServerClientConnectionStream;
import user.UserInformation;
import user.MyMap;

public class ChatRoomUserListFrame extends JFrame
{

    /**
     * @author CSJ
     */
    private static final long serialVersionUID = 2L;
    private MyMap isOnlineMap;
    private MyMap isOpenMap;
    private ServerClientConnectionStream userCS;
    private JList<String> currentOnlineUserList;
    private Map<String, String> userInfoMap;
    private DatagramSocket dataSocket;
    private UserInformation userInfo;
    private ReadMessageFromServerThread readMessageFromServerThread;
    private Thread readMessagehread;
    private ChatRoomClientFrame chatRoomClientFrame;
    private Map<String, ChatRoomClientFrame> chatRoomMap = new HashMap<String, ChatRoomClientFrame>();

    public ChatRoomUserListFrame(ServerClientConnectionStream userCS, UserInformation userInfo) throws HeadlessException
    {
        super("好友列表");
        this.userCS = userCS;
        this.userInfo = userInfo;
        initData();
        createFrame();
        addEventHandler();
        autoListening();
    }

    private void initData()
    {
        isOnlineMap = new MyMap();
        isOpenMap = new MyMap();
        userInfoMap=new HashMap<String, String>();
        try
        {
            dataSocket = new DatagramSocket(userInfo.getPort());
        } catch (SocketException e)
        {
            e.printStackTrace();
        }
    }

    private void createFrame()
    {
        currentOnlineUserList = new JList<String>();
        currentOnlineUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //ImageIcon portraItImage = new ImageIcon(
        //       getClass().getResource("\\pictures\\portrait-" + userInfo.getUserPortraitNum() + ".jpg"));
        //portraItImage.setImage(portraItImage.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
        // JLabel portraItImageLable = new JLabel(portraItImage);
        JScrollPane listScrollPane = new JScrollPane(currentOnlineUserList);
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        // northPanel.add(portraItImageLable, BorderLayout.NORTH);
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 2, 5, 10));
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        JLabel userInfoLable = new JLabel(hour < 6 ? "凌晨好，" + userInfo.getName()
                : (hour < 12 ? "上午好，" : (hour < 18 ? "下午好，" : "晚上好，")) + userInfo.getName());
        userInfoPanel.add(userInfoLable);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getInstance();
        simpleDateFormat.applyPattern("yyyy年MM月dd日    E");
        JLabel userTimeLable = new JLabel(simpleDateFormat.format(new Date()));
        userInfoPanel.add(userTimeLable);
        northPanel.add(userInfoPanel);
        centerPanel.add(new JLabel("在线用户列表："), BorderLayout.CENTER);
        centerPanel.add(listScrollPane, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int width = getWidth();
        int screenWidth = screenSize.width / 2;
        setLocation(screenWidth - width / 2, 0);
        setResizable(false);
    }

    public void autoListening() {
        final ClientToClientConnectionStream userDataSoketCS = new ClientToClientConnectionStream(dataSocket);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String string = userDataSoketCS.read();
                    System.out.println("string::" + string);
                    System.out.println("自动监听读取到：" + string + "端口号：" + userDataSoketCS.getUserReceivePacket().getPort());
                    if (!"%TEST%".equals(string) && !"I_HAVE_EXIT_THE_WINDOW".equals(string) ) {
                        System.out.println("进了if循环xxxxxxxxx");
                        String tString[] = string.split("-");
                        if (isOpenMap.getValue(tString[1]) == true) {
                            chatRoomMap.get(tString[1]).getUserDataCS()
                                    .setHostAddress(userDataSoketCS.getUserReceivePacket().getAddress());
                            chatRoomMap.get(tString[1]).getUserDataCS()
                                    .setPort(userDataSoketCS.getUserReceivePacket().getPort());
                            chatRoomMap.get(tString[1]).getUserDataCS().send("%TEST%");
                            chatRoomMap.get(tString[1]).getChatTextArea().append(string + "\n");
                        } else {
                            System.out.println("hhhhxxxx进了if循环");
                            int t = JOptionPane.showConfirmDialog(null, tString[0] + "给您发来一条消息，是否查收？", "确认",
                                    JOptionPane.YES_NO_OPTION);
                            if (t == JOptionPane.YES_OPTION) {
                                UserInformation toUserInfo = new UserInformation(tString[0], tString[1],
                                        userDataSoketCS.getUserReceivePacket().getAddress().getHostAddress(),
                                        userDataSoketCS.getUserReceivePacket().getPort(), Integer.parseInt(tString[2]),
                                        tString[3], Integer.parseInt(tString[4]));
                                chatRoomClientFrame = new ChatRoomClientFrame(toUserInfo, userInfo, isOpenMap).showMe();
                                StringBuffer tMessage = new StringBuffer();
                                int indexf = tString[5].indexOf("\n");
                                String time = tString[5].substring(0, indexf).trim();
                                String all_msgsf = tString[5].substring(indexf).trim();
                                String real_msgsf = all_msgsf.substring(7).trim();
                                tMessage.append("\n");
                                tMessage.append(real_msgsf);
                                chatRoomClientFrame.getChatTextArea().append(tString[0] + time + tMessage + "\n");
                                FileWriter writer;
                                String cont = tString[0] + time + tMessage + "\r\n";
                                try {
                                    writer = new FileWriter("D:\\冬季学期\\JAVA\\最终版" +"\\"+userInfo.getName()+"\\"+ userInfo.getName() + ".txt", true);
                                    writer.write(cont);
                                    writer.flush();
                                    writer.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    isOpenMap.replace(tString[1], true);
                                    chatRoomMap.put(tString[1], chatRoomClientFrame);
                                }
                                System.out.println(toUserInfo.getPort());
                            }
                        }
                    }
                }
            }
        }).start();
    }


    private void addEventHandler()
    {
        currentOnlineUserList.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    String tString = (String) currentOnlineUserList.getSelectedValue();
                    System.out.println(tString);
                    if (tString != null)
                    {
                        String t[] = tString.split("（");
                        String[] string = t[1].split("）");
                        // 姓名-账号-IP-端口
                        if (string[0].equals(userInfo.getAccount()))
                        {
                            JOptionPane.showMessageDialog(null, "不能选择自己！");
                        } else
                        {
                            String tempUserInfo[]= userInfoMap.get(string[0]).split("-");
                            UserInformation toUserInfo = new UserInformation(tempUserInfo[3], tempUserInfo[2], tempUserInfo[0],
                                    Integer.parseInt(tempUserInfo[1]), Integer.parseInt(tempUserInfo[4]), tempUserInfo[0],
                                    Integer.parseInt(tempUserInfo[1]));
                            chatRoomClientFrame = new ChatRoomClientFrame(toUserInfo, userInfo, isOpenMap);
                            chatRoomClientFrame.showMe();
                            chatRoomMap.put(tempUserInfo[1], chatRoomClientFrame);
                            isOpenMap.replace(tempUserInfo[1], true);
                        }
                    }
                }

            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int t = JOptionPane.showConfirmDialog(null, "确认要退出客户端吗？\n温馨提示：退出会同时关闭所有聊天窗口", "确认退出",
                        JOptionPane.OK_CANCEL_OPTION);
                if (t == JOptionPane.OK_OPTION)
                {
                    readMessagehread.interrupt();
                    readMessageFromServerThread.stopMe();
                    userCS.send("%EXIT%:" + userInfo.getAccount());
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    userCS.send("%EXIT%:" + userInfo.getAccount());
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
    }

    public void showMe()
    {
        setSize(340, 700);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        readMessageFromServerThread = new ReadMessageFromServerThread(userCS,  userInfoMap, currentOnlineUserList,
                isOnlineMap, isOpenMap);
        readMessagehread = new Thread(readMessageFromServerThread);
        readMessagehread.start();
    }
}
