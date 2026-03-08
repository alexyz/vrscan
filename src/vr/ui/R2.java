package vr.ui;

import javax.swing.*;
import java.awt.*;

public class R2 implements Runnable {

    private final Component c;
    private final Runnable l;

    public R2(Component c, Runnable l) {
        this.c = c;
        this.l = l;
    }

    @Override
    public void run() {
        try {
            l.run();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(c, ScanJF.wrapStr(e.toString()));
        }
    }
}
