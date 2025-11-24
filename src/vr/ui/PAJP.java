package vr.ui;

import vr.*;

import javax.swing.*;
import java.awt.*;

public class PAJP extends JPanel {

    private final JTextArea area = new JTextArea();
    private Polygons p;

    public PAJP() {
        super(new BorderLayout());
        area.setEditable(false);
        area.setWrapStyleWord(false);
        area.setFont(ScanJF.MONO);

        JScrollPane areaPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(areaPane, BorderLayout.CENTER);
    }


    public void setPolgons(Polygons p) {
        this.p = p;
        StringBuilder sb = new StringBuilder();

            sb.append(p).append("\n");
            for (int n = 0; n < p.polyAddrs.size(); n++) {
                PA pa = p.polyAddrs.get(n);
                sb.append(String.format("%-4d: %s\n", n, pa));
            }

        area.setText(sb.toString());
        area.setCaretPosition(0);
        repaint();
    }

}
