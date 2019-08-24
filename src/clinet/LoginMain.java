package clinet;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

public class LoginMain {
    public static void main(String[] args) {
        //隐藏设置按钮
        try {
            //设置本属性将改变窗口边框样式定义
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            //TODO exception
        }
        new LoginFrame();
    }
}
