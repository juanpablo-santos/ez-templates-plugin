package com.joelj.jenkins.eztemplates.exclusion;

import hudson.model.AbstractProject;

public abstract class HardCodedExclusion implements Exclusion {
    public abstract void preClone(AbstractProject implementationProject);

    public abstract void postClone(AbstractProject implementationProject);

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), getId());
    }

    @Override
    public Exclusion clone() throws CloneNotSupportedException {
        return (Exclusion) super.clone();
    }

}
