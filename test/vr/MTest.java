package vr;

import vr.m.M;
import vr.m.M1;
import vr.m.M4;

public class MTest {

    public static void main(String[] args) {
        M a = new M4().setAll(1, 2, 3, 3.5f, 4, 5, 6, 6.5f, 7, 8, 9, 9.5f, 10, 11, 12, 13);
        M b = new M4().setAll(9, 8, 7, 7.5f, 6, 5, 4, 4.5f, 3, 2, 1, 1.5f, -1, -2, -3, -4);
        M ab = a.mul(b);
        M exAb = new M4().setAll(26.5f, 17f, 7.5f, 7f, 77.5f, 56, 34.5f, 35.5f, 128.5f, 95, 61.5f, 64, 179, 133, 87, 90.5f);
        System.out.println("ab=" + ab);
        if (!ab.equals(exAb)) {
            throw new RuntimeException();
        }

        M d = new M1().setAll(9, 8, 7, 7.5f);
        M ad = a.mul(d);
        System.out.println("ad=" + ad);
        M exAd = new M1().setAll(72.25f, 166.75f, 261.25f, 359.5f);
        if (!ad.equals(exAd)) {
            throw new RuntimeException();
        }
    }
}
