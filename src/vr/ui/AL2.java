package vr.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AL2 implements ActionListener {

    private final Component c;
    private final ActionListener l;

    public AL2(Component c, ActionListener l) {
        this.c = c;
        this.l = l;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            l.actionPerformed(ae);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(c, ScanJF.wrapStr(e.toString()));
        }
    }
}
