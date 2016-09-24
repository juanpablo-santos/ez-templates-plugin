package com.joelj.jenkins.eztemplates;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Clumsy util to proxy classes with a reflective equals and toString method
 */
public class Equaliser {

    private final static Map<Class, Enhancer> ENHANCERS = new HashMap<Class, Enhancer>();

    @SuppressWarnings("unchecked")
    public static <T> T proxy(Class<T> c, Class[] types, Object[] values) {
        return (T) enhancerFor(c).create(types, values);
    }

    public static <T> T proxy(Class<T> c, Object... values) {
        Class[] types = new Class[values.length];
        int i = 0;
        for (Object value : values) {
            types[i++] = value.getClass(); // doesn't support null or primitives
        }
        return proxy(c, types, values);
    }

    private synchronized static Enhancer enhancerFor(Class<?> c) {
        if (!ENHANCERS.containsKey(c)) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(c);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    if ("toString".equals(method.getName())) {
                        return ReflectionToStringBuilder.toString(obj);
                    } else if ("equals".equals(method.getName())) {
                        return EqualsBuilder.reflectionEquals(obj, args[0]); // unsafe but we know there to be 1 arg!
                    } else {
                        return proxy.invokeSuper(obj, args);
                    }
                }
            });
            ENHANCERS.put(c, enhancer);
        }
        return ENHANCERS.get(c);
    }

}
