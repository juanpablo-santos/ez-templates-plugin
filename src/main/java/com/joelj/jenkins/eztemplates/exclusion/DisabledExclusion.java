package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.jobtypes.JobsFacade;

import hudson.model.Job;

public class DisabledExclusion extends HardCodedExclusion {

    public static final String ID = "disabled";
    private boolean disabled;

    @Override
    public String getId() {
        return ID;
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
    public void preClone(Job implementationProject) {
        disabled = JobsFacade.isDisabled( implementationProject );
    }

    @Override
    public void postClone(Job implementationProject) {
        JobsFacade.disable( implementationProject, disabled );
    }

}
