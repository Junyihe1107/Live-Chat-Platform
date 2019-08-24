package clinet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JTextArea;
import clinet.ClientToClientConnectionStream;
import user.UserInformation;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ReadMessageFromClientThread implements Runnable {
    private ClientToClientConnectionStream userDataCS;
    private JTextArea chatTextArea;
    private UserInformation toUserInfo;
    private volatile boolean stop = false;

    public ReadMessageFromClientThread(ClientToClientConnectionStream userDataCS, JTextArea chatTextArea,
                                       UserInformation toUserInfo) {
        super();
        this.userDataCS = userDataCS;
        this.chatTextArea = chatTextArea;
        this.toUserInfo = toUserInfo;
        this.toUserInfo = toUserInfo;
    }

    @Override
    public void run() {
        while (!stop) {
            synchronized (this) {
                System.out.println("客户端--客户端读取消息线程已启动！");
                if (userDataCS != null) {
                    System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhh");
                    String message = userDataCS.read();
                    System.out.println("message::" + message);
                    if (message.equals("%TEST%")) {
                        System.out.println("客户端中原IP：" + userDataCS.getHostAddress() + "原端口：" + userDataCS.getPort());
                        userDataCS.setHostAddress(userDataCS.getUserReceivePacket().getAddress());
                        userDataCS.setPort(userDataCS.getUserReceivePacket().getPort());
                        System.out.println("客户端中现IP：" + userDataCS.getHostAddress() + "现在端口：" + userDataCS.getPort());
                    } else if ("I_HAVE_EXIT_THE_WINDOW".equals(message)) {
                        try {
                            userDataCS.setHostAddress(InetAddress.getByName(toUserInfo.getRecenIP()));
                            userDataCS.setPort(toUserInfo.getRecentPort());
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
//----------------------------------------------------判断传过去的是文字还是文件不同-------------------------------------------------------------
                    else {

//                        comm = comm.substring(index + 2);
//                        index = comm.indexOf("/#");
//                        String filename = comm.substring(0, index).trim();
//                        String filesize = comm.substring(index + 2).trim();
                        String tString[] = message.split("-");
                        int index = tString[5].indexOf("\n");
                        String all_msgs = tString[5].substring(index).trim();
                        String judge_msgs = all_msgs.substring(0, 7).trim();

                        System.out.println("all_msgs     " + all_msgs);
                        System.out.println("judge_msgs    " + judge_msgs);
                        SimpleDateFormat simpleDateFormat;
                        simpleDateFormat = (SimpleDateFormat) DateFormat.getInstance();
//                        simpleDateFormat.applyPattern("yyyy年MM月dd日HH时mm分ss秒");
//                        String time = simpleDateFormat.format(new Date());
//--------------------------------------------------------------文字接收-------------------------------------------------------------------------
                        if (judge_msgs.equals("%wenzi%")) {
                            StringBuffer tMessage = new StringBuffer();
                            int indexf = tString[5].indexOf("\n");
                            String time = tString[5].substring(0, indexf).trim();
                            String all_msgsf = tString[5].substring(indexf).trim();
                            String real_msgsf = all_msgsf.substring(7).trim();
                            tMessage.append("\n");
                            tMessage.append(real_msgsf);
                            chatTextArea.append(tString[0] + time + tMessage + "\n");
                        }
//--------------------------------------------------------------文件接收-------------------------------------------------------------------------
                        else if(judge_msgs.equals("%wenji%")) {

                            String real_msgs = all_msgs.substring(7).trim();
                            String astring[] = real_msgs.split("/");
                            int all_len = Integer.parseInt(astring[1]);
                            DatagramSocket serverSocket = null;                //socket
                            InetAddress ip = null;                               //ip
                            DatagramPacket getPacket = null;                  //get packet
                            DatagramPacket sendPacket = null;                 //send packet
                            FileOutputStream fos = null;                        //file
                            try {
                                String filename = astring[0];
                                /**创建空文件，用来进行接收文件*/
                                File file = new File(filename);
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        System.out.println("服务器端创建文件失败");
                                    }
                                } else {
                                    /**在此也可以询问是否覆盖*/
                                    System.out.println("本路径已存在相同文件，进行覆盖");
                                }
                                byte[] buf = new byte[1024];
                                fos = new FileOutputStream(file);
                                System.out.println("创建文件结束！");

                                System.out.println("输出buf长度    " + buf.length);
                                System.out.println("real_msgs的长度   " + real_msgs.getBytes().length);
                                int m = 0;
//--------------------------------------------------------------循环接受-------------------------------------------------------------------------
                                int a;
                                while (true) {
                                    if (all_len > 1024) {
                                        a = 1024;
                                    } else {
                                        a = all_len;
                                    }
                                    all_len = all_len - 1024;

                                    String len_a = userDataCS.read();
                                    fos.write(userDataCS.getBuffer(), 0, a);
                                    fos.flush();
                                    System.out.println("a的值是多少   " + a);
                                    if (a < 1024) {
                                        System.out.println("out！");
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (fos != null)
                                        fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }//catch (IOException e)
                            }
                        }
                    }
                }
            }
        }
    }



    public void stopMe()
    {
        stop = true;
    }
}

