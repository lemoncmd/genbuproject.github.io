package net.zhuoweizhang.mcpelauncher.patch;

import com.joshuahuelsman.patchtool.PTPatch;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import net.zhuoweizhang.mcpelauncher.MaraudersMap;
import net.zhuoweizhang.mcpelauncher.MinecraftVersion;
import net.zhuoweizhang.mcpelauncher.MinecraftVersion.PatchTranslator;

public final class PatchUtils {
    public static MinecraftVersion minecraftVersion = null;

    private PatchUtils() {
    }

    private static ByteBuffer positionBuf(ByteBuffer buf, int addr) {
        if (buf == MainActivity.minecraftLibBuffer && addr >= 0 && addr < MaraudersMap.minecraftTextBuffer.capacity()) {
            buf = MaraudersMap.minecraftTextBuffer;
        }
        buf.position(addr);
        return buf;
    }

    public static void patch(ByteBuffer buf, PTPatch patch) {
        PatchTranslator translator = minecraftVersion.translator;
        patch.count = 0;
        while (patch.count < patch.getNumPatches()) {
            int addr = patch.getNextAddr();
            if (translator != null) {
                addr = translator.get(addr);
            }
            positionBuf(buf, addr).put(patch.getNextData());
            patch.count++;
        }
    }

    public static void unpatch(ByteBuffer buf, byte[] original, PTPatch patch) {
        PatchTranslator translator = minecraftVersion.translator;
        ByteBuffer originalBuf = ByteBuffer.wrap(original);
        patch.count = 0;
        while (patch.count < patch.getNumPatches()) {
            int addr = patch.getNextAddr();
            if (translator != null) {
                addr = translator.get(addr);
            }
            ByteBuffer newBuf = positionBuf(buf, addr);
            originalBuf.position(addr);
            byte[] nextData = new byte[patch.getDataLength()];
            originalBuf.get(nextData);
            newBuf.put(nextData);
            patch.count++;
        }
    }

    public static void copy(File from, File to) throws IOException {
        InputStream is = new FileInputStream(from);
        byte[] data = new byte[((int) from.length())];
        is.read(data);
        is.close();
        OutputStream os = new FileOutputStream(to);
        os.write(data);
        os.close();
    }

    public static boolean canLivePatch(File file) throws IOException {
        return true;
    }

    public static byte[] createMovwInstr(int rd, int imm) {
        long instr = ((((4064280576L | ((long) (rd << 8))) | ((long) (imm & 255))) | ((long) (((imm >> 8) & 7) << 12))) | ((long) (((imm >> 11) & 1) << 26))) | ((long) (((imm >> 12) & 15) << 16));
        byte[] finalByte = intToLEByteArray(instr);
        System.out.println("Port patch: " + Long.toString(instr, 16));
        return finalByte;
    }

    public static final byte[] intToLEByteArray(long value) {
        return new byte[]{(byte) ((int) (value >>> 16)), (byte) ((int) (value >>> 24)), (byte) ((int) value), (byte) ((int) (value >>> 8))};
    }
}
