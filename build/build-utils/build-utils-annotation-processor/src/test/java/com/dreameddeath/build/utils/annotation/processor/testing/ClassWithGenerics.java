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

package com.dreameddeath.build.utils.annotation.processor.testing;

import java.io.Closeable;
import java.util.Map;

/**
 * Created by Christophe Jeunesse on 22/10/2015.
 */
public abstract class ClassWithGenerics<TREQ, TRES extends Map<String, String> & Closeable> implements GenericsTestExistingClass<TREQ, TRES> {
    private TREQ value;

    public <T extends ClassWithGenerics<TREQ, TRES>> T classWithTreq(TREQ test) {
        value = test;
        return null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private Status status;

    public enum Status {
        OK,
        KO
    }
}
