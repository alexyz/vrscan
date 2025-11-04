package vr;

import java.io.*;
import java.util.Arrays;
import java.util.List;

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

        writeBytes(new File(outDir, "maincpu1.bin"), loadMainCpu1(romDir));
        writeBytes(new File(outDir, "maincpu2.bin"), loadMainCpu2(romDir));
        writeBytes(new File(outDir, "maincpu3.bin"), loadMainCpu3(romDir));
        writeIntsBe(new File(outDir, "polygons.swap.bin"), swap32(loadPolygons(romDir)));
        byte[] tgp = loadTgpData(romDir);
        writeBytes(new File(outDir, "tgpdata.bin"), tgp);
        writeIntsBe(new File(outDir, "tgpdata.swap.bin"), swap32(tgp));

    }

    public static byte[] loadMainCpu1(File romDir) throws IOException {
        // ROM_LOAD16_BYTE( "epr-14882.14",  0x200000, 0x80000,
        // ROM_LOAD16_BYTE( "epr-14883.15",  0x200001, 0x80000,
        return loadRomsAlt(romDir, new String[]{"epr-14882.14", "epr-14883.15"}, 1);
    }

    public static byte[] loadMainCpu2(File romDir) throws IOException {
        // ROM_LOAD( "epr-14878a.4", 0xfc0000, 0x20000,
        // ROM_LOAD( "epr-14879a.5", 0xfe0000, 0x20000,
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(0x20000 * 2)) {
            bos.write(loadRom(romDir, "epr-14878a.4"));
            bos.write(loadRom(romDir, "epr-14879a.5"));
            return bos.toByteArray();
        }
    }

    public static byte[] loadMainCpu3(File romDir) throws IOException {
        // ROM_LOAD16_BYTE( "mpr-14880.6",  0x1000000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14881.7",  0x1000001, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14884.8",  0x1100000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14885.9",  0x1100001, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14886.10", 0x1200000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14887.11", 0x1200001, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14888.12", 0x1300000, 0x80000,
        // ROM_LOAD16_BYTE( "mpr-14889.13", 0x1300001, 0x80000,
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(0x80000 * 8)) {
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14880.6", "mpr-14881.7"}, 1));
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14884.8", "mpr-14885.9"}, 1));
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14886.10", "mpr-14887.11"}, 1));
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14888.12", "mpr-14889.13"}, 1));
            return bos.toByteArray();
        }
    }

    public static byte[] loadPolygons(File romDir) throws IOException {
        // ROM_LOAD32_WORD( "mpr-14890.26",  0x000000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14891.27",  0x000002, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14892.28",  0x400000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14893.29",  0x400002, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14894.30",  0x800000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14895.31",  0x800002, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14896.32",  0xc00000, 0x200000,
        // ROM_LOAD32_WORD( "mpr-14897.33",  0xc00002, 0x200000,
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(0x200000 * 8)) {
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14890.26", "mpr-14891.27"}, 2));
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14892.28", "mpr-14893.29"}, 2));
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14894.30", "mpr-14895.31"}, 2));
            bos.write(loadRomsAlt(romDir, new String[]{"mpr-14896.32", "mpr-14897.33"}, 2));
            return bos.toByteArray();
        }
    }

    public static byte[] loadTgpData(File romDir) throws IOException {
        // ROM_LOAD32_BYTE( "mpr-14898.39",  0x000000,  0x80000,
        // ROM_LOAD32_BYTE( "mpr-14899.40",  0x000001,  0x80000,
        // ROM_LOAD32_BYTE( "mpr-14900.41",  0x000002,  0x80000,
        // ROM_LOAD32_BYTE( "mpr-14901.42",  0x000003,  0x80000,
        return loadRomsAlt(romDir, new String[]{"mpr-14898.39", "mpr-14899.40", "mpr-14900.41", "mpr-14901.42"}, 1);
    }

    public static int[] swap32(byte[] roms) throws IOException {
        int[] outw;
        outw = new int[roms.length / 4];
        byte[] a = new byte[4], b = new byte[4];
        try (ByteArrayInputStream bis = new ByteArrayInputStream(roms)) {
            int p = 0;
            while (bis.read(a) > 0) {
                b[0] = a[3];
                b[1] = a[2];
                b[2] = a[1];
                b[3] = a[0];
                outw[p++] = ((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8) | (b[3] & 0xff);
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

//    public static byte[] loadRom(File romDir, String[] romFiles) throws IOException {
//        File f1 = new File(romDir, romFiles[0]);
//        File f2 = new File(romDir, romFiles[1]);
//        if (f1.length() != f2.length()) {
//            throw new RuntimeException();
//        }
//
//        byte[] out;
//        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) (f1.length() * 2))) {
//            byte[] a = new byte[2];
//            try (InputStream is1 = new BufferedInputStream(new FileInputStream(f1));
//                 InputStream is2 = new BufferedInputStream(new FileInputStream(f2))) {
//                while (is1.read(a) > 0) {
//                    bos.writeBytes(a);
//                    if (is2.read(a) > 0) {
//                        bos.writeBytes(a);
//                    } else {
//                        throw new RuntimeException();
//                    }
//                }
//            }
//            out = bos.toByteArray();
//            System.out.println("out=" + out.length);
//        }
//
//        return out;
//    }

    /**
     * load roms consecutively
     */
//    private static byte[] loadRoms(File romDir, String[] names) throws IOException {
//        List<File> files = Arrays.asList(names).stream().map(s -> new File(romDir, s)).toList();
//        int length = files.stream().mapToInt(f -> (int) f.length()).sum();
//        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(length)) {
//            for (File file : files) {
//                try (FileInputStream fis = new FileInputStream(file)) {
//                    bos.write(fis.readAllBytes());
//                }
//            }
//            return bos.toByteArray();
//        }
//    }

    private static byte[] loadRom(File romDir, String name) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(romDir, name))) {
            return fis.readAllBytes();
        }
    }


    /**
     * load numbytes from each rom in turn. all roms must be same size.
     */
    private static byte[] loadRomsAlt(File romDir, String[] names, int numBytes) throws IOException {
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
