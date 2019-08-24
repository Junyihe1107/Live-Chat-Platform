package server;

import java.util.Map;

import javax.swing.JList;
import javax.swing.JOptionPane;

import server.ServerClientConnectionStream;
import user.MyMap;

public class ReadMessageFromServerThread implements Runnable{
    private ServerClientConnectionStream userCS;
    private String onlineUserList[];
    private MyMap isOnlineMap;
    private JList<String> currentOnlineUserList;
    private volatile boolean stop = false;
    private MyMap isOpenMap;
    private Map<String, String> userInfoMap;
    private String loginName;

    public ReadMessageFromServerThread(ServerClientConnectionStream userCS,
                                       Map<String, String> userInfoMap, JList<String> currentOnlineUserList,
                                       MyMap isOnlineMap, MyMap isOpenMap) {
        super();
        this.userCS = userCS;
        this.userInfoMap=userInfoMap;
        this.isOnlineMap = isOnlineMap;
        this.isOpenMap = isOpenMap;
        this.currentOnlineUserList = currentOnlineUserList;
    }

    public void refreshGUIUserList() {
        StringBuffer t = new StringBuffer();
        System.out.println("在服务器--客户端线程中刷新用户界面！");
        for (String string : onlineUserList)
        {
            // 姓名 账号 IP-端口 头像序号--
            String per[] = string.split("-");
            if (!per[2].equals(loginName))
            {
                System.out.println(per[2]);
                isOnlineMap.put(per[2], false);
                isOpenMap.put(per[2], false);
            }
            t.append(per[3] + "（" + per[2]+ "）"+":");
            userInfoMap.put(per[2], string);
        }
        String tt[] = t.toString().split(":");
        currentOnlineUserList.setListData(tt);
    }

    @Override
    public void run()
    {
        while (!stop)
        {

            System.out.println("服务器--客户端读取消息线程已启动！");
            String message = userCS.read();
            if (message != null)
            {
                System.out.println("服务器--客户端读取消息线程已启动！" + message);
                String perMessage[] = message.split(":");
                loginName = perMessage[1];
                if ("%USER_LIST%".equals(perMessage[0]))
                {
                    onlineUserList = perMessage[2].split(",");
                    if ("LOGIN".equals(perMessage[1].split("&")[1]))
                    {
                        isOnlineMap.put(perMessage[1].split("&")[0], true);
                        isOpenMap.put(perMessage[1].split("&")[0], false);
                    } else if ("EXIT".equals(perMessage[1].split("&")[1]))
                    {
                            if (isOpenMap.getValue(perMessage[1].split("&")[0]) == true)
                            {
                            JOptionPane.showMessageDialog(null, "对方已离线！");
                        }
                        isOnlineMap.replace(perMessage[1].split("&")[0], false);
                        isOpenMap.replace(perMessage[1].split("&")[0], false);
                    }
                    refreshGUIUserList();
                }
            }

        }
    }

    public void stopMe() {
        stop = true;
    }
}
