package vr.ui;

import vr.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class ScanJF extends JFrame {

    private static class SCI {
        public final Scene s;

        public SCI(Scene s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return String.format("Scene %x (%d lists)", s.position, s.lists.size());
        }
    }

    public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static Preferences PREFS = Preferences.userNodeForPackage(ScanJF.class);

    public static void main(String[] args) throws Exception {


        ScanJF f = new ScanJF();
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        SwingUtilities.invokeLater(() -> f.initScenes());
    }

    private final SceneJP sceneJp = new SceneJP();
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    private final DLPanel listsJp = new DLPanel();
    private final JComboBox<SCI> sceneCombo = new JComboBox<>();
    private final JTextField dirField = new JTextField(32);
    private final JButton loadButton = new JButton("Load");

    public ScanJF() {
        setPreferredSize(new Dimension(1280, 960));
        setTitle("VrScan");
        setLayout(new BorderLayout());
        // [romfile] [load] [proj] [trans] [scale]
        tabs.addTab("View", sceneJp);
        tabs.addTab("DL", listsJp);
        sceneCombo.addItemListener(new SCIL());
        dirField.setText(PREFS.get("romDir", System.getProperty("user.dir")));
        loadButton.addActionListener(e -> initScenes());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(new JLabel("Dir"));
        top.add(dirField);
        top.add(loadButton);
        top.add(new JLabel("Scene"));
        top.add(sceneCombo);
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void initScenes() {
        File romDir = new File(dirField.getText());
        if (romDir.isDirectory()) {
            try {
                byte[] polyBytes = Roms.loadPolygons(romDir);
                int[] polyWords = Roms.swap32(polyBytes);
                List<DL> dls = Polygons.loadDisplayLists(polyWords);
                System.out.println("dls.size=" + dls.size());

                byte[] cpu1Bytes = Roms.loadMainCpu1(romDir);
                int[] cpu1Words = Roms.swap32(cpu1Bytes);
                List<PA> pas = Polygons.loadPolyAddrs(cpu1Words);

                System.out.println("pas.size=" + pas.size());
                for (int n = 0; n < Math.min(pas.size(), 32); n++) {
                    System.out.println(String.format("pa[%d]=%s",n,pas.get(n)));
                }

                for (DL dl : dls) {
                    int exPa = (dl.position + 16) / 4;
                    dl.pa = pas.stream().filter(pa -> pa.polyAddr == exPa).findFirst().orElse(null);
                }

                Scene bf = new Scene(Polygons.T1_BF);
                bf.lists.addAll(dls.stream().filter(l -> l.position >= Polygons.T1_BF && l.position < Polygons.T2_AP).toList());

                Scene ap = new Scene(Polygons.T2_AP);
                ap.lists.addAll(dls.stream().filter(l -> l.position >= Polygons.T2_AP && l.position < Polygons.T3_BB).toList());

                Scene bb = new Scene(Polygons.T3_BB);
                bb.lists.addAll(dls.stream().filter(l -> l.position >= Polygons.T3_BB && l.position < Polygons.T_END).toList());

                List<Scene> scenes = Arrays.asList(bf, ap, bb);
                sceneCombo.setModel(new DefaultComboBoxModel<SCI>(scenes.stream().map(s -> new SCI(s)).toArray(i -> new SCI[i])));
                setScene(scenes.get(0));

                PREFS.put("romDir", romDir.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), e.toString(), "init scenes", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "not a directory", "init scenes", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setScene(Scene s) {
        sceneJp.setScene(s);
        listsJp.setScene(s);
        repaint();
    }

    private class SCIL implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getItem() instanceof SCI ci) {
                setScene(ci.s);
            }
        }
    }
}

