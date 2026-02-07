package vr.ui;

import vr.DL;
import vr.Poly;
import vr.Polygons;
import vr.Scene;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public class DLJP2 extends JPanel {

    private static class DLCI {
        public final DL dl;

        public DLCI(DL dl) {
            this.dl = dl;
        }

        @Override
        public String toString() {
            return String.format("DL %x (%d polys)", dl.offset, dl.polys.size());
        }
    }

    private final JComboBox<DLCI> dlCombo = new JComboBox<>();
    private final JTextPane area = new JTextPane();

    public DLJP2() {
        super(new BorderLayout());
        dlCombo.addItemListener(new DLIL());
        area.setEditable(false);
        //area.setWrapStyleWord(false);
        area.setFont(ScanJF.MONO);

        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("DL"));
        top.add(dlCombo);
        JScrollPane areaPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(top, BorderLayout.NORTH);
        add(areaPane, BorderLayout.CENTER);
    }

    public void setPolygons(Polygons p) {
        StyledDocument doc = area.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        for (int n = 0; n < p.colors.length; n++) {
            String name = Integer.toHexString(p.palette[n] & 0xffff);
            if (doc.getStyle(name) == null) {
                Style s = doc.addStyle(name, defaultStyle);
                StyleConstants.setForeground(s, Color.white);
                StyleConstants.setBackground(s, p.colors[n]);
                //System.out.println("set color: " + name + " style: " + s);
            }
        }
    }


    public void setScene(Scene s) {
        Vector<DLCI> list = new Vector<>();
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

        try {
            StyledDocument doc = area.getStyledDocument();
            doc.remove(0, doc.getLength());
            for (int n = 0; n < dl.polys.size(); n++) {
                Poly p = dl.polys.get(n);
                String name = Integer.toHexString(p.col & 0xffff);
                Style style = doc.getStyle(name);
                //System.out.println("get color: " + name + " style: " + style);
                doc.insertString(doc.getLength(), String.format("%-3d: %s\n", n, p), style);
            }
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        area.setCaretPosition(0);
        repaint();
    }
}
