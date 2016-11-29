package com.joelj.jenkins.eztemplates;

import static com.joelj.jenkins.eztemplates.EzMatchers.hasImplementations;
import static com.joelj.jenkins.eztemplates.EzMatchers.hasNoImplementations;
import static com.joelj.jenkins.eztemplates.EzMatchers.hasNoTemplate;
import static com.joelj.jenkins.eztemplates.EzMatchers.hasTemplate;
import static com.joelj.jenkins.eztemplates.FieldMatcher.hasField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.joelj.jenkins.eztemplates.TemplateImplementationProperty.DescriptorImpl;
import com.joelj.jenkins.eztemplates.exclusion.Exclusions;

import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import hudson.model.listeners.ItemListener;
import hudson.triggers.TimerTrigger;
import hudson.util.ListBoxModel;


/**
 * Tests of the job and property behaviours.
 */
public class BehaviourTest {

    @Rule
    public final JenkinsRule jenkins = new JenkinsRule();

    private FreeStyleProject project(String name) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject(name);
        project.setDisplayName(WordUtils.capitalizeFully(name.replaceAll("[\\-]", " "))); // my-template > My Template
        return project;
    }

    private FreeStyleProject template(String name) throws Exception {
        FreeStyleProject template = project(name);
        template.addProperty(new TemplateProperty());
        return template;
    }

    private FreeStyleProject impl(String name, String template) throws Exception {
        FreeStyleProject impl = project(name);
        impl.addProperty(TemplateImplementationProperty.newImplementation(template));
        return impl;
    }

    @Test
    public void impl_finds_known_templates() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject template2 = template("beta-template");
        DescriptorImpl implDescriptor = new TemplateImplementationProperty.DescriptorImpl();
        implDescriptor.isApplicable(FreeStyleProject.class); // so doFillTemplateJobNameItems knows which types of jobs to look for

        // When:
        ListBoxModel knownTemplates = implDescriptor.doFillTemplateJobNameItems();
        // Then:
        assertThat(knownTemplates, contains(
                both(hasField("name", "No template selected")).and(hasField("value", null)),
                both(hasField("name", "Alpha Template")).and(hasField("value", "alpha-template")),
                both(hasField("name", "Beta Template")).and(hasField("value", "beta-template"))
        ));
    }

    @Test
    public void impl_has_default_exclusions() throws Exception {
        // Given:
        FreeStyleProject impl = impl("my-impl", null);
        // When:
        List<String> exclusions = impl.getProperty(TemplateImplementationProperty.class).getExclusions();
        // Then:
        assertThat(exclusions, is(equalTo(Exclusions.DEFAULT)));
    }

    @Test
    public void impl_knows_its_template() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        // When:
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        // Then:
        assertThat(impl, hasTemplate("alpha-template"));
    }

    @Test
    public void template_knows_its_children() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        // When:
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        FreeStyleProject impl2 = impl("alpha-2", "alpha-template");
        // Then:
        assertThat(template, hasImplementations(impl, impl2));
    }

    // Property listener

    @Test
    public void saving_impl_initiates_a_merges() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        impl.addTrigger(new TimerTrigger("* H * * *"));
        // When:
        ItemListener.fireOnUpdated(impl);
        // Then:
        assertThat(impl.getTriggers().isEmpty(), is(true));
    }

    @Test
    public void saving_template_initiates_a_merge() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        impl.addTrigger(new TimerTrigger("* H * * *"));
        // When:
        ItemListener.fireOnUpdated(template);
        // Then:
        assertThat(impl.getTriggers().isEmpty(), is(true));
    }

    @Test
    public void saving_something_else_works() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        FreeStyleProject other = project("beta");
        impl.addTrigger(new TimerTrigger("* H * * *"));
        // When:
        ItemListener.fireOnUpdated(other);
        // Then:
        assertThat(impl.getTriggers().isEmpty(), is(false));
    }

    @Test
    public void saving_impl_with_no_template_works() throws Exception {
        // Given:
        FreeStyleProject impl = impl("alpha-1", "null"); // FIXME this really should be tested via web submission
        impl.addTrigger(new TimerTrigger("* H * * *"));
        // When:
        ItemListener.fireOnUpdated(impl);
        // Then:
        assertThat(impl.getTriggers().size(), is(1));
    }

    @Test
    public void deleting_impl_removes_from_template() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        // When:
        impl.delete();
        // Then:
        assertThat(template, hasNoImplementations());
    }

    @Test
    public void deleting_template_frees_an_impl() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        // When:
        template.delete();
        // Then:
        assertThat(impl, hasNoTemplate());
    }

    @Test
    public void renaming_template_updates_impl() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        FreeStyleProject template2 = template("beta-template");
        FreeStyleProject impl2 = impl("beta-1", "beta-template");
        // When:
        template.renameTo("gamma-template");
        // Then:
        assertThat(impl, hasTemplate("gamma-template"));
        assertThat(impl2, hasTemplate("beta-template"));
    }

    @Test
    public void moving_template_updates_impl() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        FreeStyleProject impl = impl("alpha-1", "alpha-template");
        // When:
        template.renameTo("subfolder/alpha-template");
        // Then:
        assertThat(impl, hasTemplate("subfolder/alpha-template"));
    }

    @Test
    public void copying_template_creates_impl() throws Exception {
        // Given:
        FreeStyleProject template = template("alpha-template");
        // When:
        FreeStyleProject impl = (FreeStyleProject) jenkins.jenkins.copy((TopLevelItem) template, "alpha-1");
        // Then:
        assertThat(impl, hasTemplate("alpha-template"));
    }

}