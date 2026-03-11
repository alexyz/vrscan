package vr.ui;

import vr.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

public class ScanJF extends JFrame {

    private static class SCI {
        public final Scene s;

        public SCI(Scene s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s != null ? String.format("Scene %x (%d lists)", s.position, s.dls.size()) : "Custom Scene";
        }
    }

    public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Preferences PREFS = Preferences.userNodeForPackage(ScanJF.class);

    public static void main(String[] args) {
        ScanJF f = new ScanJF();
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        CL.comp = f;

        SwingUtilities.invokeLater(() -> CL.runnable(() -> f.initRoms()).run());
    }

    public static String wrapStr(String msg) {
        StringBuilder sb = new StringBuilder();
        int l = 0;
        for (int n = 0; n < msg.length(); n++) {
            final char c = msg.charAt(n);
            sb.append(c);
            l++;
            if (c == '\n') {
                l = 0;
            }
            if ((l > 120 && !Character.isLetterOrDigit(c)) || l >= 160) {
                sb.append("\n");
                l = 0;
            }
        }
        return sb.toString();
    }

    private final SceneJP sceneJp = new SceneJP();
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    private final DLJP listsJp = new DLJP();
    private final DLJP2 listsJp2 = new DLJP2();
    private final DLTJP listsJp3 = new DLTJP();
    private final PAJP paJp = new PAJP();
    private final PalJP palJp = new PalJP();
    private final JComboBox<Game> gameCombo = new JComboBox<>(Game.values());
    private final JComboBox<SCI> sceneCombo = new JComboBox<>();
    private final JTextField dirField = new JTextField(32);
    private final JSpinner startSpin = new JSpinner();
    private final JSpinner lenSpin = new JSpinner();
    private final JLabel dlsLabel = new JLabel();
    private Polygons polys;
    private Roms roms;

    public ScanJF() {
        setPreferredSize(new Dimension(1280, 960));
        setTitle("VrScan");
        setLayout(new BorderLayout());
        // [romfile] [load] [proj] [trans] [scale]
        tabs.addTab("View", sceneJp);
        tabs.addTab("DL", listsJp);
        tabs.addTab("DL2", listsJp2);
        tabs.addTab("DL3", listsJp3);
        tabs.addTab("PA", paJp);
        tabs.addTab("Palette", palJp);
        sceneCombo.addItemListener(CL.itemListener(e -> sceneChange()));
        dirField.setText(PREFS.get("romDir", System.getProperty("user.dir")));
        dirField.addActionListener(CL.actionListener(e -> initRoms()));
        dirField.addActionListener(CL.actionListener(e -> initRoms()));
        startSpin.addChangeListener(CL.changeListener(e -> customScene()));
        lenSpin.addChangeListener(CL.changeListener(e -> customScene()));
        gameCombo.setSelectedIndex(PREFS.getInt("game", 0));
        gameCombo.addItemListener(CL.itemListener(e -> initScenes()));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(new JLabel("Dir"));
        top.add(dirField);
        top.add(new JLabel("Game"));
        top.add(gameCombo);
        top.add(new JLabel("Scene"));
        top.add(sceneCombo);
        top.add(new JLabel("DL"));
        top.add(startSpin);
        top.add(new JLabel("Len"));
        top.add(lenSpin);
        top.add(dlsLabel);
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private void initRoms() {
        System.out.println("init roms");
        File romDir = new File(dirField.getText());
        roms = new Roms(romDir);
        PREFS.put("romDir", romDir.getAbsolutePath());
        initScenes();
    }

    private void initScenes() {
        System.out.println("init scenes");
        Game game = (Game) gameCombo.getSelectedItem();
        PREFS.putInt("game", gameCombo.getSelectedIndex());

        polys = new Polygons().load(roms, game);
        dlsLabel.setText(String.valueOf(polys.displayLists.size()));
        System.out.println("polys=" + polys);

        paJp.setPolgons(polys);
        palJp.setPolgons(polys);
        listsJp2.setPolygons(polys);

        startSpin.setModel(new SpinnerNumberModel(0, 0, polys.displayLists.size(), 1));
        lenSpin.setModel(new SpinnerNumberModel(1, 1, polys.displayLists.size(), 1));
        Vector<SCI> sceneItems = new Vector<>();
        Scenes.scenes(game, polys).stream().forEach(s -> sceneItems.add(new SCI(s)));
        sceneItems.add(new SCI(null));
        sceneCombo.setModel(new DefaultComboBoxModel<SCI>(sceneItems));
        sceneChange();
    }

    private void customScene() {
        int start = getStartModel().getNumber().intValue();
        int len = getLenModel().getNumber().intValue();
        System.out.println("custom scene " + start + ", " + len);
        List<DL> subdls = polys.displayLists.subList(start, Math.min(polys.displayLists.size(), start + len));
        Scene custom = new Scene(subdls.get(0).offset);
        custom.dls.addAll(subdls);
        setScene(custom);
    }

    private SpinnerNumberModel getLenModel() {
        return (SpinnerNumberModel) lenSpin.getModel();
    }

    private SpinnerNumberModel getStartModel() {
        return (SpinnerNumberModel) startSpin.getModel();
    }

    private void setScene(Scene s) {
        System.out.println("set scene " + s);
        sceneJp.setScene(s);
        listsJp.setScene(s);
        listsJp2.setScene(s);
        listsJp3.setScene(s);
        repaint();
    }

    private void sceneChange() {
        SCI sci = (SCI) sceneCombo.getSelectedItem();
        System.out.println("scene change " + sci.s);
        if (sci.s != null) {
            setScene(sci.s);
            startSpin.setEnabled(false);
            lenSpin.setEnabled(false);
            getStartModel().setValue(sci.s.dls.get(0).ordinal);
            getLenModel().setValue(sci.s.dls.size());
        } else {
            customScene();
            startSpin.setEnabled(true);
            lenSpin.setEnabled(true);
        }
    }
}

