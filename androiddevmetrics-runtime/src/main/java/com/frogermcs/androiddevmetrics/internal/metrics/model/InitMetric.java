package com.frogermcs.androiddevmetrics.internal.metrics.model;

import androidx.annotation.NonNull;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Miroslaw Stanek on 23.01.2016.
 */
public class InitMetric {

    public Class<?> cls;
    public long initTimeMillis = 0;
    public int instanceNo = 0;
    public String threadName = "";
    public Set<InitMetric> args = new HashSet<>();
    public StackTraceElement[] traceElements;

    public long getTotalInitTime() {
        long total = initTimeMillis;
        for (InitMetric initMetric : args) {
            total += initMetric.getTotalInitTime();
        }
        return total;
    }

    public long getInitTimeWithoutArgs() {
        return initTimeMillis;
    }

    public String getSimpleClassName() {
        String className;
        if (Proxy.isProxyClass(cls)) {
            final Class<?>[] interfaces = cls.getInterfaces();
            if (interfaces.length == 1) {
                className = interfaces[0].getName();
            } else {
                className = Arrays.asList(interfaces).toString();
            }
        } else {
            className = cls.getName();
        }

        int dot = className.lastIndexOf('.');
        if (dot != -1) {
             className = className.substring(dot + 1);
        }

        if (instanceNo > 0) {
            return className + "#" + instanceNo;
        }
        return className;
    }

    public String getThreadName() {
        return threadName;
    }

    @NonNull
    @Override
    public String toString() {
        return "InitMetric{" +
                "initTimeMillis=" + initTimeMillis +
                ", cls=" + (Proxy.isProxyClass(cls) ? Arrays.asList(cls.getInterfaces()) : cls.getName()) +
                ", args=" + args +
                '}';
    }
}
