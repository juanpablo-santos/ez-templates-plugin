package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.utils.ReflectionUtils;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;

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
    public void preClone(AbstractProject implementationProject) {
        description = implementationProject.getDescription();
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        ReflectionUtils.setFieldValue(AbstractItem.class, implementationProject, "description", description);
    }

}
