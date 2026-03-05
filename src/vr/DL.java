package vr;

import vr.m.*;
import java.util.ArrayList;
import java.util.List;

public class DL {
    public final List<Poly> polys = new ArrayList<>();
    public final int ordinal;
    public final int offset;
    public PA pa;
    private Stats stats;

    public static class Stats {
        public final M1 min = new M1().setMaxHc(), max = new M1().setMinHc();
    }

    public DL(int ordinal, int offset) {
        this.ordinal = ordinal;
        this.offset = offset;
    }

    private Stats stats() {
        if (stats == null) {
            Stats s = new Stats();
            for (int n = 0; n < polys.size(); n++) {
                Poly p = polys.get(n);
                s.min.setMin(p.s2).setMin(p.s3);
                s.max.setMax(p.s2).setMax(p.s3);
            }
            this.stats = s;
        }
        return stats;
    }

    public M1 min() {
        return stats().min;
    }

    public M1 max() {
        return stats().max;
    }

    public int end() {
        return offset + (polys.size() * 20);
    }

    @Override
    public String toString() {
        return String.format("DL[%x ord=%d end=%x len=%d min=%s max=%s pa=%x ta=%x]",
                offset, ordinal, end(), polys.size(), min(), max(), pa != null ? pa.polyAddr : 0, pa != null ? pa.texAddr : 0);
    }
}
