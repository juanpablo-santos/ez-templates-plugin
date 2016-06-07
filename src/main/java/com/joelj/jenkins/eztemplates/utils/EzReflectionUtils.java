package com.joelj.jenkins.eztemplates.utils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.springframework.util.ReflectionUtils.*;

public class EzReflectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Class c, Object instance, String name) {
        Field f = findField(c,name);
        makeAccessible(f);
        return (T) getField(f, instance);
    }

    public static void setFieldValue(Class c, Object instance, String name, Object value) {
        Field f = findField(c,name);
        makeAccessible(f);
        setField(f, instance, value);
    }

}
