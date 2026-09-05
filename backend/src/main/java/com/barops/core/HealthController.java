package com.barops.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint test đơn giản — dùng để xác nhận backend đã chạy đúng lúc setup lần đầu.
 * GET http://localhost:8080/api/health
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "barops-backend",
            "time", Instant.now().toString()
        );
    }
}
