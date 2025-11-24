package vr.ui;

import vr.Polygons;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class PalJP extends JPanel {

    private final JPanel[] panels = new JPanel[1024];
    private Polygons p;

    public PalJP() {
        super(new GridLayout(32, 32));
        Border b = BorderFactory.createLineBorder(Color.black);
        for (int n = 0; n < panels.length; n++) {
            JPanel p = new JPanel(new GridLayout(1,1));
            p.setBorder(b);
            panels[n] = p;
            add(p);
        }
    }

    public void setPolgons(Polygons p) {
        this.p = p;
        for (int n = 0; n < p.colors.length; n++) {
            panels[n].setBackground(p.colors[n]);
            panels[n].setToolTipText("n=" + n + " col=" + Integer.toHexString(p.palette[n] & 0x7fff));
        }
        repaint();
    }

}
