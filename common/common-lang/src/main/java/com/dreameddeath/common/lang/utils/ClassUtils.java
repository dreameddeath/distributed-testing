/*
 *
 *  * Copyright Christophe Jeunesse
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.dreameddeath.common.lang.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Created by Christophe Jeunesse on 20/10/2015.
 */

public class ClassUtils {

    private static Deque<Class<?>> getAncestorsPath(Class<?> currentClass, Class<?> ancestorClass) {
        Deque<Class<?>> foundList = null;
        if (currentClass.equals(ancestorClass)) {
            foundList = new LinkedList<>();
        } else if (ancestorClass.isInterface()) {
            foundList = Arrays.stream(currentClass.getInterfaces())
                    .filter(ancestorClass::isAssignableFrom)
                    .map(it -> getAncestorsPath(it, ancestorClass))
                    .filter(list -> list.size() > 0)
                    .findFirst()
                    .orElse(null);
        }
        if (foundList == null) {
            var parentClass = currentClass.getSuperclass();
            if ((parentClass != null) && ancestorClass.isAssignableFrom(parentClass)) {
                var parentLookup = getAncestorsPath(parentClass, ancestorClass);
                if (parentLookup.size() > 0) {
                    foundList = parentLookup;
                }
            }
        }
        if (foundList != null) {
            foundList.addFirst(currentClass);
            return foundList;
        } else {
            return new LinkedList<>();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static Class<?> getEffectiveGenericType(Class<?> clazz, Class<?> parameterizedClassToLookup, int pos) {
        var stack = getAncestorsPath(clazz, parameterizedClassToLookup);
        if (stack.size() == 0) {
            throw new RuntimeException("Cannot find path from class " + clazz.getName() + " to ancestor class " + parameterizedClassToLookup.getName());
        } else if (stack.size() == 1) {
            throw new RuntimeException("Ancestor and ref class are both the same");
        }
        var currParent = stack.pollLast();
        int currParentParameterPos = pos;
        var iterator = stack.descendingIterator();
        while (iterator.hasNext()) {
            var currChild = iterator.next();
            Type parentGenericType = null;
            //Everything is a class
            if (currParent != null && !currParent.isInterface()) {
                parentGenericType = currChild.getGenericSuperclass();
            } else if (currChild.isInterface()) {
                parentGenericType = currChild.getGenericInterfaces()[0];
            }
            //Parent is interface
            else {
                int interfacePos = 0;
                for (var interfaceClass : currChild.getInterfaces()) {
                    if (interfaceClass.equals(currParent)) {
                        parentGenericType = currChild.getGenericInterfaces()[interfacePos];
                        break;
                    }
                    interfacePos++;
                }
            }

            if (!(parentGenericType instanceof ParameterizedType)) {
                throw new RuntimeException("The class " + parentGenericType + " isn't parameterized");
            } else {
                var type = ((ParameterizedType) parentGenericType).getActualTypeArguments()[currParentParameterPos];
                if (type instanceof Class) {
                    return (Class) type;
                } else if (type instanceof TypeVariable) {
                    var typeName = ((TypeVariable) type).getName();
                    int currChildParamPos = 0;
                    for (TypeVariable currTypeParam : currChild.getTypeParameters()) {
                        if (currTypeParam.getName().equals(typeName)) {
                            break;
                        }
                        currChildParamPos++;
                    }
                    currParentParameterPos = currChildParamPos;
                }
            }
            currParent = currChild;
        }

        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public static Package getFirstParentPackage(Package pkg) {
        if (!pkg.getName().contains(".")) {
            return null;
        }
        return getPotentialParentPackageNameList(pkg.getName())
                .stream()
                .map(ClassUtils::loadPackage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static Package loadPackage(String potentialParent) {
        ClassLoader effectiveClassLoader = getEffectiveClassLoader();
        var aPackage = effectiveClassLoader.getDefinedPackage(potentialParent);
        if (aPackage == null) {
            try {
                effectiveClassLoader.loadClass(potentialParent + ".package-info");
            } catch (ClassNotFoundException ignored) {
            } finally {
                aPackage = effectiveClassLoader.getDefinedPackage(potentialParent);
            }
        }
        return aPackage;
    }

    private static List<String> getPotentialParentPackageNameList(String name) {
        var parts = name.split("\\.");
        var potentialParents = new ArrayList<String>(parts.length);
        if (parts.length > 1) {
            var fullName = new StringBuilder(parts[0]);
            potentialParents.add(fullName.toString());
            for (int pos = 1; pos < (parts.length - 1); ++pos) {
                fullName.append('.').append(parts[pos]);
                potentialParents.add(0, fullName.toString());
            }
        }
        return potentialParents;
    }


    @SuppressWarnings("WeakerAccess")
    public static <A extends Annotation> Class getClassWithAnnotation(Class objectClass, Class<A> annotation) {
        if (objectClass.getAnnotation(annotation) != null) {
            return objectClass;
        }
        for (Class implementedInterface : objectClass.getInterfaces()) {
            if (implementedInterface.getAnnotation(annotation) != null) {
                return implementedInterface;
            }
        }
        if (objectClass.getSuperclass() != null) {
            return getClassWithAnnotation(objectClass, annotation);
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public static ClassLoader getEffectiveClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
