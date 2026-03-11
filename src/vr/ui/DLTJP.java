package vr.ui;

import vr.DL;
import vr.Scene;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public class DLTJP extends JPanel {

    private final JComboBox<DLCI> dlCombo = new JComboBox<>();
    private final JTable table = new JTable();

    public DLTJP() {
        super(new BorderLayout());
        dlCombo.addItemListener(new DLIL());
        table.setDefaultRenderer(Object.class,new PolyCR());
        table.setAutoCreateRowSorter(true);

        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("DL"));
        top.add(dlCombo);
        JScrollPane areaPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(top, BorderLayout.NORTH);
        add(areaPane, BorderLayout.CENTER);
    }


    public void setScene(Scene s) {
        Vector<DLCI> list = new Vector<>();
        list.add(new DLCI(null));
        s.dls.stream().map(l -> new DLCI(l)).forEach(i -> list.add(i));
        dlCombo.setModel(new DefaultComboBoxModel<>(list));
        dlCombo.getModel().setSelectedItem(list.get(0));
        updateArea(list.get(0).dl);
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
        if (dl != null) {
            table.setModel(new DLTM(dl));
        } else {
            table.setModel(new DefaultTableModel());
        }
        repaint();
    }
}
