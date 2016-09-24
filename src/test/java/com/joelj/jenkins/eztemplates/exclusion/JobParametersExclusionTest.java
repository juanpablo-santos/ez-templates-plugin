package com.joelj.jenkins.eztemplates.exclusion;

import com.google.common.collect.Lists;
import com.joelj.jenkins.eztemplates.Equaliser;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JobParametersExclusionTest {

    private List<ParameterDefinition> template;
    private List<ParameterDefinition> child;

    private static StringParameterDefinition string(String name, String defaultValue, String desc) {
        return Equaliser.proxy(StringParameterDefinition.class, name, defaultValue, desc);
    }

    private static ChoiceParameterDefinition choice(String name, String choices, String desc) {
        return Equaliser.proxy(ChoiceParameterDefinition.class, name, choices, desc);
    }

    private static List<ParameterDefinition> params(ParameterDefinition... defs) {
        return Lists.newArrayList(defs);
    }

    @Test
    public void merge_identical_params_retains_description_from_template() {
        // Given:
        template = params(string("alpha", "alpha-default", "YYYYYYYY"));
        child = params(string("alpha", "XXXXXXXX", "alpha-description"));
        // When:
        ParametersDefinitionProperty merged = JobParametersExclusion.merge(child, template);
        // Then:
        assertThat(merged.getParameterDefinitions(), is(equalTo(params(string("alpha", "XXXXXXXX", "YYYYYYYY")))));
    }

    @Test
    public void can_remove_one_param() {
        // Given:
        template = params(string("alpha", "alpha-default", "alpha-description"));
        child = params(string("alpha", "alpha-default", "alpha-description"),
                string("beta", "beta-default", "beta-description"));
        // When:
        ParametersDefinitionProperty merged = JobParametersExclusion.merge(child, template);
        // Then:
        assertThat(merged.getParameterDefinitions(), is(equalTo(params(string("alpha", "alpha-default", "alpha-description")))));
    }

    @Test
    public void can_remove_all_params() {
        // Given:
        template = params();
        child = params(string("alpha", "alpha-default", "alpha-description"),
                string("beta", "beta-default", "alpha-description"));
        // When:
        ParametersDefinitionProperty merged = JobParametersExclusion.merge(child, template);
        // Then:
        assertThat(merged, is(nullValue()));
    }

    @Test
    public void can_add_one_param() {
        // Given:
        template = params(string("alpha", "alpha-default", "alpha-description"),
                string("beta", "beta-default", "beta-description"));
        child = params(string("alpha", "alpha-default", "alpha-description"));
        // When:
        ParametersDefinitionProperty merged = JobParametersExclusion.merge(child, template);
        // Then:
        assertThat(merged.getParameterDefinitions(), is(equalTo(template)));
    }

    @Test
    public void can_change_type_of_param() {
        // Given:
        template = params(string("alpha", "alpha-default", "YYYYYYYY"));
        child = params(choice("alpha", "a,b,c", "alpha-description"));
        // When:
        ParametersDefinitionProperty merged = JobParametersExclusion.merge(child, template);
        // Then:
        assertThat(merged.getParameterDefinitions(), is(equalTo(params(string("alpha", "alpha-default", "YYYYYYYY")))));
    }

}