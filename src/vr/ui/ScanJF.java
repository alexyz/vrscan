package vr.ui;

import vr.DL;
import vr.Polygons;
import vr.Roms;
import vr.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

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

    public static void main(String[] args) throws Exception {

        File romDir = new File("E:\\MYBOOK\\software\\mame0279\\roms\\vr");
        //String[] romFiles = new String[]{"mpr-14890.26", "mpr-14891.27"};
        //byte[] roms = Roms.loadRom(romDir, romFiles);
        byte[] roms = Roms.loadPolygons(romDir);
        int[] romWords = Roms.swap32(roms);
        List<DL> lists = Polygons.loadLists(romWords);
        System.out.println("paralists.size=" + lists.size());

        Scene bf = new Scene(Polygons.T1_BF);
        bf.lists.addAll(lists.stream().filter(l -> l.position >= Polygons.T1_BF && l.position < Polygons.T2_AP).toList());
        Scene ap = new Scene(Polygons.T2_AP);
        ap.lists.addAll(lists.stream().filter(l -> l.position >= Polygons.T2_AP && l.position < Polygons.T3_BB).toList());
        Scene bb = new Scene(Polygons.T3_BB);
        bb.lists.addAll(lists.stream().filter(l -> l.position >= Polygons.T3_BB && l.position < Polygons.T_END).toList());


        ScanJF f = new ScanJF();
        f.setScenes(Arrays.asList(bf, ap, bb));
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private final SceneJP sceneJp = new SceneJP();
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    private final DLPanel listsJp = new DLPanel();
    private final JComboBox<SCI> sceneCombo = new JComboBox<>();

    public ScanJF() {
        setPreferredSize(new Dimension(1280,960));
        setTitle("VrScan");
        setLayout(new BorderLayout());
        // [romfile] [load] [proj] [trans] [scale]
        tabs.addTab("View", sceneJp);
        tabs.addTab("DL", listsJp);
        sceneCombo.addItemListener(new SCIL());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(new JLabel("Scene"));
        top.add(sceneCombo);
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void setScenes(List<Scene> scenes) {
        sceneCombo.setModel(new DefaultComboBoxModel<SCI>(scenes.stream().map(s -> new SCI(s)).toArray(i -> new SCI[i])));
        setScene(scenes.get(0));
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

