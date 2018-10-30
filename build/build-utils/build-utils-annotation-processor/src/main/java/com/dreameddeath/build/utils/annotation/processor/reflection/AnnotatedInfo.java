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

package com.dreameddeath.build.utils.annotation.processor.reflection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Christophe Jeunesse on 07/03/2015.
 */
@SuppressWarnings("WeakerAccess")
public class AnnotatedInfo {
    private AnnotatedElement annotElt = null;
    private Element element = null;
    private String javaDoc = null;

    public AnnotatedInfo(AnnotatedElement elt) {
        annotElt = elt;
    }

    public AnnotatedInfo(Element elt) {
        element = elt;
        if (AnnotationElementType.CURRENT_ELEMENT_UTILS.get() != null) {
            javaDoc = AnnotationElementType.CURRENT_ELEMENT_UTILS.get().getDocComment(elt);
        }
    }

    public <A extends Annotation> A getAnnotation(Class<A> clazz) {
        if (annotElt != null) {
            return annotElt.getAnnotation(clazz);
        } else {
            return element.getAnnotation(clazz);
        }
    }

    public <A extends Annotation> A[] getAnnotationByType(Class<A> clazz) {
        if (annotElt != null) {
            return annotElt.getAnnotationsByType(clazz);
        } else {
            return element.getAnnotationsByType(clazz);
        }
    }

    public Annotation[] getAnnotations() {
        if (annotElt != null) {
            return annotElt.getAnnotations();
        } else {
            List<Annotation> annotations = new ArrayList<>(element.getAnnotationMirrors().size());
            for (AnnotationMirror annotMirror : element.getAnnotationMirrors()) {
                AnnotationInfo annotInfo = (AnnotationInfo) AbstractClassInfo.getClassInfo((TypeElement) annotMirror.getAnnotationType().asElement());
                annotations.addAll(Arrays.asList(getAnnotationByType(annotInfo.getCurrentClass())));
            }
            return annotations.toArray(new Annotation[0]);
        }
    }

    public String getJavaDoc() {
        return javaDoc;
    }
}
