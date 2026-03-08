package vr;

import vr.m.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {

    public static class Stats {
        public final M1 min = new M1().setMaxHc(), max = new M1().setMinHc();
        public int minpos, maxpos;
    }

    public final List<DL> dls = new ArrayList<>();
    public final int position;
    private Stats stats;

    public Scene(int position) {
        this.position = position;
    }

    public Scene(List<DL> dls) {
        this.position = dls.get(0).offset;
        this.dls.addAll(dls);
    }

    private Stats tStats() {
        if (stats == null) {
            Stats s = new Stats();
            for (int n = 0; n < dls.size(); n++) {
                DL p = dls.get(n);
                s.min.setMin(p.min());
                s.max.setMax(p.max());
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
        M1 d = dim();
        float max = Math.max(d.x(), Math.max(d.y(), d.z()));
        return M4.scale(1 / max, 1 / max, 1 / max);
    }

    /** translate so min point is 0 */
    public M zeroBase2() {
        return M4.trans(-min().x(), -min().y(), -min().z());
    }

    public M1 dim() {
        return max().sub(min());
    }

    public int minPos() {
        return tStats().minpos;
    }

    public int maxPos() {
        return tStats().maxpos;
    }

    public M1 min() {
        return tStats().min;
    }

    public M1 max() {
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
