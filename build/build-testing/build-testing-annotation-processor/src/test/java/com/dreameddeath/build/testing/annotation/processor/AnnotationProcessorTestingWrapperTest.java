package com.dreameddeath.build.testing.annotation.processor;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Set;

public class AnnotationProcessorTestingWrapperTest {

    public @interface TestingAnnot {
        String value() default "";
    }

    public class TestAnnotationProcessor extends AbstractProcessor {
        boolean generateError = false;

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            var env = processingEnv;
            for (Element element : roundEnv.getElementsAnnotatedWith(TestingAnnot.class)) {
                try {
                    String className = element.getSimpleName().toString();
                    TestingAnnot annotation = element.getAnnotation(TestingAnnot.class);
                    String filename = "test.out." + className;
                    FileObject jfo = env.getFiler().createSourceFile(filename, element);
                    Writer writer = jfo.openWriter();
                    writer.write("package test.out;\n");
                    writer.write("public class " + className + "{\n");
                    writer.write("    public final String name = \"" + annotation.value() + "\";\n");
                    writer.write("    public String value=null;\n");
                    writer.write("    public " + className + "(String in){value = in;}\n");
                    writer.write("    public String getValue(String in){return value + in;}\n");
                    writer.write("}\n");
                    if (generateError) {
                        writer.write(" ERROR IN CLASS\n");
                    }
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latest();
        }

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return Collections.singleton(TestingAnnot.class.getCanonicalName());
        }
    }

    @Test
    void simpleTest() throws Throwable {
        TestAnnotationProcessor testAnnotationProcessor = new TestAnnotationProcessor();
        AnnotationProcessorTestingWrapper wrapper = new AnnotationProcessorTestingWrapper()
                .withTempDirectoryPrefix("TestDirectory")
                .withAnnotationProcessor(testAnnotationProcessor);

        Assertions.assertThat(wrapper.getTempDirectoryPrefix()).isEqualTo("TestDirectory");
        Assertions.assertThat(wrapper.getAnnotationProcessors()).containsExactly(testAnnotationProcessor);
        AnnotationProcessorTestingWrapper.Result testing = wrapper.run("classpath:/testing");
        Assertions.assertThat(testing.getResult()).isTrue();
        Assertions.assertThat(testing.getSourceFiles()).hasSize(1);
        Assertions.assertThat(testing.getOutputManager()).isNotNull();
        Assertions.assertThat(testing.getDiagnostics().getDiagnostics()).hasSize(0);
        Assertions.assertThat(testing.hasClass("test.out.TestingIn")).isTrue();
        {
            Constructor constructor = testing.getConstructor("test.out.TestingIn", String.class);
            Assertions.assertThat(constructor).isNotNull();
            Object newInstance = constructor.newInstance("toto");
            Object value = testing.getField("test.out.TestingIn", "value").get(newInstance);
            Object resultInvoke = testing.getMethod("test.out.TestingIn", "getValue", String.class).invoke(newInstance, "Suffix");
            Assertions.assertThat(newInstance).isNotNull();
            Assertions.assertThat(newInstance.getClass()).isEqualTo(testing.getClass("test.out.TestingIn"));
            Assertions.assertThat(newInstance.getClass().getCanonicalName()).isEqualTo("test.out.TestingIn");
            Assertions.assertThat(value).isEqualTo("toto");
            Assertions.assertThat(resultInvoke).isEqualTo("totoSuffix");
        }
        {
            Assertions.assertThat(testing.hasFile("test/out/TestingIn.java")).isTrue();
            File file = testing.getFile("test/out/TestingIn.java");
            Assertions.assertThat(file).isNotNull();
            Assertions.assertThat(file).isFile();
            Assertions.assertThat(file).exists();
            Assertions.assertThat(file.toString()).startsWith(testing.getOutputDir().toString());
        }
        testing.cleanUp();
    }

    @Test
    void errorTest() throws Throwable {
        TestAnnotationProcessor testAnnotationProcessor = new TestAnnotationProcessor();
        testAnnotationProcessor.generateError = true;
        AnnotationProcessorTestingWrapper wrapper = new AnnotationProcessorTestingWrapper()
                .withTempDirectoryPrefix("TestDirectory")
                .withAnnotationProcessor(testAnnotationProcessor);
        AnnotationProcessorTestingWrapper.Result testing = wrapper.run("classpath:/testing");
        Assertions.assertThat(testing.getResult()).isFalse();
        Assertions.assertThat(testing.getDiagnostics().getDiagnostics()).hasSize(1);
    }
}