package com.merchantwarehouse.zucchini;

import cucumber.api.Scenario;
import cucumber.runtime.CucumberException;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.MethodFormat;
import cucumber.runtime.Utils;
import cucumber.runtime.java.ObjectFactory;
import gherkin.TagExpression;
import gherkin.formatter.model.Tag;

import java.lang.reflect.Method;
import java.util.Collection;

import static java.util.Arrays.asList;

class JavaHookDefinition implements HookDefinition {

    private final Class clazz;
    private final Method method;
    private final long timeoutMillis;
    private final TagExpression tagExpression;
    private final int order;
    private final ObjectFactory objectFactory;

    public JavaHookDefinition(final Class clazz, final Method method, final String[] tagExpressions, final int order,
            final long timeoutMillis, final ObjectFactory objectFactory) {
        this.clazz = clazz;
        this.method = method;
        this.timeoutMillis = timeoutMillis;
        tagExpression = new TagExpression(asList(tagExpressions));
        this.order = order;
        this.objectFactory = objectFactory;
    }

    Method getMethod() {
        return method;
    }

    @Override
    public String getLocation(final boolean detail) {
        MethodFormat format = detail ? MethodFormat.FULL : MethodFormat.SHORT;
        return format.format(method);
    }

    @Override
    public void execute(final Scenario scenario) throws Throwable {
        Object[] args;
        switch (method.getParameterTypes().length) {
            case 0:
                args = new Object[0];
                break;
            case 1:
                if (!Scenario.class.equals(method.getParameterTypes()[0])) {
                    throw new CucumberException("When a hook declares an argument it must be of type " +
                            Scenario.class.getName() + ". " + method.toString());
                }
                args = new Object[]{scenario};
                break;
            default:
                throw new CucumberException("Hooks must declare 0 or 1 arguments. " + method.toString());
        }

        Utils.invoke(objectFactory.getInstance(clazz), method, timeoutMillis, args);
    }

    @Override
    public boolean matches(final Collection<Tag> tags) {
        return tagExpression.evaluate(tags);
    }

    @Override
    public int getOrder() {
        return order;
    }

}