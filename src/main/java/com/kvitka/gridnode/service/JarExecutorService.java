package com.kvitka.gridnode.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Service
public class JarExecutorService {

    @Value("${execution-properties.directory}")
    private String directoryName;

    public List<String> executeJar(MultipartFile jarFile, MultipartFile argFile, int index) throws IOException {
        List<String> tempFiles = new ArrayList<>();
        String jarName = createFile(jarFile, "jar", index, "jar", tempFiles);

        InputStream inputStream = new ByteArrayInputStream(argFile.getBytes());
        Scanner scanner = new Scanner(inputStream).useDelimiter("\n");
        List<String> args = new ArrayList<>();
        while (scanner.hasNext()) args.add(scanner.next());

        Process process = Runtime.getRuntime().exec(
                "java -jar %s %s".formatted(directoryName + "/" + jarName, String.join(" ", args)));

        inputStream = process.getInputStream();
        scanner = new Scanner(inputStream).useDelimiter("\r\n");
        List<String> result = new ArrayList<>();
        while (scanner.hasNext()) result.add(scanner.next());

        if (!result.isEmpty()) {
            process.destroy();
            deleteFiles(tempFiles);
            System.out.println("(" + new Date() + ")Success! " + args + " " + result);
            return result;
        }

        inputStream = process.getErrorStream();
        scanner = new Scanner(inputStream).useDelimiter("\r\n");
        while (scanner.hasNext()) result.add(scanner.next());
        process.destroy();
        deleteFiles(tempFiles);
        throw new RuntimeException("Jar execution failed (Error message: \n\t%s)"
                .formatted(String.join("\n\t", result)));
    }

    @SuppressWarnings("SameParameterValue")
    private String createFile(MultipartFile multipartFile, String filename, int index,
                              String extension, List<String> fileRegistry)
            throws IOException {
        byte[] bytes = multipartFile.getBytes();
        String name = filename + index + "." + extension;
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(directoryName + "/" + name));
        stream.write(bytes);
        stream.close();
        fileRegistry.add(name);
        return name;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteFiles(List<String> fileRegistry) {
        for (String filePath : fileRegistry) new File(directoryName + "/" + filePath).delete();
    }
}
