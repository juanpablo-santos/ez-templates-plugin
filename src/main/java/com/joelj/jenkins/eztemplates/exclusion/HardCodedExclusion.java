package com.joelj.jenkins.eztemplates.exclusion;

import hudson.model.Job;

public abstract class HardCodedExclusion implements Exclusion {
    public abstract void preClone(Job implementationProject);

    public abstract void postClone(Job implementationProject);

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), getId());
    }

    @Override
    public Exclusion clone() throws CloneNotSupportedException {
        return (Exclusion) super.clone();
    }

}
