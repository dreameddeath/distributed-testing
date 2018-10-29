/*
 * Copyright Christophe Jeunesse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dreameddeath.common.lang.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Christophe Jeunesse on 03/03/2016.
 */

@SuppressWarnings("ALL")
public class ClassUtilsTest {


    @Deprecated
    public interface TestRoot<TOTO, TUTU> {
    }

    public interface TestRoot2<A, B> {
    }

    public interface TestSubRoot<TITI, TATA> extends TestRoot<TATA, TITI> {
    }

    public class TestClass implements TestRoot<String, Integer> {
    }

    public class TestClassSubInterface implements TestSubRoot<String, Double> {
    }

    public class TestRootClass<TOTO extends Number> implements TestRoot<Integer, TOTO>, TestRoot2<TOTO, TOTO> {
    }

    public class TestSubClass extends TestRootClass<Double> {
    }


    @Test
    void getClassWithAnnotation() {
        Assertions.assertThat(ClassUtils.getClassWithAnnotation(TestClass.class, Deprecated.class)).isEqualTo(TestRoot.class);
    }


    @Test
    public void getFirstParentPackage() throws Throwable {
        var contextClassLoader = ClassUtils.getEffectiveClassLoader();
        Class<?> aClass = contextClassLoader.loadClass("test.sub.packagename.sub2.Dummy");
        Package childPackage = contextClassLoader.getDefinedPackage("test.sub.packagename.sub2");
        Assertions.assertThat(childPackage).isNotNull();
        Package parentPackage = ClassUtils.getFirstParentPackage(childPackage);
        Assertions.assertThat(parentPackage).isNotNull();
        Assertions.assertThat(parentPackage.getName()).isEqualTo("test.sub");
    }


    @Test
    public void testGetEffectiveGenericType() {
        try {
            ClassUtils.getEffectiveGenericType(TestClass.class, TestRoot2.class, 1);
            Assertions.fail("Should have raised an error");
        } catch (RuntimeException e) {
            //Ok
        }
        //Tests around TestClass
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestClass.class, TestRoot.class, 0)).isEqualTo(String.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestClass.class, TestRoot.class, 1)).isEqualTo(Integer.class);
        //Tests around TestClassSubInterface
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestClassSubInterface.class, TestSubRoot.class, 0)).isEqualTo(String.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestClassSubInterface.class, TestSubRoot.class, 1)).isEqualTo(Double.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestClassSubInterface.class, TestRoot.class, 0)).isEqualTo(Double.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestClassSubInterface.class, TestRoot.class, 1)).isEqualTo(String.class);
        //Tests around TestRootClass
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestRootClass.class, TestRoot.class, 0)).isEqualTo(Integer.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestRootClass.class, TestRoot.class, 1)).isNull();
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestRootClass.class, TestRoot2.class, 0)).isNull();
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestRootClass.class, TestRoot2.class, 1)).isNull();
        //Tests around TestSubClass
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestSubClass.class, TestRootClass.class, 0)).isEqualTo(Double.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestSubClass.class, TestRoot.class, 0)).isEqualTo(Integer.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestSubClass.class, TestRoot.class, 1)).isEqualTo(Double.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestSubClass.class, TestRoot2.class, 0)).isEqualTo(Double.class);
        Assertions.assertThat(ClassUtils.getEffectiveGenericType(TestSubClass.class, TestRoot2.class, 1)).isEqualTo(Double.class);
    }


}