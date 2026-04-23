package ru.kpfu.itis.group400.stashkov.service.metrics;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.group400.stashkov.dto.MethodPercentileInfo;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BenchMarker {

    private final static int defaultPercentile = 50;
    private final Map<String, List<Double>> times = new ConcurrentHashMap<>();

    public void saveNewTime(String methodName, double durationSeconds) {
        times.computeIfAbsent(methodName, k -> new ArrayList<>()).add(durationSeconds);
    }

    public List<MethodPercentileInfo> getStatistics(String methodName, Integer percentile) {
        List<MethodPercentileInfo> result = new ArrayList<>();
        if (percentile == null) percentile = defaultPercentile;
        if (methodName == null || methodName.isBlank()) {
            for (var entry : times.entrySet()) {
                result.add(calculatePercentileInfo(entry.getKey(), entry.getValue(), percentile));
            }
        } else {
            List<Double> values = times.getOrDefault(methodName, List.of());
            result.add(calculatePercentileInfo(methodName, values, percentile));
        }
        return result;
    }

    private MethodPercentileInfo calculatePercentileInfo(String methodName, List<Double> values, int percentile) {
        if (values == null || values.isEmpty()) {
            return new MethodPercentileInfo(methodName, 0.0);
        }
        double position = (percentile / 100.0) * (values.size() - 1);
        int index = (int) Math.round(position);
        index = Math.max(0, Math.min(index, values.size() - 1));
        double percentileValue = values.get(index);
        // округление до 3 знаков для читаемости (опционально)
        percentileValue = Math.round(percentileValue * 1000.0) / 1000.0;
        return new MethodPercentileInfo(methodName, percentileValue);
    }
}