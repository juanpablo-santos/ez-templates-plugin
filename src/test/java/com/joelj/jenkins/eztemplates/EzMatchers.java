package com.joelj.jenkins.eztemplates;

import hudson.model.AbstractProject;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Collection;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

abstract class EzMatchers {

    static class HasTemplate extends FeatureMatcher<AbstractProject<?, ?>, String> {

        HasTemplate(String templateName) {
            super(equalTo(templateName), "an impl with template", "template name");
        }

        @Override
        protected String featureValueOf(AbstractProject<?, ?> actual) {
            TemplateImplementationProperty prop = actual.getProperty(TemplateImplementationProperty.class);
            return prop == null ? null : prop.findTemplate().getFullName();
        }
    }

    public static HasTemplate hasTemplate(String templateName) {
        return new HasTemplate(templateName);
    }

    public static HasTemplate hasNoTemplate() {
        return new HasTemplate(null);
    }

    static class HasImplmentations extends FeatureMatcher<AbstractProject<?, ?>, Collection<AbstractProject>> {

        HasImplmentations(Matcher impls) {
            super(impls, "a template with impls", "implementations");
        }

        @Override
        protected Collection<AbstractProject> featureValueOf(AbstractProject<?, ?> actual) {
            return actual.getProperty(TemplateProperty.class).getImplementations();
        }
    }

    public static HasImplmentations hasImplementations(AbstractProject... impls) {
        return new HasImplmentations(contains(impls));
    }

    public static HasImplmentations hasNoImplementations() {
        return new HasImplmentations(Matchers.<AbstractProject>empty());
    }

}
