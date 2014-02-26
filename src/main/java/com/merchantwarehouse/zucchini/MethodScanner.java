package com.merchantwarehouse.zucchini;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Utils;
import cucumber.runtime.ClassFinder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static cucumber.runtime.io.MultiLoader.packageName;
import cucumber.runtime.java.StepDefAnnotation;

class MethodScanner {

    private final Collection<Class<? extends Annotation>> cucumberAnnotationClasses;

    private final ClassFinder classFinder;

    public MethodScanner(final ClassFinder classFinder) {
        this.classFinder = classFinder;
        cucumberAnnotationClasses = findCucumberAnnotationClasses();
    }

    /**
     * Registers step definitions and hooks.
     *
     * @param javaBackend the backend where stepdefs and hooks will be registered
     * @param gluePaths where to look
     */
    public void scan(final JavaBackend javaBackend, final List<String> gluePaths) {
        for (String gluePath : gluePaths) {
            for (Class<?> glueCodeClass : classFinder.getDescendants(Object.class, packageName(gluePath))) {
                while (glueCodeClass != null && glueCodeClass != Object.class && !Utils.isInstantiable(glueCodeClass)) {
                    // those can't be instantiated without container class present.
                    glueCodeClass = glueCodeClass.getSuperclass();
                }
                if (glueCodeClass != null) {
                    for (Method method : glueCodeClass.getMethods()) {
                        scan(javaBackend, method, glueCodeClass);
                    }
                }
            }
        }
    }

    /**
     * Registers step definitions and hooks.
     *
     * @param javaBackend the backend where stepdefs and hooks will be registered.
     * @param method a candidate for being a stepdef or hook.
     * @param glueCodeClass the class where the method is declared.
     */
    public void scan(final JavaBackend javaBackend, final Method method, final Class<?> glueCodeClass) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (!method.getDeclaringClass().isAssignableFrom(glueCodeClass)) {
                throw new CucumberException(String.format("%s isn't assignable from %s",
                        method.getDeclaringClass(), glueCodeClass));
            }
            if (isHookAnnotation(annotation)) {
                javaBackend.addHook(annotation, glueCodeClass, method);
            } else if (isStepdefAnnotation(annotation)) {
                javaBackend.addStepDefinition(annotation, glueCodeClass, method);
            }
        }
    }

    private Collection<Class<? extends Annotation>> findCucumberAnnotationClasses() {
        return classFinder.getDescendants(Annotation.class, "cucumber.api");
    }

    private boolean isHookAnnotation(final Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.equals(Before.class) || annotationClass.equals(After.class)
                || annotationClass.equals(org.junit.Before.class) || annotationClass.equals(org.junit.After.class);
    }

    private boolean isStepdefAnnotation(final Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.getAnnotation(StepDefAnnotation.class) != null;
    }
}
