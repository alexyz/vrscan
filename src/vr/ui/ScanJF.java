package vr.ui;

import vr.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    private static Preferences PREFS = Preferences.userNodeForPackage(ScanJF.class);

    public static void main(String[] args) {
        ScanJF f = new ScanJF();
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        SwingUtilities.invokeLater(() -> f.wrapEx(() -> f.initRoms()));
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
    private final JComboBox<Game> gameCombo = new JComboBox<>(Game.values());
    private final JComboBox<SCI> sceneCombo = new JComboBox<>();
    private final JTextField dirField = new JTextField(32);
    private final JSpinner startSpin = new JSpinner();
    private final JSpinner lenSpin = new JSpinner();
    private Polygons polys;
    private Roms roms;

    public ScanJF() {
        setPreferredSize(new Dimension(1280, 960));
        setTitle("VrScan");
        setLayout(new BorderLayout());
        // [romfile] [load] [proj] [trans] [scale]
        tabs.addTab("View", sceneJp);
        tabs.addTab("DL", listsJp);
        sceneCombo.addItemListener(e -> sceneChangeIL());
        dirField.setText(PREFS.get("romDir", System.getProperty("user.dir")));
        dirField.addActionListener(e -> wrapEx(() -> initRoms()));
        startSpin.addChangeListener(e -> wrapEx(() -> customScene()));
        lenSpin.addChangeListener(e -> wrapEx(() -> customScene()));
        gameCombo.setSelectedItem(Game.vr);
        gameCombo.addItemListener(e -> wrapEx(() -> initScenes()));

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
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    public void wrapEx(Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, wrapStr(e.toString()));
        }
    }

    private void initRoms() {
        System.out.println("init roms");
        File romDir = new File(dirField.getText());
        roms = new Roms(romDir);
        PREFS.put("romDir", romDir.getAbsolutePath());
        initScenes();
    }

    private void initScenes() {
//        try {
        System.out.println("init scenes");
        Game game = (Game) gameCombo.getSelectedItem();

        try {
            polys = new Polygons().load(roms, game);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("polys=" + polys);

        startSpin.setModel(new SpinnerNumberModel(0, 0, polys.displayLists.size(), 1));
        lenSpin.setModel(new SpinnerNumberModel(1, 1, polys.displayLists.size(), 1));

        Vector<SCI> sceneItems = new Vector<>();
        if (game == Game.vr) {
            World world = new World(polys);
            Arrays.asList(world.bf(), world.ap(), world.bb(), world.p()).stream().forEach(s -> sceneItems.add(new SCI(s)));
        }
        sceneItems.add(new SCI(null));

        sceneCombo.setModel(new DefaultComboBoxModel<SCI>(sceneItems));
        sceneChangeIL();

//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, wrap(e.toString()));
//        }
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
        repaint();
    }

    private void sceneChangeIL() {
//        try {
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
//        } catch (Exception e2) {
//            e2.printStackTrace();
//            JOptionPane.showMessageDialog(this, wrap(e2.toString()));
//        }
    }
}

