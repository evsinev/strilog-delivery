package com.strilog.delivery.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SafeFiles {

    private static final Logger LOG = LoggerFactory.getLogger(SafeFiles.class);

    public static List<File> listFilesSorted(File aDir, FileFilter aFilter) {
        List<File> files = listFiles(aDir, aFilter);
        Collections.sort(files);
        return files;
    }

    public static List<File> listFiles(File aDir, FileFilter aFilter) {
        if (!aDir.exists()) {
            return Collections.emptyList();
        }

        File[] files = aDir.listFiles(aFilter);
        if (files == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(files);
    }

    public static String readLines(File aFile) {
        StringBuilder sb = new StringBuilder();
        try {
            try (LineNumberReader in = createLineNumberReader(aFile)) {
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load file " + aFile.getAbsolutePath(), e);
        }
    }

    public static File createDir(File aDir) {
        if (aDir.exists()) {
            return aDir;
        }

        if (!aDir.mkdirs()) {
            throw new IllegalStateException("Cannot create dir " + aDir.getAbsolutePath());
        }

        return aDir;
    }

    public static File createDirs(File aBaseDir, String ... aSubDirs) {
        File dir = aBaseDir;
        for (String subDir : aSubDirs) {
            dir = createDir(new File(dir, subDir));
        }
        return dir;
    }

    public static InputStreamReader createInputStreamReader(File aFile) throws IOException {
        return new InputStreamReader(Files.newInputStream(aFile.toPath()), UTF_8);
    }

    public static LineNumberReader createLineNumberReader(File aFile) throws IOException {
        return new LineNumberReader(createInputStreamReader(aFile));
    }

    public static OutputStreamWriter createOutputStreamWriter(File aFile) throws IOException {
        return new OutputStreamWriter(Files.newOutputStream(aFile.toPath()), UTF_8);
    }

    public static void deleteFile(File aFile) {
        if (!aFile.delete()) {
            LOG.error("Cannot delete file {}", aFile.getAbsolutePath());
        }
    }
}
