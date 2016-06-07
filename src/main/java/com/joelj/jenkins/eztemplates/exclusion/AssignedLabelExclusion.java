package com.joelj.jenkins.eztemplates.exclusion;

import com.google.common.base.Throwables;
import hudson.model.AbstractProject;
import hudson.model.Label;

import java.io.IOException;

public class AssignedLabelExclusion extends HardCodedExclusion {
    private Label label;

    @Override
    public String getId() {
        return "scm";
    }

    @Override
    public String getDescription() {
        return "Retain local scm block";
    }

    @Override
    public void preClone(AbstractProject implementationProject) {
        label = implementationProject.getAssignedLabel();
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        try {
            implementationProject.setAssignedLabel(label);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

}
