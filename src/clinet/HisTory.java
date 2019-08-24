package clinet;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class HisTory extends JFrame {
    JTextArea HWind;
    JPanel HPanel;
    public HisTory(int number){
        HPanel=new JPanel();
        HWind=new JTextArea(20,40);
        this.add(HPanel);
        HPanel.add(HWind);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setBounds(100, 100, 500, 400);
        this.addWindowListener(new WL()); // 为页面的关闭按钮设置监听
        this.setTitle("历史消息");
        BufferedReader br = null;
        try {
            System.out.println("历史消息:   "+number);
            br = new BufferedReader(new FileReader("D:\\冬季学期\\JAVA\\tcp udp\\"+number+".txt"));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        StringBuffer sb=new StringBuffer();
        String line =null;
        try {
            while((line=br.readLine())!=null){
                sb.append(line);
                HWind.append(line);
                HWind.append("\n");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    class WL extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            dispose();
        }
    }
}

