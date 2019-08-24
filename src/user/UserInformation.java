package user;

public class UserInformation {
    private String name;
    private String account;
    private String password;
    private String IP;
    private int port;
    private int recentPort;
    private String recenIP;
    private int userPortraitNum;

    public UserInformation()
    {
        super();
    }

    public UserInformation(String name, String account, String password, String IP, int port, int userPortraitNum)
    {
        super();
        this.name = name;
        this.account = account;
        this.password = password;
        this.IP = IP;
        this.port = port;
        this.userPortraitNum = userPortraitNum;
    }

    public UserInformation(String name, String account, String IP, int port, int userPortraitNum, String recentIP,
                           int recentPort)
    {
        super();
        this.name = name;
        this.account = account;
        this.IP = IP;
        this.port = port;
        this.recenIP = recentIP;
        this.recentPort = recentPort;
        this.userPortraitNum = userPortraitNum;
    }

    public int getRecentPort()
    {
        return recentPort;
    }

    public void setRecentPort(int recentPort)
    {
        this.recentPort = recentPort;
    }

    public String getRecenIP()
    {
        return recenIP;
    }

    public void setRecenIP(String recenIP)
    {
        this.recenIP = recenIP;
    }

    public UserInformation(String name, int port)
    {
        super();
        this.name = name;
        this.port = port;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String acocount)
    {
        this.account = acocount;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getIP()
    {
        return IP;
    }

    public void setIP(String iP)
    {
        IP = iP;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getUserPortraitNum()
    {
        return userPortraitNum;
    }

    public void setUserPortraitNum(int userPortraitNum)
    {
        this.userPortraitNum = userPortraitNum;
    }

    public UserInformation setInfo(String message)
    {
        // 标志 IP 端口号 姓名 账号 密码
        this.IP = message.split(":")[1];
        this.port = Integer.parseInt(message.split(":")[2]);
        this.name = message.split(":")[3];
        this.account = message.split(":")[4];
        this.password = message.split(":")[5];
        this.userPortraitNum = Integer.parseInt(message.split(":")[6]);
        return this;
    }
}
