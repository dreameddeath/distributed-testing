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

import com.dreameddeath.build.testing.annotation.processor.AnnotationProcessorTestingWrapper;
import com.dreameddeath.build.utils.annotation.processor.testing.TestingAnnotationProcessor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

@SuppressWarnings("WeakerAccess")
public class AnnotationElementTypeTest {
    @Test
    public void testAnnotationUtils() throws Exception {
        TestingAnnotationProcessor processor = new TestingAnnotationProcessor();
        AnnotationProcessorTestingWrapper annotTester = new AnnotationProcessorTestingWrapper();
        annotTester.
                withAnnotationProcessor(processor).
                withTempDirectoryPrefix("AnnotationElementTypeTest");
        URL sourceFiles = this.getClass().getClassLoader().getResource("sourceFiles");
        Assertions.assertThat(sourceFiles).isNotNull();
        AnnotationProcessorTestingWrapper.Result result = annotTester.run(sourceFiles.getPath());
        Assertions.assertThat(result.getResult()).isTrue();
        Assertions.assertThat(processor.getNbProcessedClasses()).isEqualTo(3);
        result.cleanUp();
    }
}