package com.frogermcs.androiddevmetrics.internal.metrics;

import com.frogermcs.androiddevmetrics.internal.MetricDescription;
import com.frogermcs.androiddevmetrics.internal.metrics.model.InitMetric;
import com.frogermcs.androiddevmetrics.internal.ui.interceptor.UIInterceptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Miroslaw Stanek on 24.01.2016.
 */
public class InitManager {
    private static class Holder {
        static final InitManager INSTANCE = new InitManager();
    }

    public static InitManager getInstance() {
        return Holder.INSTANCE;
    }

    public final Set<OnMetricsDataListener> dataListeners = new HashSet<>();

    public final LinkedHashMap<String, InitMetric> initializedMetrics = new LinkedHashMap<>();
    public final LinkedHashMap<String, Integer> initCounter = new LinkedHashMap<>();

    public void addInitMetric(Class<?> initializedClass, Object[] args, long initTimeMillis) {
        InitMetric initMetric = new InitMetric();
        initMetric.initTimeMillis = initTimeMillis;
        initMetric.cls = initializedClass;
        initMetric.threadName = Thread.currentThread().getName();
        initMetric.traceElements = Thread.currentThread().getStackTrace();

        String simpleName = initializedClass.getName();
        if (!initializedMetrics.containsKey(simpleName)) {
            putInitMetric(simpleName, initMetric);
            for (Object arg : args) {
                if (arg == null) continue;
                String argClassSimpleName = arg.getClass().getName();
                InitMetric argMetrics = initializedMetrics.get(argClassSimpleName);
                if (argMetrics != null) {
                    initMetric.args.add(argMetrics);
                    initializedMetrics.remove(argClassSimpleName);
                }
            }
            initMetric.instanceNo = 0;
            initCounter.put(simpleName, 0);
        } else {
            int counterVal = initCounter.get(simpleName) + 1;
            initCounter.put(simpleName, counterVal);
            initMetric.instanceNo = counterVal;
            putInitMetric(simpleName + "#" + counterVal, initMetric);
        }
    }

    private void putInitMetric(String key, InitMetric initMetric) {
        initializedMetrics.put(key, initMetric);
        for (OnMetricsDataListener dataListener : dataListeners) {
            dataListener.onInitNewMetricRecorded(initMetric);
        }
    }

    public List<MetricDescription> getListOfMetricDescriptions(UIInterceptor interceptor) {
        List<MetricDescription> metricDescriptions = new ArrayList<>();
        List<InitMetric> displayList = interceptor.intercept(new ArrayList<>(InitManager.getInstance().initializedMetrics.values()));
        for (InitMetric initMetric : displayList) {
            metricDescriptions.add(MetricDescription.InitFromMetric(initMetric));
        }
        return metricDescriptions;
    }

    public void addMetricsDataListener(OnMetricsDataListener onDataListener) {
        dataListeners.add(onDataListener);
    }

    public void removeMetricsDataListener(OnMetricsDataListener onDataListener) {
        dataListeners.remove(onDataListener);
    }
}
