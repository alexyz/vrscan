package vr.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public class CL {

    public static Component comp;

    public static ActionListener actionListener(ActionListener al) {
        return ae -> {
            try {
                al.actionPerformed(ae);
            } catch (Exception e) {
                handleEx(e);
            }
        };
    }

    public static ChangeListener changeListener(ChangeListener cl) {
        return ce -> {
            try {
                cl.stateChanged(ce);
            } catch (Exception e) {
                handleEx(e);
            }
        };
    }

    public static ItemListener itemListener(ItemListener il) {
        return ie -> {
            try {
                il.itemStateChanged(ie);
            } catch (Exception e) {
                handleEx(e);
            }
        };
    }

    public static Runnable runnable(Runnable r) {
        return () -> {
            try {
                r.run();
            } catch (Exception e) {
                handleEx(e);
            }
        };
    }

    private static void handleEx(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(comp, ScanJF.wrapStr(e.toString()));
    }

}