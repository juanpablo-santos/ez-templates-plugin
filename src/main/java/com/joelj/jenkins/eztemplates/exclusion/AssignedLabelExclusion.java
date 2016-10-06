package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.jobtypes.JobsFacade;

import hudson.model.Job;
import hudson.model.Label;

public class AssignedLabelExclusion extends HardCodedExclusion {

    public static final String ID = "assigned-label";
    private Label label;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDescription() {
        return "Retain local assigned label";
    }

    @Override
    public String getDisabledText() {
        return null;
    }

    @Override
    public void preClone(Job implementationProject) {
        label = JobsFacade.getAssignedLabel( implementationProject );
    }

    @Override
    public void postClone(Job implementationProject) {
        JobsFacade.setAssignedLabel( implementationProject, label );
    }

}
