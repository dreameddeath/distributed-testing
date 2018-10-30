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

package com.dreameddeath.build.utils.annotation.processor;

import com.dreameddeath.build.utils.annotation.processor.reflection.*;
import com.dreameddeath.build.utils.annotation.processor.testing.ClassWithGenerics;
import com.dreameddeath.build.utils.annotation.processor.testing.TestingPackageAnnot;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Created by Christophe Jeunesse on 22/10/2015.
 */
@SuppressWarnings("WeakerAccess")
public class ClassInfoTest {
    @Test
    public void test() {
        ClassInfo classInfo = (ClassInfo) AbstractClassInfo.getClassInfo(ClassWithGenerics.class);
        FieldInfo field = classInfo.getFieldByName("value");
        MethodInfo info = classInfo.getMethod("classWithTreq", field.getType());
        Assertions.assertThat(info).isNotNull();
        Assertions.assertThat(classInfo.getPackageInfo()).isNotNull();
        Assertions.assertThat(classInfo.getPackageInfo().getParentPackage()).isNotNull();
        TestingPackageAnnot annot = null;
        PackageInfo currPackage = classInfo.getPackageInfo();
        while (currPackage != null) {
            annot = currPackage.getAnnotation(TestingPackageAnnot.class);
            if (annot != null) {
                break;
            }
            currPackage = currPackage.getParentPackage();
        }
        Assertions.assertThat(annot).isNotNull();
        Assertions.assertThat(annot.value()).isEqualTo("toto");
    }
}
