package com.jakewharton;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

class Streams {
    Streams() {
    }

    static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[EnchantType.pickaxe];
            while (true) {
                int count = reader.read(buffer);
                if (count == -1) {
                    break;
                }
                writer.write(buffer, 0, count);
            }
            String stringWriter = writer.toString();
            return stringWriter;
        } finally {
            reader.close();
        }
    }
}
