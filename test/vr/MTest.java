package vr;

import vr.m.M;
import vr.m.M1;
import vr.m.M4;
import vr.ui.ScanJF;

import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class MTest {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ScanJF.class);

    public static void main(String[] args) {
        MTest t = new MTest();
        t.setUp();
        t.mtest();
        t.romtest();
    }

    public static void assertEquals(Object o1, Object o2) {
        if (!Objects.equals(o1, o2)) {
            throw new RuntimeException();
        }
    }

    public static void assertNotNull(Object o) {
        if (o == null) {
            throw new RuntimeException();
        }
    }

    public static void assertTrue(boolean b) {
        if (!b) {
            throw new RuntimeException();
        }
    }

    public static void assertNotEmpty(Collection<?> c) {
        if (c == null || c.size() == 0) {
            throw new RuntimeException();
        }
    }

    private Roms roms;

    public void setUp() {
        roms = new Roms(new File(PREFS.get("romDir", System.getProperty("user.dir"))));
    }

    public void romtest() {
        for (Game g : Game.values()) {
            System.out.println("game " + g);
            for (Bank b : Bank.values()) {
                assertNotNull(roms.load(g, b));
                assertNotNull(roms.loadHalfWords(g, b));
                assertNotNull(roms.loadWords(g, b));
            }
            Polygons p = new Polygons().load(roms, g);
            assertNotEmpty(p.displayLists);
            assertNotNull(p.polyAddrs);
            assertTrue(p.colors.length > 0);
            assertTrue(p.palette.length > 0);
            assertTrue(p.textures.length > 0);
            assertNotNull(Scenes.scenes(g, p));

            List<M1> mlist = p.displayLists.stream().flatMap(dl -> dl.polys.stream().flatMap(p2 -> Stream.of(p2.s1, p2.s2, p2.s3))).toList();
            HashSet<M1> hset = new HashSet<>(mlist.size() * 2);
            hset.addAll(mlist);
            System.out.println("mlist=" + mlist.size() + " hset=" + hset.size());
        }
    }

    public void mtest() {
        M a = new M4().setAll(1, 2, 3, 3.5f, 4, 5, 6, 6.5f, 7, 8, 9, 9.5f, 10, 11, 12, 13);
        M b = new M4().setAll(9, 8, 7, 7.5f, 6, 5, 4, 4.5f, 3, 2, 1, 1.5f, -1, -2, -3, -4);
        M ab = a.mul(b);
        M exAb = new M4().setAll(26.5f, 17f, 7.5f, 7f, 77.5f, 56, 34.5f, 35.5f, 128.5f, 95, 61.5f, 64, 179, 133, 87, 90.5f);
        System.out.println("ab=" + ab);
        assertEquals(ab, exAb);

        M d = new M1().setAll(9, 8, 7, 7.5f);
        M ad = a.mul(d);
        System.out.println("ad=" + ad);
        M exAd = new M1().setAll(72.25f, 166.75f, 261.25f, 359.5f);
        assertEquals(ad, exAd);
    }
}
