package vr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Scenes {
    public static int T1_START = 0, T1_END = 0xf9fb8;
    public static int T2_START = 0xfb36c, T2_END = 0x1f5570;
    public static int T3_START = 0x1f6924, T3_END = 0x2A6FD4;
    public static int P_START = 0x480000, P_END = 0x487788;

    private Scenes() {
        //
    }

    public static List<Scene> scenes(Game g, Polygons p) {
        switch (g) {
            case Game.vr:
                return Arrays.asList(bf(p), ap(p), bb(p), pod(p));
            case Game.vf:
                return vf(p);
            default:
                return Collections.emptyList();
        }
    }

    public static List<Scene> vf(Polygons p) {
        List<Scene> l = new ArrayList<>();
        for (int[] i : new int[][]{{0, 2}, {2, 2}, {4, 2}, {6, 3}, {9, 2}, {11, 3}, {14, 3}, {17, 3}, {2952, 4}, {2956, 2}, {2958, 1}}) {
            l.add(new Scene(p.displayLists.subList(i[0], i[0] + i[1])));
        }
        return l;
    }

    public static Scene bf(Polygons p) {
        Scene bf = new Scene(T1_START);
        bf.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= T1_START && l.offset < T1_END).toList());
        return bf;
    }

    public static Scene ap(Polygons p) {
        Scene ap = new Scene(T2_START);
        ap.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= T2_START && l.offset < T2_END).toList());
        return ap;
    }

    public static Scene bb(Polygons p) {
        Scene bb = new Scene(T3_START);
        bb.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= T3_START && l.offset < T3_END).toList());
        return bb;
    }

    public static Scene pod(Polygons p) {
        Scene bb = new Scene(P_START);
        bb.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= P_START && l.offset < P_END).toList());
        return bb;
    }
}
