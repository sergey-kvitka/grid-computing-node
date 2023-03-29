package com.kvitka.gridnode.controller;

import com.kvitka.gridnode.service.JarExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
public class SubtaskController {

    private final AtomicInteger counter = new AtomicInteger(1);

    private final JarExecutorService jarExecutorService;

    @GetMapping("/ping")
    public ResponseEntity<Integer> ping() {
        return ResponseEntity.ok(0);
    }

    @PostMapping("/exec")
    public List<String> executeJar(
            @RequestParam("jar") MultipartFile jarFile,
            @RequestParam("args") MultipartFile argFile
    ) throws IOException {
        return jarExecutorService.executeJar(jarFile, argFile, counter.getAndIncrement());
    }
}
