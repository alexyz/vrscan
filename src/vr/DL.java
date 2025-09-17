package vr;

import java.util.ArrayList;
import java.util.List;

public class DL {
    public final List<Para> paras = new ArrayList<>();
    public final int position;
    private Stats stats;

    public static class Stats {
        public final F3 min = new F3().setMax(), max = new F3().setMin();
    }

    public DL(int position) {
        this.position = position;
    }

    private Stats stats() {
        if (stats == null) {
            Stats s = new Stats();
            for (int n = 0; n < paras.size(); n++) {
                Para p = paras.get(n);
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
        return String.format("DL[%x len=%d min=%s max=%s]",
                position, paras.size(), min(), max());
    }
}
