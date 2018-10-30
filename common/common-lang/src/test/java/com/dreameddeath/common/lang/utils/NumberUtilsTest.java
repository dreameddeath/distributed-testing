package com.dreameddeath.common.lang.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberUtilsTest {

    @Test
    void asInt() {
        Assertions.assertThat(NumberUtils.asInt("1")).isEqualTo(1);
        Assertions.assertThat(NumberUtils.asInt(1119.0D)).isEqualTo(1119);
        Assertions.assertThat(NumberUtils.asInt(0.9)).isEqualTo(0);
    }

    @Test
    void asLong() {
        Assertions.assertThat(NumberUtils.asLong("1")).isEqualTo(1L);
        Assertions.assertThat(NumberUtils.asLong(10.0D)).isEqualTo(10L);
        Assertions.assertThat(NumberUtils.asLong(0.9)).isEqualTo(0L);
    }
}