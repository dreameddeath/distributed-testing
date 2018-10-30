package com.dreameddeath.common.lang.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LogUtilsTest {
    private static class SubClass {
        private Class<?> subMethod(int level) throws ClassNotFoundException {
            return LogUtils.getCallerClass(level);
        }

        private static Class<?> subMethodStatic(int level) throws ClassNotFoundException {
            return LogUtils.getCallerClass(level);
        }

    }

    @Test
    void getCallerClass() throws Throwable {
        SubClass subClass = new SubClass();
        Assertions.assertThat(subClass.subMethod(0)).isEqualTo(SubClass.class);
        Assertions.assertThat(subClass.subMethod(1)).isEqualTo(LogUtilsTest.class);
        Assertions.assertThat(SubClass.subMethodStatic(0)).isEqualTo(SubClass.class);
        Assertions.assertThat(SubClass.subMethodStatic(1)).isEqualTo(LogUtilsTest.class);
    }
}