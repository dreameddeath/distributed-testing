package com.dreameddeath.common.lang.utils;

public class LogUtils {

    @SuppressWarnings("WeakerAccess")
    public static Class getCallerClass(int level) throws ClassNotFoundException {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String rawFQN = stElements[level + 2].toString().split("\\(")[0];
        return Thread.currentThread().getContextClassLoader().loadClass(rawFQN.substring(0, rawFQN.lastIndexOf('.')));
    }
}
