package vr.ui;

import vr.m.M1;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class PolyCR extends DefaultTableCellRenderer {
    public PolyCR() {
        //
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel v = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        v.setHorizontalAlignment(JLabel.RIGHT);
        v.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        setBackground(Color.white);
        setForeground(Color.black);

        if (value instanceof Integer i) {
            if (i == 0) {
                setValue("");
            } else {
                setValue(Integer.toHexString(i));
            }
        } else if (value instanceof M1 m1) {
            setValue(m1.toHcStr());
        } else if (value instanceof Color c) {
            setBackground(c);
            setForeground(Color.white);
            setValue(String.format("%02x%02x%02x",c.getRed(),c.getGreen(),c.getBlue()));
        } else if (value == null) {
            setValue("");
        } else {
            setValue(Objects.toString(value));
        }

        return v;
    }
}
