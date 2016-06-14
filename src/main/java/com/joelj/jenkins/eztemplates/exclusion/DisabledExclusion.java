package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.utils.EzReflectionUtils;
import hudson.model.AbstractProject;
import jenkins.model.Jenkins;

public class DisabledExclusion extends HardCodedExclusion {
    private boolean disabled;

    @Override
    public String getId() {
        return "disabled";
    }

    @Override
    public String getDescription() {
        return "Retain local disabled setting";
    }

    @Override
    public String getDisabledText() {
        return null;
    }

    @Override
    public void preClone(AbstractProject implementationProject) {
        disabled = implementationProject.isDisabled();
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        EzReflectionUtils.setFieldValue(AbstractProject.class, implementationProject, "disabled", disabled);
    }

}
