package ru.kpfu.itis.group400.stashkov.controller.metrics;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.group400.stashkov.dto.MethodPercentileInfo;
import ru.kpfu.itis.group400.stashkov.dto.MetricCount;
import ru.kpfu.itis.group400.stashkov.service.metrics.BenchMarker;
import ru.kpfu.itis.group400.stashkov.service.metrics.Counter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metrics")
@AllArgsConstructor
public class MetricsController {

    private final Counter counter;
    private final BenchMarker benchmarker;

    @GetMapping("/count")
    public Map<String, MetricCount> getMetricCount() {
        return counter.getStatistic();
    }

    @GetMapping("/bench")
    public List<MethodPercentileInfo> getMethodPercentile(@RequestParam(required = false, name = "percentile") String percentile,
                                                          @RequestParam(required = false, name = "methodName")   String methodName){
        Integer percentileInt;
        if (percentile == null  ) {
            return benchmarker.getStatistics(methodName, null);
        }
        try {
            percentileInt = Integer.parseInt(percentile);
        } catch (NumberFormatException e) {
            return List.of(new MethodPercentileInfo("not valid percentile, use number from 0 to 100", 0d));
        }
        return benchmarker.getStatistics(methodName, percentileInt);

    }

}
