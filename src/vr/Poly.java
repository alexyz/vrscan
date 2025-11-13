package vr;

import vr.m.*;

public class Poly {
    public int word;
    public final F3 s1 = new F3(), s2 = new F3(), s3 = new F3();

    @Override
    public boolean equals(Object o) {
        if (o instanceof Poly p) {
            return word == p.word && s1.equals(p.s1) && s2.equals(p.s2) && s3.equals(p.s3);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (s2.equals(s3)) {
            return String.format("P[%8x, %s, %s, -]", word, s1, s2);
        } else {
            return String.format("P[%8x, %s, %s, %s]", word, s1, s2, s3);
        }
    }
}
