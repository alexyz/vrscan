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
    private final JCheckBox dlBox = new JCheckBox("DL");

    public SceneJP() {
        super(new BorderLayout());

        numBox.addItemListener(e -> setOpts());
        numField.setFont(ScanJF.MONO);
        numField.setColumns(24);
        numField.addActionListener(e -> setOpts());
        dlField.setFont(ScanJF.MONO);
        dlField.setColumns(24);
        dlField.addActionListener(e -> setOpts());
        xSpin.addChangeListener(e -> setOpts());
        ySpin.addChangeListener(e -> setOpts());
        zSpin.addChangeListener(e -> setOpts());
        renderCombo.addItemListener(e -> setOpts());
        dlBox.setSelected(true);
        dlBox.addItemListener(e -> setOpts());

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
        top.add(dlBox);

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
        opts.dispDl = dlBox.isSelected();
        sceneJc.setOpts(opts);
    }

    private static float getRadians(JSpinner spin) {
        return (float) Math.toRadians(((SpinnerNumberModel) spin.getModel()).getNumber().floatValue());
    }

    public void setScene(Scene s) {
        sceneJc.setScene(s);
    }

}
