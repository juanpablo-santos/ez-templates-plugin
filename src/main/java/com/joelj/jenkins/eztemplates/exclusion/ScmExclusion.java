package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.jobtypes.JobsFacade;

import hudson.model.Job;
import hudson.scm.SCM;

public class ScmExclusion extends HardCodedExclusion {

    public static final String ID = "scm";
    private SCM scm;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDescription() {
        return "Retain local Source Code Management";
    }

    @Override
    public String getDisabledText() {
        return null;
    }

    @Override
    public void preClone(Job implementationProject) {
        scm = JobsFacade.getScm( implementationProject );
    }

    @Override
    public void postClone(Job implementationProject) {
        JobsFacade.setScm( implementationProject, scm );
    }

}
