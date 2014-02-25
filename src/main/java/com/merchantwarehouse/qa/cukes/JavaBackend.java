package com.merchantwarehouse.qa.cukes;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.runtime.Backend;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.DuplicateStepDefinitionException;
import cucumber.runtime.Glue;
import cucumber.runtime.UnreportedStepExecutor;
import cucumber.runtime.Utils;
import cucumber.runtime.java.ObjectFactory;
import cucumber.runtime.snippets.FunctionNameSanitizer;
import cucumber.runtime.snippets.SnippetGenerator;
import gherkin.formatter.model.Step;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class JavaBackend implements Backend {

    private final SnippetGenerator snippetGenerator = new SnippetGenerator(new JavaSnippet());
    private final ObjectFactory objectFactory;

    private final MethodScanner methodScanner;
    private Glue glue;

    /**
     * The constructor called by reflection by default.
     *
     * @param classFinder
     * @param parentClazz
     */
    public JavaBackend(final ClassFinder classFinder, final Class parentClazz) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        methodScanner = new MethodScanner(classFinder);
        objectFactory = new DefaultJavaObjectFactory();

        objectFactory.addClass(parentClazz);
    }

    @Override
    public final void loadGlue(final Glue glue, final List<String> gluePaths) {
        this.glue = glue;
        methodScanner.scan(this, gluePaths);
    }

    @Override
    public final void setUnreportedStepExecutor(final UnreportedStepExecutor executor) {
        //Not used here yet
    }

    @Override
    public final void buildWorld() {
        objectFactory.start();
    }

    @Override
    public final void disposeWorld() {
        objectFactory.stop();
    }

    @Override
    public final String getSnippet(final Step step, final FunctionNameSanitizer fns) {
        return snippetGenerator.getSnippet(step, fns);
    }

    final void addStepDefinition(final Annotation annotation, final Class clazz, final Method method) {
        try {
            glue.addStepDefinition(new JavaStepDefinition(clazz, method, pattern(annotation), timeoutMillis(annotation),
                    objectFactory));
        } catch (DuplicateStepDefinitionException e) {
            throw e;
        } catch (Throwable e) {
            throw new CucumberException(e);
        }
    }

    private Pattern pattern(final Annotation annotation) throws Throwable {
        Method regexpMethod = annotation.getClass().getMethod("value");
        String regexpString = (String) Utils.invoke(annotation, regexpMethod, 0);
        return Pattern.compile(regexpString);
    }

    private long timeoutMillis(final Annotation annotation) throws Throwable {
        Method regexpMethod = annotation.getClass().getMethod("timeout");
        return (Long) Utils.invoke(annotation, regexpMethod, 0);
    }

    final void addHook(final Annotation annotation, final Class clazz, final Method method) {
        if (annotation.annotationType().equals(Before.class)) {
            String[] tagExpressions = ((Before) annotation).value();
            long timeout = ((Before) annotation).timeout();
            glue.addBeforeHook(new JavaHookDefinition(clazz, method, tagExpressions, ((Before) annotation).order(),
                    timeout, objectFactory));
        } else {
            String[] tagExpressions = ((After) annotation).value();
            long timeout = ((After) annotation).timeout();
            glue.addAfterHook(new JavaHookDefinition(clazz, method, tagExpressions, ((After) annotation).order(),
                    timeout, objectFactory));
        }
    }
}
