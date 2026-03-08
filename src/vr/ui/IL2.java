package vr.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class IL2 implements ItemListener {

    private final Component c;
    private final ItemListener l;

    public IL2(Component c, ItemListener l) {
        this.c = c;
        this.l = l;
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        try {
            l.itemStateChanged(ie);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(c, ScanJF.wrapStr(e.toString()));
        }
    }
}
