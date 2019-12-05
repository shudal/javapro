package com.qst.dms.ui.dialog;

import java.awt.*;
import javax.swing.*;
public class TipDialog {
    public static void showTip(String title, String content) {
        JFrame jf = new JFrame(title);
        jf.setSize(250, 100);
        jf.setLocationRelativeTo(null);
        JLabel jl = new JLabel(content);
        jf.add(jl);

        jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        jf.setVisible(true);

    }
}
