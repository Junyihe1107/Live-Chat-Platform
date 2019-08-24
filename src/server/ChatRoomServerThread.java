package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import javax.swing.JTextArea;
import server.ServerClientConnectionStream;
import user.UserInformation;

public class ChatRoomServerThread implements Runnable{
    private ServerClientConnectionStream userCS;
    private UserInformation userInfo;
    private Set<UserInformation> userSet;
    private Map<String, ServerClientConnectionStream> userMap;
    private StringBuffer userList;
    private JTextArea hintInfo;
    private JTextArea startInfoTextArea;
    private JTextArea onlineUserInfo;

    public ChatRoomServerThread(ServerClientConnectionStream userCS, Set<UserInformation> userSet,
                                Map<String, ServerClientConnectionStream> userMap, JTextArea hintInfo, JTextArea startInfoTextArea,
                                JTextArea onlineUserInfo)
    {
        super();
        this.userCS = userCS;
        this.userSet = userSet;
        this.userMap = userMap;
        this.hintInfo = hintInfo;
        this.startInfoTextArea = startInfoTextArea;
        this.onlineUserInfo = onlineUserInfo;
        userList = new StringBuffer();
        userInfo = new UserInformation();
    }
    @Override
    public void run()
    {
        while (true)
        {
            String message = userCS.read();
            if (message.indexOf("%LOGIN%") == 0)
            {
                userInfo.setInfo(message);
                // 标志 IP 端口号 姓名 账号 密码
                if (userMap.containsKey(userInfo.getAccount()))
                {
                    userCS.send("LOGIN_FIAL");
                } else if (userInfo.getName().equals(""))
                {
                    userCS.send("NAME_IS_NULL");
                } else
                {
                    userMap.put(userInfo.getAccount(), userCS);
                    userSet.add(userInfo);
                    userCS.send("LOGIN_SUCESSFULLY");
                    startInfoTextArea.append("客户端账号：" + userInfo.getAccount() + "登录服务器登录成功！\n");
                    userList.append(userInfo.getAccount() + "&LOGIN:");
                    sendCurrentUserList();
                }
            } else if (message.indexOf("%EXIT%") == 0)
            {
                startInfoTextArea.append("服务器收到客户端账号：" + userInfo.getAccount() + "的退出请求...\n");
                userMap.remove(userInfo.getAccount());
                userSet.remove(userInfo);
                userList.append(userInfo.getAccount() + "&EXIT:");
                sendCurrentUserList();
                startInfoTextArea.append("已向客户端账号：" + userInfo.getAccount() + "发送了用户列表消息！\n");
                break;
            }
        }
        if (userCS != null)
        {
            userCS.close();
        }
    }

    public void sendCurrentUserList()
    {
        System.out.println("正在向客户端发送用户列表消息...");
        onlineUserInfo.setText("");
        for (UserInformation userInfo : userSet)
        {
            onlineUserInfo.append("账号：" + userInfo.getAccount() + ",密码：" + userInfo.getPassword() + ",昵称："
                    + userInfo.getName() + ",IP地址：" + userInfo.getIP() + ",端口号(UDP)：" + userInfo.getPort() + "\n");
            userList.append(userInfo.getIP() + "-" + userInfo.getPort() + "-" + userInfo.getAccount() + "-"
                    + userInfo.getName() + "-" + userInfo.getUserPortraitNum() + ",");
        }

        for (String string : userMap.keySet())
        {
            userMap.get(string).send("%USER_LIST%:" + userList);
            startInfoTextArea.append("服务器向" + string + "发送的用户列表为：%USER_LIST%:" + userList + "\n");
        }
        startInfoTextArea.append("已向客户端账号：" + userInfo.getAccount() + "发送了用户列表消息！\n");
        userList.delete(0, userList.length());
        printServerInfo();
        System.out.println("已更新了服务器状态信息！");
        startInfoTextArea.append("已更新了服务器状态信息！\n");
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
