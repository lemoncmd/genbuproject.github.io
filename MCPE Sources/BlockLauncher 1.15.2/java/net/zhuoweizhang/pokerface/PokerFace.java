package net.zhuoweizhang.pokerface;

import android.os.Build.VERSION;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

public class PokerFace {
    public static final int PROT_EXEC = 4;
    public static final int PROT_NONE = 0;
    public static final int PROT_READ = 1;
    public static final int PROT_WRITE = 2;
    public static final int _SC_PAGESIZE = 39;

    public static native int mprotect(long j, long j2, int i);

    public static native long sysconf(int i);

    public static ByteBuffer createDirectByteBuffer(long address, long length) throws Exception {
        if (VERSION.SDK_INT >= 18) {
            try {
                return createDirectByteBufferNew(address, length);
            } catch (NoSuchMethodException e) {
            }
        }
        Class cls = Class.forName("java.nio.ReadWriteDirectByteBuffer");
        Class[] clsArr = new Class[PROT_WRITE];
        clsArr[PROT_NONE] = Integer.TYPE;
        clsArr[PROT_READ] = Integer.TYPE;
        Constructor cons = cls.getDeclaredConstructor(clsArr);
        cons.setAccessible(true);
        Object[] objArr = new Object[PROT_WRITE];
        objArr[PROT_NONE] = Integer.valueOf((int) address);
        objArr[PROT_READ] = Integer.valueOf((int) length);
        return (ByteBuffer) cons.newInstance(objArr);
    }

    private static ByteBuffer createDirectByteBufferNew(long address, long length) throws Exception {
        Class cls = Class.forName("java.nio.DirectByteBuffer");
        Class[] clsArr = new Class[PROT_WRITE];
        clsArr[PROT_NONE] = Long.TYPE;
        clsArr[PROT_READ] = Integer.TYPE;
        Constructor cons = cls.getDeclaredConstructor(clsArr);
        cons.setAccessible(true);
        Object[] objArr = new Object[PROT_WRITE];
        objArr[PROT_NONE] = Long.valueOf(address);
        objArr[PROT_READ] = Integer.valueOf((int) length);
        return (ByteBuffer) cons.newInstance(objArr);
    }

    public static void init() {
    }

    static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("mcpelauncher_tinysubstrate");
    }
}
