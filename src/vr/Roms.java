package vr;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Roms {

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
        writeBytes(new File(outDir, "maincpu1.bin"), roms.load(Game.vr, Bank.mainCpu1));
        writeBytes(new File(outDir, "maincpu2.bin"), roms.load(Game.vr, Bank.mainCpu2));
        writeBytes(new File(outDir, "maincpu3.bin"), roms.load(Game.vr, Bank.mainCpu3));
        writeIntsBe(new File(outDir, "polygons.swap.bin"), roms.loadWords(Game.vr, Bank.polygons));
        writeBytes(new File(outDir, "tgpdata.bin"), roms.load(Game.vr, Bank.tgpData));
        writeIntsBe(new File(outDir, "tgpdata.swap.bin"), roms.loadWords(Game.vr, Bank.tgpData));

    }

    private final File romDir;
    private Map<Game, Map<Bank, byte[]>> bytes = new TreeMap<>();
    private Map<Game, Map<Bank, int[]>> words = new TreeMap<>();
    private Map<Game, Map<Bank, short[]>> halfWords = new TreeMap<>();

    public Roms(File romDir) {
        if (!romDir.isDirectory()) {
            throw new RuntimeException();
        }
        this.romDir = romDir;
    }

    private byte[] loadAny(File dir, Names load) throws IOException {
        switch (load.type) {
            case load:
                return loadRom(dir, load.names[0]);
            case loadAltByte:
                return loadRomsAlt(dir, load.names, 1);
            case loadAltWord:
                return loadRomsAlt(dir, load.names, 2);
            default:
                throw new RuntimeException();
        }
    }

    private byte[] loadMany(File dir, Names[] names) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            for (Names name : names) {
                bos.write(loadAny(dir, name));
            }
            return bos.toByteArray();
        }
    }

    public byte[] load(Game g, Bank rb) {
        Map<Bank, byte[]> bytes2 = bytes.computeIfAbsent(g, k -> new TreeMap<>());
        byte[] b = bytes2.get(rb);
        if (b == null) {
            Names[] loads = Games.instance.get(g, rb);
            if (loads != null) {
                try {
                    b = loadMany(new File(romDir, g.name()), loads);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                b = new byte[0];
            }
            bytes2.put(rb, b);
        }
        return b;
    }

    public int[] loadWords(Game g, Bank bank) throws IOException {
        Map<Bank, int[]> words2 = words.computeIfAbsent(g, k -> new TreeMap<>());
        int[] w = words2.get(bank);
        if (w == null) {
            words2.put(bank, w = swap32(load(g, bank)));
        }
        return w;
    }

    public short[] loadHalfWords(Game g, Bank bank) throws IOException {
        Map<Bank, short[]> words2 = halfWords.computeIfAbsent(g, k -> new TreeMap<>());
        short[] hw = words2.get(bank);
        if (hw == null) {
            words2.put(bank, hw = swap16(load(g, bank)));
        }
        return hw;
    }

    public static short[] swap16(byte[] roms) {
        short[] outw = new short[roms.length / 2];
        try (ByteArrayInputStream bis = new ByteArrayInputStream(roms)) {
            int p = 0;
            byte[] a = new byte[2];
            while (bis.read(a) > 0) {
                outw[p++] = (short) ((a[1] & 0xff) << 8 | (a[0] & 0xff));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outw;
    }

    public static int[] swap32(byte[] roms) {
        int[] outw = new int[roms.length / 4];
        byte[] a = new byte[4];
        try (ByteArrayInputStream bis = new ByteArrayInputStream(roms)) {
            int p = 0;
            while (bis.read(a) > 0) {
                outw[p++] = ((a[3] & 0xff) << 24) | ((a[2] & 0xff) << 16) | ((a[1] & 0xff) << 8) | (a[0] & 0xff);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private byte[] loadRom(File dir, String name) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(dir, name))) {
            return fis.readAllBytes();
        }
    }

    /**
     * load numbytes from each rom in turn. all roms must be same size.
     */
    private byte[] loadRomsAlt(File dir, String[] names, int numBytes) throws IOException {
        System.out.println("load " + Arrays.toString(names) + " nb " + numBytes);
        if (names == null || names.length == 0) {
            return new byte[0];
        }

        List<File> files = Arrays.asList(names).stream().map(s -> new File(dir, s)).toList();
        List<File> missingFiles = files.stream().filter(f -> !f.isFile()).toList();
        if (missingFiles.size() > 0) {
            throw new RuntimeException("missing files: " + missingFiles);
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
