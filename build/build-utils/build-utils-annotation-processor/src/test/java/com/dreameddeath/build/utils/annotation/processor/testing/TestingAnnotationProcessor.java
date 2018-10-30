/*
 * Copyright Christophe Jeunesse
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.dreameddeath.build.utils.annotation.processor.testing;

import com.dreameddeath.build.utils.annotation.processor.AbstractAnnotationProcessor;
import com.dreameddeath.build.utils.annotation.processor.exception.AnnotationProcessorException;
import com.dreameddeath.build.utils.annotation.processor.reflection.*;
import com.google.common.base.Preconditions;
import com.squareup.javapoet.*;
import org.assertj.core.api.Assertions;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Created by Christophe Jeunesse on 06/03/2015.
 */
public class TestingAnnotationProcessor extends AbstractAnnotationProcessor {

    private int nbProcessedClasses = 0;

    public int getNbProcessedClasses() {
        return nbProcessedClasses;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        Elements elementUtils = processingEnv.getElementUtils();
        AnnotationElementType.CURRENT_ELEMENT_UTILS.set(elementUtils);
        for (Element baseElem : roundEnv.getElementsAnnotatedWith(TestingAnnotation.class)) {
            nbProcessedClasses++;
            try {
                AnnotatedInfo elemInfo = AnnotationElementType.getInfoOf(baseElem);
                if (baseElem instanceof TypeElement) {
                    AbstractClassInfo classInfo = AbstractClassInfo.getClassInfo((TypeElement) baseElem);
                    if (classInfo.getSimpleName().equals("ExtendsGenericClassWithGenerics")) {
                        ClassInfo classWithGenericsInfo = ((ClassInfo) classInfo).getSuperClass();
                        FieldInfo field = classWithGenericsInfo.getFieldByName("value");
                        MethodInfo info = classWithGenericsInfo.getMethod("classWithTreq", field.getType());
                        Assertions.assertThat(info).isNotNull();
                        info = classWithGenericsInfo.getMethod("setStatus", classWithGenericsInfo.getFieldByName("status").getType());
                        Assertions.assertThat(info).isNotNull();

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
                        Assertions.assertThat(annot.value()).isEqualTo("toto2");
                    }
                }
                Annotation[] annotArray = elemInfo.getAnnotations();
                Assertions.assertThat(annotArray.length).isEqualTo(1);
                for (Annotation annot : annotArray) {
                    AbstractClassInfo annotInfo = AbstractClassInfo.getClassInfo(annot.annotationType());
                    if (annotInfo.getSimpleName().equals("TestingAnnotation")) {
                        Assertions.assertThat(annotInfo.getDeclaredMethods()).hasSize(1);
                        Assertions.assertThat(annotInfo.getDeclaredMethod("value").getReturnType().getMainType().getSimpleName()).isEqualTo("String");
                    }
                }


                if (elemInfo instanceof InterfaceInfo) {
                    List<InterfaceInfo> parentInterfaces = ((InterfaceInfo) elemInfo).getParentInterfaces();
                    printNote("Nb interfaces " + parentInterfaces.size());
                }
            } catch (AnnotationProcessorException e) {
                throw new RuntimeException(e);
            }


            Preconditions.checkArgument(baseElem instanceof TypeElement, "The element %s must be of type TypeElement", baseElem);
            AbstractClassInfo classInfo = AbstractClassInfo.getClassInfo((TypeElement) baseElem);
            this.writeFile(
                    JavaFile.builder(classInfo.getPackageInfo().getName(),
                            TypeSpec.classBuilder(classInfo.getSimpleName() + "Generated")
                                    .addField(FieldSpec.builder(
                                            ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class)),
                                            "SRC_CLASS",
                                            Modifier.PRIVATE, Modifier.STATIC
                                    )
                                            .initializer("$T.class", classInfo.getClassName())
                                            .build())
                                    .build()
                    ).build()
                    , messager);


        }

        try {
            //Tests around TestClass
            Assertions.assertThat(
                    AbstractClassInfo.getEffectiveGenericType(
                            AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestClass"),
                            AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 0)
            )
                    .isEqualTo(AbstractClassInfo.getClassInfo(String.class));
            Assertions.assertThat(
                    AbstractClassInfo.getEffectiveGenericType(
                            AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestClass"),
                            AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 1)
            )
                    .isEqualTo(AbstractClassInfo.getClassInfo(Integer.class));
            //Tests around TestClassSubInterface
            Assertions.assertThat(
                    AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestClassSubInterface"),
                            AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubRoot"), 0)
            )
                    .isEqualTo(AbstractClassInfo.getClassInfo(String.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestClassSubInterface"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubRoot"), 1)).isEqualTo(AbstractClassInfo.getClassInfo(Double.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestClassSubInterface"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 0)).isEqualTo(AbstractClassInfo.getClassInfo(Double.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestClassSubInterface"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 1)).isEqualTo(AbstractClassInfo.getClassInfo(String.class));
            //Tests around TestRootClass
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRootClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 0)).isEqualTo(AbstractClassInfo.getClassInfo(Integer.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRootClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 1)).isNull();
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRootClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot2"), 0)).isNull();
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRootClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot2"), 1)).isNull();
            //Tests around TestSubClass
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRootClass"), 0)).isEqualTo(AbstractClassInfo.getClassInfo(Double.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 0)).isEqualTo(AbstractClassInfo.getClassInfo(Integer.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 1)).isEqualTo(AbstractClassInfo.getClassInfo(Double.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot2"), 0)).isEqualTo(AbstractClassInfo.getClassInfo(Double.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubClass"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot2"), 1)).isEqualTo(AbstractClassInfo.getClassInfo(Double.class));
            Assertions.assertThat(AbstractClassInfo.getEffectiveGenericType(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestSubClassComplex"), AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestRoot"), 0)).isEqualTo(AbstractClassInfo.getClassInfo("com.test.GenericsClassInfoTest$TestExemple"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(TestingAnnotation.class.getCanonicalName());
    }
}

