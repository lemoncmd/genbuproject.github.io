package net.zhuoweizhang.mcpelauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class CoffeeScriptCompiler {

    private static class TranslateThread implements Runnable {
        public Throwable error;
        public String input;
        public boolean literate;
        public String output;

        public TranslateThread(String input, boolean literate) {
            this.input = input;
            this.literate = literate;
        }

        public void run() {
            try {
                this.output = CoffeeScriptCompiler.compileForReal(this.input, this.literate);
            } catch (Exception e) {
                this.error = e;
            }
        }
    }

    public static boolean isCoffeeScript(File input) {
        return input.getName().toLowerCase().endsWith(".coffee") || isLiterateCoffeeScript(input);
    }

    public static boolean isLiterateCoffeeScript(File input) {
        return input.getName().toLowerCase().endsWith(".litcoffee");
    }

    public static String outputName(String input) {
        return input.substring(0, input.lastIndexOf(".")) + ".js";
    }

    public static void compileFile(File input, File output) throws IOException {
        InputStream is = new FileInputStream(input);
        byte[] data = new byte[((int) input.length())];
        is.read(data);
        is.close();
        Charset utf8 = Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET);
        String outputString = compile(new String(data, utf8), isLiterateCoffeeScript(input));
        OutputStream os = new FileOutputStream(output);
        os.write(outputString.getBytes(utf8));
        os.close();
    }

    public static String compile(String input, boolean literate) {
        System.gc();
        TranslateThread parseRunner = new TranslateThread(input, literate);
        Thread t = new Thread(Thread.currentThread().getThreadGroup(), parseRunner, "BlockLauncher parse thread", 262144);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
        }
        System.gc();
        if (parseRunner.error == null) {
            return parseRunner.output;
        }
        RuntimeException back;
        if (parseRunner.error instanceof RuntimeException) {
            back = (RuntimeException) parseRunner.error;
        } else {
            back = new RuntimeException(parseRunner.error);
        }
        throw back;
    }

    private static String compileForReal(String input, boolean literate) {
        throw new RuntimeException("CoffeeScript compiler has been removed");
    }
}
