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

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * Created by Christophe Jeunesse on 24/04/2015.
 */
public class AnnotationInfo extends InterfaceInfo {

    AnnotationInfo(Class elt) {
        super(elt);
    }

    AnnotationInfo(TypeElement elt) {
        super(elt);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Annotation> getCurrentClass() {
        return super.getCurrentClass();
    }
}
