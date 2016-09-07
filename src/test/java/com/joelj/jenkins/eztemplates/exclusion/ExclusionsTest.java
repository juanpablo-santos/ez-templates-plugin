package com.joelj.jenkins.eztemplates.exclusion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.joelj.jenkins.eztemplates.TemplateImplementationProperty;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExclusionsTest {

    @Test
    public void providesCorrectExclusions() {
        // Given:
        TemplateImplementationProperty property = mock(TemplateImplementationProperty.class);
        when(property.getExclusions()).thenReturn(ImmutableList.of("ownership", "scm", "ez-templates"));
        // When:
        Collection<Exclusion> exclusions = Exclusions.configuredExclusions(property);
        // Then:
        assertThat(exclusions, containsInAnyOrder(
                hasProperty("id",equalTo("ownership")),
                hasProperty("id",equalTo("scm")),
                hasProperty("id",equalTo("ez-templates"))
        ));
    }

    @Test
    public void providesUniqueExclusions() {
        // Given:
        TemplateImplementationProperty property = mock(TemplateImplementationProperty.class);
        when(property.getExclusions()).thenReturn(ImmutableList.of("ownership"));
        Collection<Exclusion> exclusions = Exclusions.configuredExclusions(property);
        // When:
        Collection<Exclusion> exclusions2 = Exclusions.configuredExclusions(property);
        // Then:
        assertThat(exclusions, is(not(equalTo(exclusions2)))); // Assumes Exclusions have not implemented an equals() method!
    }

}