package vr;

import java.util.ArrayList;
import java.util.List;

public class Polygons {
    public static int T1_BF = 0;
    public static int T2_AP = 0xF8D10; // was 0xf9fc0
    public static int T3_BB = 0x1F4C78;
    public static int T_END = 0x2A6FD4;

    public static List<DL> loadLists(int[] romWords) {
        List<DL> lists = new ArrayList<>();
        boolean newlist = true;

        for (int wp = 0; wp < romWords.length; ) {
            if (romWords[wp] == 0) {
                newlist = true;
                wp++;

            } else if (isPrefix(romWords[wp])) {
                Para p = readPara(romWords, wp);
                if (isDummy(p)) {
                    //System.out.println(String.format("dummy p %x", wp * 4));
                    newlist = true;

                } else {
                    if (newlist) {
                        lists.add(new DL(wp * 4));
                        newlist = false;
                    }
                    lists.get(lists.size() - 1).paras.add(p);
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

    private static boolean isDummy(Para p) {
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

    public static Para readPara(int[] words, int o) { // todo to Roms
        Para p = new Para();
        p.word = words[o + 0];
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

    // 0180 1b02
    // n1 = 0
    // n2 = 0,1 - paint?
    // n3 = 0,8 - paint?
    // n4 = 0
    // n5 = 1
    // n6 = register
    // n7 = 0
    // n8 = 1,2 (Q, T)
    public static boolean isPrefix(int i) { // todo to Roms
        int i1 = i & 0xff_00_00_00;
        if (i1 == 0 || i1 == 0x1_00_00_00) { // byte 1 is [01]
            int i2 = i & 0xf0_00_00;
            if (i2 == 0 || i2 == 0x80_00_00) { // upper byte 2 is [08], lower byte is any
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

    private Polygons() { }
}
