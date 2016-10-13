package com.joelj.jenkins.eztemplates;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import com.google.common.collect.ImmutableList;
import com.joelj.jenkins.eztemplates.exclusion.AssignedLabelExclusion;
import com.joelj.jenkins.eztemplates.exclusion.DescriptionExclusion;
import com.joelj.jenkins.eztemplates.exclusion.DisabledExclusion;
import com.joelj.jenkins.eztemplates.exclusion.Exclusion;
import com.joelj.jenkins.eztemplates.exclusion.Exclusions;
import com.joelj.jenkins.eztemplates.exclusion.EzTemplatesExclusion;
import com.joelj.jenkins.eztemplates.exclusion.JobParametersExclusion;
import com.joelj.jenkins.eztemplates.exclusion.MatrixAxisExclusion;
import com.joelj.jenkins.eztemplates.exclusion.ScmExclusion;
import com.joelj.jenkins.eztemplates.exclusion.TriggersExclusion;
import com.joelj.jenkins.eztemplates.jobtypes.JobsFacade;
import com.joelj.jenkins.eztemplates.utils.ProjectUtils;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;

public class TemplateImplementationProperty extends JobProperty<Job<?, ?>> {
    private static final Logger LOG = Logger.getLogger("ez-templates");

    private String templateJobName;
    private final boolean syncMatrixAxis;
    private final boolean syncDescription;
    private final boolean syncBuildTriggers;
    private final boolean syncDisabled;
    private final boolean syncSecurity;
    private final boolean syncScm;
    private final boolean syncOwnership;
    private final boolean syncAssignedLabel;
    private List<String> exclusions; // Non-final until we drop support for upgrade from 1.1.x

    public static TemplateImplementationProperty newImplementation(String templateJobName) {
        return new TemplateImplementationProperty(
                templateJobName,
                Exclusions.DEFAULT,
                true, true, false, false, false, false, false, false);
    }

    @Deprecated
    @DataBoundConstructor
    public TemplateImplementationProperty(String templateJobName, List<String> exclusions, boolean syncDescription, boolean syncDisabled, boolean syncMatrixAxis, boolean syncBuildTriggers, boolean syncSecurity, boolean syncScm, boolean syncOwnership, boolean syncAssignedLabel) {
        this.exclusions = exclusions;
        this.templateJobName = templateJobName;
        // Support for rollback to <1.2.0
        this.syncDescription = !exclusions.contains(DescriptionExclusion.ID);
        this.syncDisabled = !exclusions.contains(DisabledExclusion.ID);
        this.syncMatrixAxis = !exclusions.contains(MatrixAxisExclusion.ID);
        this.syncBuildTriggers = !exclusions.contains(TriggersExclusion.ID);
        this.syncSecurity = !exclusions.contains(Exclusions.MATRIX_SECURITY_ID);
        this.syncScm = !exclusions.contains(ScmExclusion.ID);
        this.syncOwnership = !exclusions.contains(Exclusions.OWNERSHIP_ID);
        this.syncAssignedLabel = !exclusions.contains(AssignedLabelExclusion.ID);
    }

    @Exported
    public String getTemplateJobName() {
        return templateJobName;
    }

    public void setTemplateJobName(String templateJobName) {
        this.templateJobName = templateJobName;
    }

    public List<String> getExclusions() {
        if (exclusions==null) {
            LOG.info("Upgrading from earlier EZ Templates installation");
            ImmutableList.Builder<String> list = ImmutableList.builder();
            list.add(EzTemplatesExclusion.ID);
            list.add(JobParametersExclusion.ID);
            if (!syncDescription) list.add(DescriptionExclusion.ID);
            if (!syncDisabled) list.add(DisabledExclusion.ID);
            if (!syncMatrixAxis) list.add(MatrixAxisExclusion.ID);
            if (!syncBuildTriggers) list.add(TriggersExclusion.ID);
            if (!syncSecurity) list.add(Exclusions.MATRIX_SECURITY_ID);
            if (!syncScm) list.add(ScmExclusion.ID);
            if (!syncOwnership) list.add(Exclusions.OWNERSHIP_ID);
            if (!syncAssignedLabel) list.add(AssignedLabelExclusion.ID);
            exclusions = list.build();
        }
        return exclusions;
    }

    public Job findTemplate() {
        return ProjectUtils.findProject(getTemplateJobName());
    }

    @Deprecated
    public boolean isSyncMatrixAxis() {
        return syncMatrixAxis;
    }

    @Deprecated
    public boolean isSyncDescription() {
        return syncDescription;
    }

    @Deprecated
    public boolean isSyncBuildTriggers() {
        return syncBuildTriggers;
    }

    @Deprecated
    public boolean isSyncDisabled() {
        return syncDisabled;
    }

    @Deprecated
    public boolean isSyncSecurity() {
        return syncSecurity;
    }

    @Deprecated
    public boolean isSyncScm() {
        return syncScm;
    }

    @Deprecated
    public boolean isSyncOwnership() {
        return syncOwnership;
    }

    @Deprecated
    public boolean isSyncAssignedLabel() {
        return syncAssignedLabel;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        Class< ? extends Job > jobType;

        @Override
        public JobProperty<?> newInstance(StaplerRequest request, JSONObject formData) throws FormException {
            // TODO Replace with OptionalJobProperty 1.637
            if(formData.isNullObject()) formData=new JSONObject();
            return formData.optBoolean("useTemplate")?request.bindJSON(TemplateImplementationProperty.class, formData):null;
        }

        /**
         * Jenkins-convention to populate the drop-down box with discovered templates
         * @return populated data to fill the drop-down box with discovered templates
         */
        public ListBoxModel doFillTemplateJobNameItems() {
            ListBoxModel items = new ListBoxModel();
            // Add null as first option - dangerous to force an existing project onto a template in case
            // a noob destroys their config
            items.add(Messages.TemplateImplementationProperty_noTemplateSelected(), null);
            // Add all discovered templates

            for (Job project : ProjectUtils.findProjectsWithProperty(TemplateProperty.class, jobType)) {
                // fullName includes any folder structure
                items.add(project.getFullDisplayName(), project.getFullName());
            }
            return items;
        }

        @Override
        public String getDisplayName() {
            return Messages.TemplateImplementationProperty_displayName();
        }

        public FormValidation doCheckTemplateJobName(@QueryParameter final String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error(Messages.TemplateImplementationProperty_noTemplateSelected());
            }
            return FormValidation.ok();
        }

        public Collection<Exclusion> getExclusionDefinitions() {
            return Exclusions.ALL.values();
        }

        public List<String> getDefaultExclusions() {
            return Exclusions.DEFAULT;
        }

        @Override
        public boolean isApplicable( Class< ? extends Job > jobType ) {
            this.jobType = jobType;
            return JobsFacade.isPluginApplicableTo( jobType );
        }

    }
}

