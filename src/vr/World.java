package vr;

import java.io.IOException;
import java.util.List;

public class World {
    public static int T1_START = 0, T1_END = 0xf9fb8;
    public static int T2_START = 0xfb36c, T2_END = 0x1f5570;
    public static int T3_START = 0x1f6924, T3_END = 0x2A6FD4;
    public static int P_START = 0x480000, P_END = 0x487788;

    private final Polygons p;

    public World(Polygons p) {
        this.p = p;
    }

    public Scene bf() {
        Scene bf = new Scene(T1_START);
        bf.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= T1_START && l.offset < T1_END).toList());
        return bf;
    }

    public Scene ap() {
        Scene ap = new Scene(T2_START);
        ap.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= T2_START && l.offset < T2_END).toList());
        return ap;
    }

    public Scene bb() {
        Scene bb = new Scene(T3_START);
        bb.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= T3_START && l.offset < T3_END).toList());
        return bb;
    }

    public Scene p() {
        Scene bb = new Scene(P_START);
        bb.dls.addAll(p.displayLists.stream().filter(l -> l.offset >= P_START && l.offset < P_END).toList());
        return bb;
    }
}
