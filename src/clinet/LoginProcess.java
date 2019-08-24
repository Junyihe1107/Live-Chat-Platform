package clinet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoginProcess extends JFrame{
    private static final long serialVersionUID = 1L;

    public LoginProcess(int number)
    {
        super();
        setTitle("登陆");
        ImageIcon image1 = new ImageIcon(getClass().getResource("qq.png"));
        JLabel imageLable = new JLabel(image1);
        //ImageIcon portraItImage = new ImageIcon(getClass().getResource("\\pictures\\portrait-" + number + ".jpg"));
        //portraItImage.setImage(portraItImage.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
        //JLabel portraItImageLable = new JLabel(portraItImage);
        JPanel mainLayout = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel jPanel_0 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel jPanel_1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        JPanel jPanel_2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JButton login = new JButton("正在登录...");
        login.setPreferredSize(new Dimension(170, 30));
        login.setForeground(Color.white);
        login.setBackground(new Color(30, 144, 255));
        jPanel_2.add(login);
        jPanel_0.add(imageLable);
        mainLayout.add(jPanel_0);
        //jPanel_1.add(portraItImageLable);
        JPanel jPanel_3 = new JPanel(new GridLayout(2, 1, 5, 5));
        jPanel_3.add(jPanel_1);
        jPanel_3.add(jPanel_2);
        mainLayout.add(jPanel_3);
        add(mainLayout);
        setSize(445, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int height = getHeight();
        int width = getWidth();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        setLocation(screenWidth - width / 2, screenHeight - height / 2);
        setVisible(true);
    }
}
