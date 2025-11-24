package vr;

import vr.m.*;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Polygons {
    public List<DL> displayLists;
    public List<PA> polyAddrs;
    public short[] textures;
    /**
     * 15 bit color
     */
    public short[] palette;
    public Color[] colors;

    public Polygons load(Roms roms, Game g) throws IOException {
        this.polyAddrs = loadPolyAddrs(roms, g);
        this.textures = loadTextures(roms, g);
        this.palette = loadPalette(roms, g);
        this.colors = loadColors();

        // load display lists...
        this.displayLists = loadDisplayLists(roms.loadWords(g, Bank.polygons));

        // copy pa into dl
        for (DL dl : displayLists) {
            int exPa = (dl.offset + 16) / 4;
            dl.pa = polyAddrs.stream().filter(pa -> pa.polyAddr == exPa).findFirst().orElse(null);

            if (dl.pa != null) {
                int ta = dl.pa.texAddr;
                for (int n = 1; n < dl.polys.size(); n++) {
                    Poly p = dl.polys.get(n);
                    ta += Poly.texadr(p.word);
                    if (Poly.link(p.word) > 0) {
                        p.texAddr = ta;
                        p.tex = textures[ta - 0x40000] & 0xffff;
                        p.col = palette[p.tex & 0x3ff] & 0xffff;
                        p.colObj = colors[p.tex & 0x3ff];
                    }
                }
            }
        }

        return this;
    }

    private Color[] loadColors() {
        Color[] colors = new Color[palette.length];
        for (int n = 0; n < palette.length; n++) {
            int c = palette[n] & 0xffff;
            colors[n] = new Color(Poly.red(c), Poly.green(c), Poly.blue(c));
        }
        return colors;
    }

    private short[] loadPalette(Roms roms, Game g) throws IOException {
        short[] a = new short[0x400];
        if (g == Game.vr) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu1);
            int p = 0xEC980 / 2;
            for (int n = 0; n < a.length; n++) {
                a[n] = data[p + n];
            }
        } else if (g == Game.vf) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu1);
            int p = 0xD963E / 2;
            for (int n = 0; n < a.length; n++) {
                a[n] = data[p + n];
            }
        } else {
            System.out.println("no palette for " + g);
            for (int n = 0; n < a.length; n++) {
                int c = (n * 32) / a.length;
                a[n] = (short) ((c << 10) | (c << 5) | c);
            }
        }
        return a;
    }

    private short[] loadTextures(Roms roms, Game g) {
        short[] a = new short[0x60000];
        if (g == Game.vr) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu1);
            for (int n = 0; n < a.length; n++) {
                a[n] = data[n];
            }
        } else if (g == Game.vf) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu3);
            for (int n = 0; n < a.length; n++) {
                a[n] = data[n + (0x300000 / 2)];
            }
        } else {
            System.out.println("no textures for " + g);
            for (int n = 0; n < a.length; n++) {
                a[n] = (short) ((n * 0x3ff) / a.length);
            }
        }
        return a;
    }

    private List<PA> loadPolyAddrs(Roms roms, Game g) {
        List<PA> list = new ArrayList<>();

        if (g == Game.vr) {
            int[] words = roms.loadWords(g, Bank.mainCpu1);
            int s = 0xe0000 / 4, e = 0xec980 / 4;
            for (int n = s; n < e; n += 4) {
                PA pa = new PA();
                pa.polyAddr = words[n];
                pa.texAddr = words[n + 1];
                pa.extra1 = words[n + 2];
                pa.extra2 = words[n + 3];
                list.add(pa);
            }

        } else if (g == Game.vf) {
            int[] words = roms.loadWords(g, Bank.mainCpu1);
            int s = 0xd0000 / 4, e = 0xD9638 / 4;
            for (int n = s; n < e; n += 3) {
                PA pa = new PA();
                pa.extra1 = words[n];
                pa.texAddr = words[n + 1];
                pa.polyAddr = words[n + 2];
                list.add(pa);
            }
        }

        return list;
    }

    private List<DL> loadDisplayLists(int[] romWords) {
        List<DL> lists = new ArrayList<>();
        boolean newlist = true;
        int ord = 0;

        for (int wp = 0; wp < romWords.length; ) {
            if (romWords[wp] == 0 || romWords[wp] == 0xffffffff) {
                newlist = true;
                wp++;

            } else if (isPrefix(romWords[wp])) {
                Poly p = readPara(romWords, wp);
                if (isDummy(p)) {
                    //System.out.println(String.format("dummy p %x", wp * 4));
                    newlist = true;

                } else {
                    if (newlist) {
                        lists.add(new DL(ord, wp * 4));
                        newlist = false;
                        ord++;
                    }
                    lists.get(lists.size() - 1).polys.add(p);
                    //System.out.println(String.format("p %6x %s", wp * 4, readParaStr(outw, wp)));
                }
                wp += 10;

            } else {
                System.out.println(String.format("unknown p %x w %x", wp * 4, romWords[wp]));
                break;
            }
        }
        return lists;
    }

    private static boolean isDummy(Poly p) {
        // 00021401 00000000 00000000 00000000 3F800000 C0000000 3F800000 3F800000 C0000000 BF800000
        // 00021401 00000000 3F800000 00000000 BF800000 C0000000 3F800000 BF800000 C0000000 BF800000
        if (p.word == 0x00021401) {
            if (p.s1.equals3(0, 0, 0) && p.s2.equals3(1, -2, 1) && p.s3.equals3(1, -2, -1)) {
                return true;
            } else if (p.s1.equals3(0, 1, 0) && p.s2.equals3(-1, -2, 1) && p.s3.equals3(-1, -2, -1)) {
                return true;
            }
        }
        return false;
    }

    public static Poly readPara(int[] words, int o) { // todo to Roms
        Poly p = new Poly();
        p.word = words[o];
        readSeg(words, o + 1, p.s1);
        readSeg(words, o + 4, p.s2);
        readSeg(words, o + 7, p.s3);
        return p;
    }

    public static void readSeg(int[] w, int o, F3 f) { // todo to Roms
        f.x = Float.intBitsToFloat(w[o]);
        f.y = Float.intBitsToFloat(w[o + 1]);
        f.z = Float.intBitsToFloat(w[o + 2]);
    }

    //  void model1_state::push_object(uint32_t tex_adr, uint32_t poly_adr, uint32_t size)
    // 0180 1b02
    //   18 1801
    //   98 1902
    // 0180 0303 (mask?)

    // n1 = 0
    // n2 = 0,1 - paint?
    // n3 = 0,8 - paint?
    // n4 = 0 - lightmode?
    // n5 = 1 - tex adr, moire, backface cull?
    // n6 = link
    // n7 = 0
    // n8 = 1,2 (Q, T)
    public static boolean isPrefix(int i) { // todo to Roms
        //int i1 = i & 0xff_00_00_00;
        int i1 = (i >> 24) & 0xff;
        if (i1 == 0 || i1 == 1) { // byte 1 is [01]
            //int i2 = i & 0xf0_00_00;
            int i2 = (i >> 20) & 0xf;
            if (i2 == 0 || i2 == 1 || i2 == 8 || i2 == 9) { // lower byte is any
                int i3 = i & 0xf0_00;
                if (i3 == 0x10_00 || i3 == 0x30_00 || i3 == 0x50_00 || i3 == 0x70_00) { // upper byte 3 is [1357], lower byte is any
                    int i4 = i & 0xff;
                    if (i4 == 1 || i4 == 2) { // byte 4 is [12]
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[dls=%d pas=%d tex=%d pal=%d]",
                getClass().getSimpleName(),
                displayLists.size(),
                polyAddrs.size(),
                (textures != null ? textures.length : null),
                (palette != null ? palette.length : null));
    }

}
