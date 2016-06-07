package com.joelj.jenkins.eztemplates.exclusion;

import com.google.common.base.Throwables;
import hudson.model.AbstractProject;
import hudson.scm.SCM;

import java.io.IOException;

public class ScmExclusion extends HardCodedExclusion {
    private SCM scm;

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
        scm = implementationProject.getScm();
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        try {
            implementationProject.setScm(scm);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

}
