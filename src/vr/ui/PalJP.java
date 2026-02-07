package vr.ui;

import vr.Polygons;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class PalJP extends JPanel {

    private final JComponent[] panels = new JComponent[1024];
    private Polygons p;

    public PalJP() {
        super(new GridLayout(32, 32));
        Border b = BorderFactory.createLineBorder(Color.black);
        for (int n = 0; n < panels.length; n++) {
            JPanel p = new JPanel(new GridLayout(1,1));
            JLabel l = new JLabel(Integer.toHexString(n));
            p.add(l);
            p.setBorder(b);
            panels[n] = p;
            add(p);
        }
    }

    public void setPolgons(Polygons p) {
        this.p = p;
        for (int n = 0; n < p.colors.length; n++) {
            panels[n].setBackground(p.colors[n]);
            panels[n].setToolTipText(String.format("n=%x col=%x", n, p.palette[n] & 0xffff));
        }
        repaint();
    }

}
