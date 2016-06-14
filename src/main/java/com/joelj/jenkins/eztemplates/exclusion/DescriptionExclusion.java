package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.utils.EzReflectionUtils;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import jenkins.model.Jenkins;

public class DescriptionExclusion extends HardCodedExclusion {
    private String description;

    @Override
    public String getId() {
        return "description";
    }

    @Override
    public String getDescription() {
        return "Retain local description";
    }

    @Override
    public String getDisabledText() {
        return null;
    }

    @Override
    public void preClone(AbstractProject implementationProject) {
        description = implementationProject.getDescription();
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        EzReflectionUtils.setFieldValue(AbstractItem.class, implementationProject, "description", description);
    }

}
