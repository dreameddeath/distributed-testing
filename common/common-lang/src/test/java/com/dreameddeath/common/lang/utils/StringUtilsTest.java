package com.dreameddeath.common.lang.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void isEmpty() {
        Assertions.assertThat(StringUtils.isEmpty(null)).isTrue();
        Assertions.assertThat(StringUtils.isEmpty("")).isTrue();
        Assertions.assertThat(StringUtils.isEmpty(" ")).isFalse();
        Assertions.assertThat(StringUtils.isEmpty("a")).isFalse();
    }

    @Test
    void isNotEmpty() {
        Assertions.assertThat(StringUtils.isNotEmpty(null)).isFalse();
        Assertions.assertThat(StringUtils.isNotEmpty("")).isFalse();
        Assertions.assertThat(StringUtils.isNotEmpty(" ")).isTrue();
        Assertions.assertThat(StringUtils.isNotEmpty("a")).isTrue();
    }

    @Test
    void isNotEmptyAfterTrim() {
        Assertions.assertThat(StringUtils.isNotEmptyAfterTrim(" a ")).isTrue();
        Assertions.assertThat(StringUtils.isNotEmptyAfterTrim(" ")).isFalse();
        Assertions.assertThat(StringUtils.isNotEmptyAfterTrim("\t\n")).isFalse();
    }

    @Test
    void capitalizeFirst() {
        Assertions.assertThat(StringUtils.capitalizeFirst("affiffi")).isEqualTo("Affiffi");
        Assertions.assertThat(StringUtils.capitalizeFirst("Affiffi")).isEqualTo("Affiffi");
        Assertions.assertThat(StringUtils.capitalizeFirst(null)).isNull();
        Assertions.assertThat(StringUtils.capitalizeFirst(" ")).isEqualTo(" ");
    }

    @Test
    void lowerCaseFirst() {
        Assertions.assertThat(StringUtils.lowerCaseFirst("affiffi")).isEqualTo("affiffi");
        Assertions.assertThat(StringUtils.lowerCaseFirst("Affiffi")).isEqualTo("affiffi");
        Assertions.assertThat(StringUtils.lowerCaseFirst(null)).isNull();
        Assertions.assertThat(StringUtils.lowerCaseFirst(" ")).isEqualTo(" ");
    }
}