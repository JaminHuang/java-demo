package com.demo.ibatx.util;

import java.io.*;
import java.util.Objects;

public class FileUtils {

    public static void writeFile(File file, String content) throws Exception {
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(file))) {
            printWriter.write(content);
        }
    }

    public static String readFile(File file) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        }
    }

    public static String readStream(InputStream inputStream) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        }
    }


    public static InputStream getResourceAsStream(String fileName) throws Exception {
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (Objects.nonNull(inputStream)) {
            return inputStream;
        }
        return new FileInputStream(fileName);
    }
}
