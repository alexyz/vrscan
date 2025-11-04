package vr;

import javax.swing.*;
import java.awt.*;
import java.io.File;

// todo a vertical size and a pager and much bigger offset and view to model and swap ...
// and rowwise/coloum wise, and read 1/2/3/4 bytes at a time (le or be) or at 5-6-5 colour
// reuse image if res hasn't changed
public class BinJF extends JFrame {

    public static void main(String[] args) throws Exception {
        File romDir = new File(args[0]);
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
        byte[] data = Roms.loadMainCpu1(romDir);

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
        f.setData(data);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private final JSpinner sizeSpin = new JSpinner(new SpinnerNumberModel(32, 1, 1024000, 1));
    private final JSpinner offsetSpin = new JSpinner(new SpinnerNumberModel(0, -16777216, 16777216, 65536));
    private final JComboBox<BinJC.Opt.Format> formatBox = new JComboBox<>(BinJC.Opt.Format.values());
    private final JCheckBox swapBox = new JCheckBox("Swap", true);
    private final BinJC binJc = new BinJC();

    public BinJF() {
        setPreferredSize(new Dimension(1280, 960));
        setTitle("BinView");
        setLayout(new BorderLayout());
        sizeSpin.addChangeListener(e -> updateView());
        offsetSpin.addChangeListener(e -> updateView());
        formatBox.addItemListener(e -> updateView());
        swapBox.addChangeListener(e -> updateView());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
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

    private void setData(byte[] data) {
        binJc.setData(data);
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

