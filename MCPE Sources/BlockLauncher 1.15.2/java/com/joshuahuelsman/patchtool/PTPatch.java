package com.joshuahuelsman.patchtool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class PTPatch {
    public static final byte[] magic = new byte[]{(byte) -1, (byte) 80, (byte) 84, (byte) 80};
    public static final byte[] op_codes = new byte[]{(byte) -86, (byte) -35, (byte) -18};
    public int count;
    Header mHeader = new Header();
    public String name;
    private byte[] patch_array;

    class Header {
        byte[] indices;
        byte[] magic = new byte[4];
        int minecraft_ver;
        int num_patches;

        Header() {
        }
    }

    public void loadPatch(byte[] patch_array) {
        this.patch_array = patch_array;
        this.mHeader.minecraft_ver = getMinecraftVersion();
        this.mHeader.num_patches = getNumPatches();
        this.mHeader.indices = getIndices();
        this.count = 0;
    }

    public void loadPatch(File patchf) throws IOException {
        this.patch_array = new byte[((int) patchf.length())];
        InputStream is = new FileInputStream(patchf);
        is.read(this.patch_array);
        is.close();
        this.mHeader.minecraft_ver = getMinecraftVersion();
        this.mHeader.num_patches = getNumPatches();
        this.mHeader.indices = getIndices();
        this.count = 0;
    }

    public int getMinecraftVersion() {
        return this.patch_array[4];
    }

    public int getNumPatches() {
        return this.patch_array[5];
    }

    public byte[] getIndices() {
        byte[] ret = new byte[(this.mHeader.num_patches * 4)];
        for (int i = 0; i < this.mHeader.num_patches * 4; i++) {
            ret[i] = this.patch_array[i + 6];
        }
        return ret;
    }

    public boolean checkMagic() {
        if (this.patch_array[0] == magic[0] && this.patch_array[1] == magic[1] && this.patch_array[2] == magic[2] && this.patch_array[3] == magic[3]) {
            return true;
        }
        return false;
    }

    public void checkMinecraftVersion() {
    }

    public int getNextAddr() {
        int index = byteArrayToInt(new byte[]{this.mHeader.indices[this.count * 4], this.mHeader.indices[(this.count * 4) + 1], this.mHeader.indices[(this.count * 4) + 2], this.mHeader.indices[(this.count * 4) + 3]});
        return byteArrayToInt(new byte[]{this.patch_array[index], this.patch_array[index + 1], this.patch_array[index + 2], this.patch_array[index + 3]});
    }

    public int getCurrentIndex() {
        return byteArrayToInt(new byte[]{this.mHeader.indices[this.count * 4], this.mHeader.indices[(this.count * 4) + 1], this.mHeader.indices[(this.count * 4) + 2], this.mHeader.indices[(this.count * 4) + 3]});
    }

    public byte[] getNextData() {
        byte[] array = new byte[getDataLength()];
        int index = getCurrentIndex();
        int i2 = 0;
        for (int i = 0; i < getDataLength(); i++) {
            array[i2] = this.patch_array[(index + 4) + i];
            i2++;
        }
        return array;
    }

    public int getDataLength() {
        int end;
        byte[] i = new byte[]{this.mHeader.indices[this.count * 4], this.mHeader.indices[(this.count * 4) + 1], this.mHeader.indices[(this.count * 4) + 2], this.mHeader.indices[(this.count * 4) + 3]};
        if (this.count != this.mHeader.num_patches - 1) {
            end = byteArrayToInt(new byte[]{this.mHeader.indices[(this.count + 1) * 4], this.mHeader.indices[((this.count + 1) * 4) + 1], this.mHeader.indices[((this.count + 1) * 4) + 2], this.mHeader.indices[((this.count + 1) * 4) + 3]});
        } else {
            end = this.patch_array.length;
        }
        return end - (byteArrayToInt(i) + 4);
    }

    public void applyPatch(File f) throws IOException {
        byte[] barray = new byte[((int) f.length())];
        InputStream is = new FileInputStream(f);
        is.read(barray);
        is.close();
        ByteBuffer buf = ByteBuffer.wrap(barray);
        this.count = 0;
        while (this.count < this.mHeader.num_patches) {
            buf.position(getNextAddr());
            buf.put(getNextData());
            this.count++;
        }
        f.delete();
        OutputStream os = new FileOutputStream(f);
        os.write(buf.array());
        os.close();
    }

    public void applyPatch(byte[] barray) throws IOException {
        applyPatch(ByteBuffer.wrap(barray));
    }

    public void applyPatch(ByteBuffer buf) throws IOException {
        this.count = 0;
        while (this.count < this.mHeader.num_patches) {
            buf.position(getNextAddr());
            buf.put(getNextData());
            this.count++;
        }
    }

    public void removePatch(ByteBuffer buf, byte[] original) {
        ByteBuffer originalBuf = ByteBuffer.wrap(original);
        this.count = 0;
        while (this.count < this.mHeader.num_patches) {
            int nextAddr = getNextAddr();
            buf.position(nextAddr);
            originalBuf.position(nextAddr);
            byte[] nextData = new byte[getDataLength()];
            originalBuf.get(nextData);
            buf.put(nextData);
            this.count++;
        }
    }

    public byte[] getMetaData() {
        this.count = 0;
        int metaDataStart = (this.mHeader.num_patches * 4) + 6;
        byte[] retval = new byte[(getCurrentIndex() - metaDataStart)];
        System.arraycopy(this.patch_array, metaDataStart, retval, 0, retval.length);
        return retval;
    }

    public String getDescription() {
        try {
            return new String(getMetaData(), HttpURLConnectionBuilder.DEFAULT_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }

    public static final int byteArrayToInt(byte[] b) {
        return (((b[0] << 24) + ((b[1] & 255) << 16)) + ((b[2] & 255) << 8)) + (b[3] & 255);
    }

    public static byte[] readPatch(String patch) throws IOException {
        byte[] ret = new byte[((int) new File(patch).length())];
        InputStream is = new FileInputStream(patch);
        is.read(ret, 0, ret.length);
        is.close();
        return ret;
    }
}
