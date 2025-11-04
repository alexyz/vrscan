package vr;

public class PA {
    public int polyAddr, texAddr, extra1, extra2;

    @Override
    public String toString() {
        if (extra1 == 0 && extra2 == 0) {
            return String.format("%s[pa %-8x ta %-8x]", getClass().getSimpleName(), polyAddr, texAddr);
        } else {
            return String.format("%s[pa %-8x ta %-8x ex %-8x %-8x]", getClass().getSimpleName(), polyAddr, texAddr, extra1, extra2);
        }
    }
}
