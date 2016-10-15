package com.joelj.jenkins.eztemplates;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.ObjectUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.Field;

/**
 * Matcher for public fields, highly limited
 */
public class FieldMatcher<T> extends BaseMatcher<T> {

    private final String field;
    private final Object value;

    public FieldMatcher(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public boolean matches(Object item) {
        try {
            Field f = item.getClass().getField(field);
            return ObjectUtils.equals(f.get(item), value);
        } catch (NoSuchFieldException e) {
            throw Throwables.propagate(e);
        } catch (IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("field %s=", field)).appendValue(value);
    }

    public static <T> FieldMatcher<T> hasField(String field, Object value) {
        return new FieldMatcher<T>(field, value);
    }
}
