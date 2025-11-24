package vr.ui;

import vr.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

// todo a vertical size and a pager and much bigger offset and view to model and swap ...
// and rowwise/coloum wise, and read 1/2/3/4 bytes at a time (le or be) or at 5-6-5 colour
// reuse image if res hasn't changed
public class BinJF extends JFrame {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ScanJF.class);

    public static void main(String[] args) {
        //File romDir = new File(args[0]);
        //byte[] data = Roms.intsToBytesBe(Roms.swap32(Roms.loadPolygons(romDir)));

        // looks like faint images.
        // s~30 has number images, variable width
        // s 62 o 995,000 has VR image, larger one at s 128
        // s=74 has a faint BB image?
        // s=116 has a faint skyline image?
        // s=124 has VR image near end
        // s=156 and s=160 has very structured data
        // s=164 has a faint image
        // 917,504 s 16 has a clear look up table
        // E0000 (917504-969088) possible start of DL+TA
        // F2A90 (993936-end) possible start of colours (also tiles)
        //byte[] data = Roms.loadMainCpu1(romDir);

        // lots of small structure arrays
        // 18 has array
        // the "random" data does have faint structure at 16, 24, 28
        // this could be a polygon colour index table
        //byte[] data = Roms.loadMainCpu2(romDir);

        // obvious bitmaps
        // a lot of tiles at 32?
        // some strange images at s 124 o 928,000
        // possible grouping of track outline with background tiles
        // track shapes at 256, but shaded outline only. the track view is probably the "simple" 3d scenes and not these
        // o 2,097,152 = acro
        // o 2,883,584 - BF
        // each track has large 16 byte LUT -
        //byte[] data = Roms.loadMainCpu3(romDir);

        // 64 byte lookup tables?
        //byte[] data = Roms.intsToBytesBe(Roms.swap32(Roms.loadTgpData(romDir)));

        BinJF f = new BinJF();
        //f.setData(data);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        SwingUtilities.invokeLater(() -> f.loadRoms());
    }

    private final JTextField dirField = new JTextField(20);
    private final JComboBox<Game> gameBox = new JComboBox<>(Game.values());
    private final JComboBox<Bank> bankBox = new JComboBox<>(Bank.values());
    private final JSpinner sizeSpin = new JSpinner(new SpinnerNumberModel(32, 1, 1024000, 1));
    private final JSpinner offsetSpin = new JSpinner(new SpinnerNumberModel(0, -16777216, 16777216, 65536));
    private final JComboBox<BinJC.Opt.Format> formatBox = new JComboBox<>(BinJC.Opt.Format.values());
    private final JCheckBox swapBox = new JCheckBox("Swap", true);
    private final BinJC binJc = new BinJC();
    private final JButton saveButton = new JButton("Save");
    private Roms roms;

    public BinJF() {
        setPreferredSize(new Dimension(1280, 960));
        setTitle("BinView");
        setLayout(new BorderLayout());
        sizeSpin.addChangeListener(e -> wrapEx(() -> updateView()));
        offsetSpin.addChangeListener(e -> wrapEx(() -> updateView()));
        formatBox.addItemListener(e -> wrapEx(() -> updateView()));
        swapBox.addChangeListener(e -> wrapEx(() -> updateView()));
        dirField.setText(PREFS.get("romDir", System.getProperty("user.dir")));
        dirField.addActionListener(e -> wrapEx(() -> loadRoms()));
        gameBox.addItemListener(e -> wrapEx(() -> setData()));
        bankBox.addItemListener(e -> wrapEx(() -> setData()));
        saveButton.addActionListener(e -> wrapEx(() -> saveIt()));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(new JLabel("Dir"));
        top.add(dirField);
        top.add(new JLabel("Rom"));
        top.add(gameBox);
        top.add(bankBox);
        top.add(saveButton);
        top.add(new JLabel("Size"));
        top.add(sizeSpin);
        top.add(new JLabel("Offset"));
        top.add(offsetSpin);
        top.add(new JLabel("Format"));
        top.add(formatBox);
        top.add(swapBox);
        add(top, BorderLayout.NORTH);
        add(binJc, BorderLayout.CENTER);
    }

    public void wrapEx(Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, ScanJF.wrapStr(e.toString()));
        }
    }

    private void saveIt() {
        Game g = (Game) gameBox.getSelectedItem();
        Bank b = (Bank) bankBox.getSelectedItem();
        byte[] a = roms.load(g, b);
        JFileChooser fc = new JFileChooser();
        File saveDir = new File(PREFS.get("saveDir", System.getProperty("user.dir")));
        fc.setSelectedFile(new File(saveDir, g.name() +"." + b.name() + ".bin"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.write(a);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            PREFS.put("saveDir", f.getParentFile().getAbsolutePath());
        }
    }

    private void loadRoms() {
        roms = new Roms(new File(dirField.getText()));
        PREFS.put("romDir", dirField.getText());
        setData();
    }

    private void setData() {
        binJc.setData(roms.load((Game) gameBox.getSelectedItem(), (Bank) bankBox.getSelectedItem()));
        updateView();
        repaint();
    }

    private void updateView() {
        BinJC.Opt opt = new BinJC.Opt();
        opt.size = ((Number) sizeSpin.getValue()).intValue();
        opt.offset = ((Number) offsetSpin.getValue()).intValue();
        opt.format = (BinJC.Opt.Format) formatBox.getSelectedItem();
        opt.swap = swapBox.isSelected();
        binJc.setParams(opt);
        repaint();
    }
}

