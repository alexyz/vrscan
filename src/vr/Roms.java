package vr;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Roms {

    public enum RB {mainCpu1, mainCpu2, mainCpu3, polygons, tgpData}

    public static void main(String[] args) throws Exception {
        File romDir = new File(args[0]);
        File outDir = new File(System.getProperty("user.dir"));

        // looks like math lookups
        //String[] dataRomFiles = new String[]{"mpr-14898.39", "mpr-14899.40", "mpr-14900.41", "mpr-14901.42"};

        // looks like bitmaps
        //String[] dataRomFiles = { "mpr-14880.6", "mpr-14881.7" };

        // allegedly v60 code, but looks a lot like bitmaps
        //String[] dataRomFiles = { "epr-14882.14", "epr-14883.15" };

//            String[] romFiles1 = new String[]{"mpr-14890.26", "mpr-14891.27"};
//            String[] romFiles2 = new String[]{"mpr-14892.28", "mpr-14893.29"};
//            String[] romFiles3 = new String[]{"mpr-14894.30", "mpr-14895.31"}; // every pl is a scene?
//            String[] romFiles4 = new String[]{"mpr-14896.32", "mpr-14897.33"};
        // it's not really possible to detect scene boundaries
        // need to do them manually, unless there are pointers in the data rom

        Roms roms = new Roms(romDir);
        writeBytes(new File(outDir, "maincpu1.bin"), roms.load(RB.mainCpu1));
        writeBytes(new File(outDir, "maincpu2.bin"), roms.load(RB.mainCpu2));
        writeBytes(new File(outDir, "maincpu3.bin"), roms.load(RB.mainCpu3));
        writeIntsBe(new File(outDir, "polygons.swap.bin"), roms.loadWords((RB.polygons)));
        writeBytes(new File(outDir, "tgpdata.bin"), roms.load(RB.tgpData));
        writeIntsBe(new File(outDir, "tgpdata.swap.bin"), roms.loadWords(RB.tgpData));

    }

    private final File romDir;
    private Map<RB, byte[]> bytes = new TreeMap<>();
    private Map<RB, int[]> words = new TreeMap<>();
    private Map<RB, short[]> halfWords = new TreeMap<>();

    public Roms(File romDir) {
        if (!romDir.isDirectory()) {
            throw new RuntimeException();
        }
        this.romDir = romDir;
    }

    private byte[] loadMainCpu1() throws IOException {
        // ROM_LOAD16_BYTE( "epr-14882.14",  0x200000, 0x80000,
        // ROM_LOAD16_BYTE( "epr-14883.15",  0x200001, 0x80000,
        return loadRomsAlt(new String[]{"epr-14882.14", "epr-14883.15"}, 1);
    }

    private byte[] loadMainCpu2() throws IOException {
        // ROM_LOAD( "epr-14878a.4", 0xfc0000, 0x20000,
        // ROM_LOAD( "epr-14879a.5", 0xfe0000, 0x20000,
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(0x20000 * 2)) {
            bos.write(loadRom("epr-14878a.4"));
            bos.write(loadRom("epr-14879a.5"));
            return bos.toByteArray();
        }
    }

    private byte[] loadMainCpu3() throws IOException {
        // ROM_LOAD16_BYTE( "mpr-14880.6",  0x1000000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14881.7",  0x1000001, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14884.8",  0x1100000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14885.9",  0x1100001, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14886.10", 0x1200000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14887.11", 0x1200001, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14888.12", 0x1300000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14889.13", 0x1300001, 0x80000,
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(0x80000 * 8)) {
            bos.write(loadRomsAlt(new String[]{"mpr-14880.6", "mpr-14881.7"}, 1));
            bos.write(loadRomsAlt(new String[]{"mpr-14884.8", "mpr-14885.9"}, 1));
            bos.write(loadRomsAlt(new String[]{"mpr-14886.10", "mpr-14887.11"}, 1));
            bos.write(loadRomsAlt(new String[]{"mpr-14888.12", "mpr-14889.13"}, 1));
            return bos.toByteArray();
        }
    }

    private byte[] loadPolygons() throws IOException {
        // ROM_LOAD32_WORD( "mpr-14890.26",  0x000000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14891.27",  0x000002, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14892.28",  0x400000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14893.29",  0x400002, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14894.30",  0x800000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14895.31",  0x800002, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14896.32",  0xc00000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14897.33",  0xc00002, 0x200000,
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(0x200000 * 8)) {
            bos.write(loadRomsAlt(new String[]{"mpr-14890.26", "mpr-14891.27"}, 2));
            bos.write(loadRomsAlt(new String[]{"mpr-14892.28", "mpr-14893.29"}, 2));
            bos.write(loadRomsAlt(new String[]{"mpr-14894.30", "mpr-14895.31"}, 2));
            bos.write(loadRomsAlt(new String[]{"mpr-14896.32", "mpr-14897.33"}, 2));
            return bos.toByteArray();
        }
    }

    private byte[] loadTgpData() throws IOException {
        // ROM_LOAD32_BYTE( "mpr-14898.39",  0x000000,  0x80000,
        // ROM_LOAD32_BYTE( "mpr-14899.40",  0x000001,  0x80000,
        // ROM_LOAD32_BYTE( "mpr-14900.41",  0x000002,  0x80000,
        // ROM_LOAD32_BYTE( "mpr-14901.42",  0x000003,  0x80000,
        return loadRomsAlt(new String[]{"mpr-14898.39", "mpr-14899.40", "mpr-14900.41", "mpr-14901.42"}, 1);
    }

    public byte[] load(RB rb) throws IOException {
        byte[] b = bytes.get(rb);
        if (b == null) {
            switch (rb) {
                case mainCpu1: b = loadMainCpu1(); break;
                case mainCpu2: b = loadMainCpu2(); break;
                case mainCpu3: b = loadMainCpu3(); break;
                case polygons: b = loadPolygons(); break;
                case tgpData: b = loadTgpData(); break;
                default: throw new RuntimeException();
            }
            bytes.put(rb, b);
        }
        return b;
    }

    public int[] loadWords(RB bank) throws IOException {
        int[] w = words.get(bank);
        if (w == null) {
            words.put(bank, w = swap32(load(bank)));
        }
        return w;
    }

    public short[] loadHalfWords(RB bank) throws IOException {
        short[] hw = halfWords.get(bank);
        if (hw == null) {
            halfWords.put(bank, hw = swap16(load(bank)));
        }
        return hw;
    }

    private static short[] swap16(byte[] roms) throws IOException {
        short[] outw = new short[roms.length / 2];
        try (ByteArrayInputStream bis = new ByteArrayInputStream(roms)) {
            int p = 0;
            byte[] a = new byte[2];
            while (bis.read(a) > 0) {
                outw[p++] = (short) ((a[1] & 0xff) << 8 | (a[0] & 0xff));
            }
        }
        return outw;
    }

    private static int[] swap32(byte[] roms) throws IOException {
        int[] outw = new int[roms.length / 4];
        byte[] a = new byte[4];
        try (ByteArrayInputStream bis = new ByteArrayInputStream(roms)) {
            int p = 0;
            while (bis.read(a) > 0) {
                outw[p++] = ((a[3] & 0xff) << 24) | ((a[2] & 0xff) << 16) | ((a[1] & 0xff) << 8) | (a[0] & 0xff);
            }
        }
        return outw;
    }


    private static void writeBytes(File file, byte[] roms) throws IOException {
        if (file.exists()) {
            System.out.println("not writing " + file);
        } else {
            System.out.println("writing " + file);
            try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(file))) {
                fos.write(roms);
            }
        }
    }

    private static void writeIntsBe(File file, int[] romWords) throws IOException {
        if (file.exists()) {
            System.out.println("not writing " + file);
        } else {
            System.out.println("writing " + file);
            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
                os.write(intsToBytesBe(romWords));
            }
        }
    }

    public static byte[] intsToBytesBe(int[] romWords) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(romWords.length * 4)) {
            try (DataOutputStream dos = new DataOutputStream(bos)) {
                for (int w : romWords) {
                    dos.writeInt(w);
                }
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] loadRom(String name) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(romDir, name))) {
            return fis.readAllBytes();
        }
    }

    /**
     * load numbytes from each rom in turn. all roms must be same size.
     */
    private byte[] loadRomsAlt(String[] names, int numBytes) throws IOException {
        System.out.println("load " + Arrays.toString(names) + " nb " + numBytes);
        List<File> files = Arrays.asList(names).stream().map(s -> new File(romDir, s)).toList();
        if (files.stream().anyMatch(f -> !f.isFile())) {
            throw new RuntimeException("missing files: " + Arrays.toString(names));
        }

        int[] fileLengths = files.stream().mapToInt(f -> (int) f.length()).distinct().toArray();
        if (fileLengths.length != 1 || fileLengths[0] == 0) {
            throw new RuntimeException("invalid files: " + Arrays.toString(names));
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(fileLengths[0] * files.size())) {
            List<InputStream> streams = files.stream().map(f -> openIs(f)).toList();
            doit:
            while (true) {
                for (InputStream is : streams) {
                    for (int n = 0; n < numBytes; n++) {
                        int b = is.read();
                        if (b >= 0) {
                            bos.write(b);
                        } else {
                            break doit;
                        }
                    }
                }
            }

            streams.forEach(is -> closeIs(is));
            byte[] out = bos.toByteArray();
            System.out.println("out=" + out.length);
            return out;
        }
    }

    private static void closeIs(InputStream is) {
        try {
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream openIs(File f) {
        try {
            return new BufferedInputStream(new FileInputStream(f), 65536);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
