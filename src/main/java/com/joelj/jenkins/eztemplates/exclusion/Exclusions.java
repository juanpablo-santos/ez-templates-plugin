package com.joelj.jenkins.eztemplates.exclusion;

import com.google.common.collect.ImmutableList;
import com.joelj.jenkins.eztemplates.TemplateImplementationProperty;

import java.util.Collection;

public class Exclusions {

    public static Collection<HardCodedExclusion> configuredExclusions(TemplateImplementationProperty property) {
        // TODO singletons?
        ImmutableList.Builder<HardCodedExclusion> builder = ImmutableList.builder();
        builder.add(new EzTemplatesExclusion());
        builder.add(new JobParametersExclusion());
        if (!property.getSyncBuildTriggers()) {
            builder.add(new TriggersExclusion());
        }
        if (!property.getSyncDisabled()) {
            builder.add(new DisabledExclusion());
        }
        if (!property.getSyncDescription()) {
            builder.add(new DescriptionExclusion());
        }
        if (!property.getSyncOwnership()) {
            builder.add(new JobPropertyExclusion("ownership", "Retain local ownership property", "com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerJobProperty"));
        }
        if (!property.getSyncSecurity()) {
            builder.add(new JobPropertyExclusion("matrix-security", "Retain local matrix-build security", "hudson.security.AuthorizationMatrixProperty"));
        }
        if (!property.getSyncScm()) {
            builder.add(new ScmExclusion());
        }
        if (!property.getSyncAssignedLabel()) {
            builder.add(new AssignedLabelExclusion());
        }
        if (!property.getSyncMatrixAxis()) {
            builder.add(new MatrixAxisExclusion());
        }

        return builder.build();
    }

}
