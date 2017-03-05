package net.zhuoweizhang.mcpelauncher;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

class Scrambler {
    static native void nativeScramble(ByteBuffer byteBuffer, MpepInfo mpepInfo);

    Scrambler() {
    }

    static Reader scramble(byte[] input, MpepInfo info) throws IOException {
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(input.length);
        dataBuffer.put(input);
        dataBuffer.rewind();
        nativeScramble(dataBuffer, info);
        byte[] output = new byte[input.length];
        dataBuffer.get(output);
        return new StringReader(new String(output, Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET)));
    }
}
