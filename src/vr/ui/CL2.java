package vr.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class CL2 implements ChangeListener {

    private final Component c;
    private final ChangeListener l;

    public CL2(Component c, ChangeListener l) {
        this.c = c;
        this.l = l;
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        try {
            l.stateChanged(ce);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(c, ScanJF.wrapStr(e.toString()));
        }
    }
}
