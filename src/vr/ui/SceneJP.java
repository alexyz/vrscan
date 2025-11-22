package vr.ui;

import vr.Game;
import vr.Render;
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
    private final JComboBox<Render.Renderer> renderCombo = new JComboBox<>(Render.Renderer.values());
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
        renderCombo.addItemListener(e -> setOpts());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(new JLabel("Renderer"));
        top.add(renderCombo);
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

        setOpts();
    }

    private void setOpts() {
        Set<Integer> dlSet = new TreeSet<>();
        for (String s : dlField.getText().split(",")) {
            if ((s = s.trim()).length() > 0) {
                dlSet.add(Integer.parseInt(s,16));
            }
        }
        Set<Integer> numSet = new TreeSet<>();
        for (String s : numField.getText().split(",")) {
            if ((s = s.trim()).length() > 0) {
                numSet.add(Integer.parseInt(s));

            }
        }
        Render.Opts opts = new Render.Opts();
        opts.xRot = getRadians(xSpin);
        opts.yRot = getRadians(ySpin);
        opts.zRot = getRadians(zSpin);
        opts.dispNum = numBox.isSelected();
        opts.numFilter = numSet;
        opts.dlFilter = dlSet;
        opts.render = (Render.Renderer) renderCombo.getSelectedItem();
        sceneJc.setOpts(opts);
    }

    private void spinChangeListener() {
        setOpts();
        //sceneJc.setRot(getRadians(xSpin), getRadians(ySpin), getRadians(zSpin));
    }

    private static float getRadians(JSpinner spin) {
        return (float) Math.toRadians(((SpinnerNumberModel) spin.getModel()).getNumber().floatValue());
    }

    private void dlFieldActionEvent() {
        setOpts();
//        Set<Integer> set = new TreeSet<>();
//        for (String s : dlField.getText().split(",")) {
//            if ((s = s.trim()).length() > 0) {
//                set.add(Integer.parseInt(s,16));
//            }
//        }
//        System.out.println("set dl filter " + set);
//        sceneJc.setDlFilter(set);
    }

    private void numBoxItemEvent() {
        setOpts();
        //sceneJc.setNumbers(numBox.isSelected());
    }

    private void numFieldActionEvent() {
        setOpts();
//        Set<Integer> numSet = new TreeSet<>();
//        for (String s : numField.getText().split(",")) {
//            if ((s = s.trim()).length() > 0) {
//                numSet.add(Integer.parseInt(s));
//
//            }
//        }
//        System.out.println("set number filter " + numSet);
//        sceneJc.setNumberFilter(numSet);
    }

    public void setScene(Scene s) {
        sceneJc.setScene(s);
    }

}
