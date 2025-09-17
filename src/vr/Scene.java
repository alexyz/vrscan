package vr;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    public static class Stats {
        public final F3 min = new F3().setMax(), max = new F3().setMin();
        public int minpos, maxpos;
    }

    public final List<DL> lists = new ArrayList<>();
    public final int position;
    private Stats stats;

    public Scene(int position) {
        this.position = position;
    }

    private Stats tStats() {
        if (stats == null) {
            Stats s = new Stats();
            for (int n = 0; n < lists.size(); n++) {
                DL p = lists.get(n);
                for (int d = 0; d < 3; d++) {
                    s.min.set(d, Math.min(s.min.get(d), p.min().get(d)));
                    s.max.set(d, Math.max(s.max.get(d), p.max().get(d)));
                }
            }
            {
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                for (int n = 0; n < lists.size(); n++) {
                    DL pl = lists.get(n);
                    min = Math.min(min, pl.position);
                    max = Math.max(max, pl.position);
                }
                s.minpos = min;
                s.maxpos = max;
            }
            this.stats = s;
        }
        return stats;
    }

    /** adjust f to be zero based within the scene  */
//    public F3 normalise(F3 f) {
//        // min=-200 max=-100 p=-125
//        // then (p-min) = -125 - -200 = 75
//        // or (max-p) = -100 - -125 = 25
//        // f.z - min().z
//        return new F3().set(f.x - min().x, f.y - min().y, f.z - min().z);
//    }

    public M normalisationMatrix2() {
        return M.trans4(-1, -1, -1).mul(M.scale4(2, 2, 2)).mul(unitSquare2()).mul(zeroBase2()).setRo();
    }

    /** scale so point is within [0,1]^3 preserving aspect */
    public M unitSquare2() {
        float xd = max().x - min().x;
        float yd = max().y - min().y;
        float zd = max().z - min().z;
        float max = Math.max(xd, Math.max(yd, zd));
        return M.scale4(1 / max, 1 / max, 1 / max).setRo();
    }

    /** translate so min point is 0 */
    public M zeroBase2() {
        return M.trans4(-min().x, -min().y, -min().z).setRo();
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
        return lists.stream().mapToInt(l -> l.paras.size()).sum();
    }

    @Override
    public String toString() {
        return String.format("Scene[p=%x pls=%d paras=%d dim=%s]", position, lists.size(), countP(), dim());
    }

}
