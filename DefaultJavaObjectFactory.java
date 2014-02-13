package com.merchantwarehouse.qa.cukes;

import cucumber.runtime.CucumberException;
import cucumber.runtime.java.ObjectFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class DefaultJavaObjectFactory implements ObjectFactory {
    private final Map<Class<?>, Object> instances = new HashMap<>();

    @Override
    public void start() {
        // No-op
    }

    @Override
    public void stop() {
        instances.clear();
    }

    @Override
    public void addClass(final Class<?> clazz) {
        // no-op
    }

    @Override
    public <T> T getInstance(final Class<T> type) {
        T instance = type.cast(instances.get(type));
        if (instance == null) {
            instance = cacheNewInstance(type);
        }
        return instance;
    }

    private <T> T cacheNewInstance(final Class<T> type) {
        try {
            Constructor<T> constructor = type.getConstructor();
            T instance = constructor.newInstance();
            instances.put(type, instance);
            return instance;
        } catch (NoSuchMethodException e) {
            throw new CucumberException(String.format("%s doesn't have an empty constructor. If you need DI, put "
                    + "cucumber-picocontainer on the classpath", type), e);
        } catch (Exception e) {
            throw new CucumberException(String.format("Failed to instantiate %s", type), e);
        }
    }
}