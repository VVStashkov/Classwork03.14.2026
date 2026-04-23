package ru.kpfu.itis.group400.stashkov.service.metrics;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.group400.stashkov.dto.MetricCount;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class Counter {

    private final Map<String, AtomicInteger> successCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failCount = new ConcurrentHashMap<>();

    public void incrementSuccess(String methodName) {
        successCount.computeIfAbsent(methodName, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public void incrementFail(String methodName) {
        failCount.computeIfAbsent(methodName, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public Map<String, MetricCount> getStatistic() {
        Map<String, MetricCount> map = new HashMap<>();
        for (String  methodName : successCount.keySet()) {
            map.put(methodName, new MetricCount(
                    successCount.getOrDefault(methodName, new AtomicInteger(0)).get(),
                    failCount.getOrDefault(methodName, new AtomicInteger(0)).get()
            ));
        }
        return map;
    }
}
