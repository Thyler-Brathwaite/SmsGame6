package com.example.smsgame6;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileHelper {


    public static boolean fileExists(Context ctx, String name) {
        File f = new File(ctx.getFilesDir(), name);
        return f.exists() && f.length() > 0;
    }

    public static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(FileOutputStream fos) throws IOException {
            super(fos);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // Do NOT write header when appending
        }
    }
}
