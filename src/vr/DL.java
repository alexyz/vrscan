package vr;

import java.util.ArrayList;
import java.util.List;

public class DL {
    public final List<Para> polys = new ArrayList<>();
    public final int ordinal;
    public final int offset;
    public PA pa;
    private Stats stats;

    public static class Stats {
        public final F3 min = new F3().setMax(), max = new F3().setMin();
    }

    public DL(int ordinal, int offset) {
        this.ordinal = ordinal;
        this.offset = offset;
    }

    private Stats stats() {
        if (stats == null) {
            Stats s = new Stats();
            for (int n = 0; n < polys.size(); n++) {
                Para p = polys.get(n);
                for (int d = 0; d < 3; d++) {
                    s.min.set(d, Math.min(Math.min(s.min.get(d), p.s2.get(d)), p.s3.get(d)));
                    s.max.set(d, Math.max(Math.max(s.max.get(d), p.s2.get(d)), p.s3.get(d)));
                }
            }
            this.stats = s;
        }
        return stats;
    }

    public F3 min() {
        return stats().min;
    }

    public F3 max() {
        return stats().max;
    }

    @Override
    public String toString() {
        return String.format("DL[%x ord=%d len=%d min=%s max=%s pa=%x ta=%x]",
                offset, ordinal, polys.size(), min(), max(), pa != null ? pa.polyAddr : 0, pa != null ? pa.texAddr : 0);
    }
}
