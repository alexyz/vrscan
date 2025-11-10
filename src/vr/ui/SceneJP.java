package vr.ui;

import vr.Scene;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;

public class SceneJP extends JPanel {

    private final SceneJC sceneJc = new SceneJC();
    private final JCheckBox numBox = new JCheckBox("Number");
    private final JTextField numField = new JTextField();
    private final JTextField dlField = new JTextField();
    private final JSpinner xSpin = new JSpinner(new SpinnerNumberModel(0,-360,360,1));
    private final JSpinner ySpin = new JSpinner(new SpinnerNumberModel(0,-360,360,1));
    private final JSpinner zSpin = new JSpinner(new SpinnerNumberModel(0,-360,360,1));

    public SceneJP() {
        super(new BorderLayout());

        numBox.addItemListener(e -> numBoxItemEvent());
        numField.setFont(ScanJF.MONO);
        numField.setColumns(24);
        numField.addActionListener(e -> numFieldActionEvent());
        dlField.setFont(ScanJF.MONO);
        dlField.setColumns(24);
        dlField.addActionListener(e -> dlFieldActionEvent());
        xSpin.addChangeListener(e -> spinChangeListener());
        ySpin.addChangeListener(e -> spinChangeListener());
        zSpin.addChangeListener(e -> spinChangeListener());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(numBox);
        top.add(new JLabel("DLF"));
        top.add(dlField);
        top.add(new JLabel("NF"));
        top.add(numField);
        top.add(new JLabel("R"));
        top.add(xSpin);
        top.add(ySpin);
        top.add(zSpin);

        add(top, BorderLayout.NORTH);
        add(sceneJc, BorderLayout.CENTER);
    }

    private void spinChangeListener() {
        sceneJc.setRot(getRadians(xSpin), getRadians(ySpin), getRadians(zSpin));
    }

    private static double getRadians(JSpinner spin) {
        return Math.toRadians(((SpinnerNumberModel) spin.getModel()).getNumber().doubleValue());
    }

    private void dlFieldActionEvent() {
        Set<Integer> set = new TreeSet<>();
        for (String s : dlField.getText().split(",")) {
            if ((s = s.trim()).length() > 0) {
                set.add(Integer.parseInt(s,16));
            }
        }
        System.out.println("set dl filter " + set);
        sceneJc.setDlFilter(set);
    }

    private void numBoxItemEvent() {
        sceneJc.setNumbers(numBox.isSelected());
    }

    private void numFieldActionEvent() {
        Set<Integer> set = new TreeSet<>();
        for (String s : numField.getText().split(",")) {
            if ((s = s.trim()).length() > 0) {
                set.add(Integer.parseInt(s));

            }
        }
        System.out.println("set number filter " + set);
        sceneJc.setNumberFilter(set);
    }

    public void setScene(Scene s) {
        sceneJc.setScene(s);
    }

}
