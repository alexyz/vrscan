package vr.ui;

import vr.DL;
import vr.Para;
import vr.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DLPanel extends JPanel {

    private static class DLCI {
        public final DL dl;

        public DLCI(DL dl) {
            this.dl = dl;
        }

        @Override
        public String toString() {
            return String.format("DL %x (%d polys)", dl.position, dl.paras.size());
        }
    }

    private final JComboBox<DLCI> dlCombo = new JComboBox<>();
    private final JTextArea area = new JTextArea();

    public DLPanel() {
        super(new BorderLayout());
        dlCombo.addItemListener(new DLIL());
        area.setEditable(false);
        area.setWrapStyleWord(false);
        area.setFont(ScanJF.MONO);

        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("DL"));
        top.add(dlCombo);
        JScrollPane areaPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(top, BorderLayout.NORTH);
        add(areaPane, BorderLayout.CENTER);
    }


    public void setScene(Scene s) {
        DLCI[] list = s.lists.stream().map(l -> new DLCI(l)).toArray(i -> new DLCI[i]);
        dlCombo.setModel(new DefaultComboBoxModel<DLCI>(list));
        dlCombo.getModel().setSelectedItem(list[0]);
        updateArea(s.lists.get(0));
    }

    private class DLIL implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getItem() instanceof DLCI ci) {
                updateArea(ci.dl);
            } else {
                System.out.println("unknown item event " + e.getItem());
            }
        }
    }

    private void updateArea(DL dl) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < dl.paras.size(); n++) {
            Para p = dl.paras.get(n);
            sb.append(String.format("%-3d: %s", n, p.toString())).append("\n");
        }
        area.setText(sb.toString());
        area.setCaretPosition(0);
    }
}
