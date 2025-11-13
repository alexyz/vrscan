package vr;

import vr.m.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {

    public static class Stats {
        public final F3 min = new F3().setMax(), max = new F3().setMin();
        public int minpos, maxpos;
    }

    public final List<DL> dls = new ArrayList<>();
    public final int position;
    private Stats stats;

    public Scene(int position) {
        this.position = position;
    }

    private Stats tStats() {
        if (stats == null) {
            Stats s = new Stats();
            for (int n = 0; n < dls.size(); n++) {
                DL p = dls.get(n);
                for (int d = 0; d < 3; d++) {
                    s.min.set(d, Math.min(s.min.get(d), p.min().get(d)));
                    s.max.set(d, Math.max(s.max.get(d), p.max().get(d)));
                }
            }
            {
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                for (int n = 0; n < dls.size(); n++) {
                    DL pl = dls.get(n);
                    min = Math.min(min, pl.offset);
                    max = Math.max(max, pl.offset);
                }
                s.minpos = min;
                s.maxpos = max;
            }
            this.stats = s;
        }
        return stats;
    }

    /** normalise to unit cube */
    public M normalisationMatrix2() {
        return M4.trans(-1, -1, -1)
                .mul(M4.scale(2, 2, 2))
                .mul(unitSquare2())
                .mul(zeroBase2());
    }

    /** scale so point is within [0,1]^3 preserving aspect */
    public M unitSquare2() {
        float xd = max().x - min().x;
        float yd = max().y - min().y;
        float zd = max().z - min().z;
        float max = Math.max(xd, Math.max(yd, zd));
        return M4.scale(1 / max, 1 / max, 1 / max);
    }

    /** translate so min point is 0 */
    public M zeroBase2() {
        return M4.trans(-min().x, -min().y, -min().z);
    }

    public F3 dim() {
        return new F3().set(max().x - min().x, max().y - min().y, max().z - min().z);
    }

    public int minPos() {
        return tStats().minpos;
    }

    public int maxPos() {
        return tStats().maxpos;
    }

    public F3 min() {
        return tStats().min;
    }

    public F3 max() {
        return tStats().max;
    }

    public int countP() {
        return dls.stream().mapToInt(l -> l.polys.size()).sum();
    }

    @Override
    public String toString() {
        return String.format("Scene[p=%x dls=%d paras=%d dim=%s]", position, dls.size(), countP(), dim());
    }

}
