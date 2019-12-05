package com.qst.dms.ui;

import javax.swing.*;
import java.awt.*;

import com.qst.dms.ui.*;
import com.qst.dms.ui.dialog.*;
import com.qst.dms.service.*;
import com.qst.dms.entity.*;
public class LoginFrame {
    public JFrame loginFrame;
    private String frameTitle="登录";
    private int width=360, height=150;

    private int textFieldColumns = 16;
    public LoginFrame() {
        loginFrame = new JFrame(frameTitle);
        loginFrame.setSize(width, height);
        initFrame();
        loginFrame.setVisible(true);
    }
    private void initFrame() {
        JPanel mainBox = new JPanel(new GridLayout(3,1));

        JLabel unameJL = new JLabel("用  户  名：");
        JTextField unameTF = new JTextField(textFieldColumns);
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1.add(unameJL); p1.add(unameTF);mainBox.add(p1);

        JLabel passJL =  new JLabel("密        码：");
        JPasswordField passTF = new JPasswordField(textFieldColumns);
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2.add(passJL); p2.add(passTF);mainBox.add(p2);

        JButton btnLogin = new JButton("登录");
        btnLogin.addActionListener((e)->{
            UserService userService = new UserService();
            User user = userService.findUserByName(unameTF.getText());
            if (user != null) {
                if (!user.getPassword().equals(passTF.getText())) {
                    System.out.println("wrong pass");
                    TipDialog.showTip("失败", "密码错误");
                    return;
                }
                System.out.println("ok");
                MainFrametest2 mF = new MainFrametest2();
                loginFrame.setVisible(false);
            } else {
                System.out.println("failed");
                TipDialog.showTip("失败", "用户名不存在");
            }
        });
        JButton btnRest = new JButton("重置");
        btnRest.addActionListener((e)->{
            unameTF.setText("");
            passTF.setText("");
        });
        JButton btnRegist = new JButton("注册");
        btnRegist.addActionListener((e)->{
            RegistFrame.main(null);
        });
        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p3.add(btnLogin); p3.add(btnRest); p3.add(btnRegist);
        mainBox.add(p3);

        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.add(mainBox);
    }

}
