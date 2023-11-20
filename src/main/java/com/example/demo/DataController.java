package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class DataController {

    private final Logger logger = LoggerFactory.getLogger(DataController.class);
    private final ConcurrentHashMap<String, String> dataStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> userDataSize = new ConcurrentHashMap<>();
    private final long MAX_SIZE_PER_USER = 10 * 1024 * 1024; // Пример: 10MB на пользователя

    @PostMapping("/set/{userID}/{id}")
    public ResponseEntity<String> setData(@PathVariable String userID, @PathVariable String id, @RequestBody String data) {
        String key = userID + ":" + id;
        long newSize = userDataSize.getOrDefault(userID, 0L) + data.length();

        if (newSize > MAX_SIZE_PER_USER) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Data limit exceeded for user");
        }

        dataStore.put(key, data);
        userDataSize.put(userID, newSize);
        logger.info("Set data for key: {}", key);
        return ResponseEntity.ok("Data stored successfully");
    }

    @GetMapping("/get/{userID}/{id}")
    public ResponseEntity<String> getData(@PathVariable String userID, @PathVariable String id) {
        String key = userID + ":" + id;
        String data = dataStore.get(key);

        if (data == null) {
            logger.warn("Data not found for key: {}", key);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found");
        }
        logger.info("Data retrieved for key: {}", key);
        return ResponseEntity.ok(data);
    }
}