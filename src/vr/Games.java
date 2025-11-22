package vr;

import java.util.*;

public class Games {

    public static final Games instance = new Games();

    private static Names load16Byte(String n1, String n2) {
        return new Names(Load.loadAltByte, new String[]{n1, n2});
    }

    private static Names load32Word(String n1, String n2) {
        return new Names(Load.loadAltWord, new String[]{n1, n2});
    }

    private static Names load32Byte(String n1, String n2, String n3, String n4) {
        return new Names(Load.loadAltByte, new String[]{n1, n2, n3, n4});
    }

    private static Names load(String n) {
        return new Names(Load.load, new String[]{n});
    }

    private Games() {
        add(Game.vf, Bank.mainCpu1, load16Byte("epr-16082.14", "epr-16083.15"));
        add(Game.vf, Bank.mainCpu2, load("epr-16080.4"),
                load("epr-16080.4"));
        add(Game.vf, Bank.mainCpu3, load16Byte("mpr-16084.6", "mpr-16085.7"),
                load16Byte("mpr-16086.8", "mpr-16087.9"),
                load16Byte("mpr-16088.10", "mpr-16089.11"),
                load16Byte("mpr-16090.12", "mpr-16091.13"));
        add(Game.vf, Bank.polygons, load32Word("mpr-16096.26", "mpr-16097.27"),
                load32Word("mpr-16098.28", "mpr-16099.29"),
                load32Word("mpr-16100.30", "mpr-16101.31"),
                load32Word("mpr-16102.32", "mpr-16103.33"));

        add(Game.vr, Bank.mainCpu1, load16Byte("epr-14882.14", "epr-14883.15"));
        add(Game.vr, Bank.mainCpu2, load("epr-14878a.4"),
                load("epr-14879a.5"));
        add(Game.vr, Bank.mainCpu3, load16Byte("mpr-14880.6", "mpr-14881.7"),
                load16Byte("mpr-14884.8", "mpr-14885.9"),
                load16Byte("mpr-14886.10", "mpr-14887.11"),
                load16Byte("mpr-14888.12", "mpr-14889.13"));
        add(Game.vr, Bank.polygons, load32Word("mpr-14890.26", "mpr-14891.27"),
                load32Word("mpr-14892.28", "mpr-14893.29"),
                load32Word("mpr-14894.30", "mpr-14895.31"),
                load32Word("mpr-14896.32", "mpr-14897.33"));
        add(Game.vr, Bank.tgpData, load32Byte("mpr-14898.39",
                "mpr-14899.40",
                "mpr-14900.41",
                "mpr-14901.42"));

        add(Game.swa, Bank.mainCpu1, load16Byte("epr-16669.14", "epr-16670.15"));
        add(Game.swa, Bank.mainCpu2, load("epr-16668.5"));
        // doesn't have maincpu3
        add(Game.swa, Bank.polygons, load32Word("mpr-16476.26", "mpr-16477.27"),
                load32Word("mpr-16478.28", "mpr-16479.29"),
                load32Word("mpr-16480.30", "mpr-16481.31"));
        add(Game.swa, Bank.tgpData, load32Byte("mpr-16472.39",
                "mpr-16473.40",
                "mpr-16474.41",
                "mpr-16475.42"));

        add(Game.wingwar, Bank.mainCpu1, load16Byte("epr-16729.14", "epr-16730.15"));
        add(Game.wingwar, Bank.mainCpu2, load("epr-16953.4"), load("epr-16952.5"));
        add(Game.wingwar, Bank.mainCpu3, load16Byte("mpr-16738.6", "mpr-16737.7"),
                load16Byte("mpr-16736.8", "mpr-16735.9"),
                load16Byte("mpr-16734.10", "mpr-16733.11"));
        add(Game.wingwar, Bank.polygons, load32Word("mpr-16743.26", "mpr-16744.27"),
                load32Word("mpr-16745.28", "mpr-16746.29"),
                load32Word("mpr-16747.30", "mpr-16748.31"),
                load32Word("mpr-16749.32", "mpr-16750.33"));
        add(Game.wingwar, Bank.tgpData, load32Byte("mpr-16741.39",
                "mpr-16742.40",
                "mpr-16739.41",
                "mpr-16740.42"));

        // netmerc doesn't seem to have mc1
        add(Game.netmerc, Bank.mainCpu2, load("epr-18120.ic5"));
        add(Game.netmerc, Bank.mainCpu3, load16Byte("epr-18122.ic6", "epr-18123.ic7"),
                load16Byte("epr-18124.ic8", "epr-18125.ic9"),
                load16Byte("epr-18126.ic10", "epr-18127.ic11"));
        add(Game.netmerc, Bank.polygons, load32Word("mpr-18128.ic26", "mpr-18129.ic27"),
                load32Word("mpr-18130.ic28", "mpr-18131.ic29"),
                load32Word("mpr-18132.ic30", "mpr-18133.ic31"));
    }

    private final Map<Game, Map<Bank, Names[]>> games = new TreeMap<>();

    private void add(Game g, Bank b, Names... l) {
        games.compute(g, (k1, v1) -> v1 != null ? v1 : new TreeMap<>()).put(b, l);
    }

    public Names[] get(Game g, Bank b) {
        return games.get(g).get(b);
    }

}
