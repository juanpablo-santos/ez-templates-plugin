package com.joelj.jenkins.eztemplates.jobtypes;

import java.util.List;
import java.util.Map;

import hudson.model.Job;
import hudson.model.Label;
import hudson.scm.SCM;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;


/**
 * Contract for proxies encapsulating different types of jobs. It's meant to enforce a set of commons operations, but it's not intended to
 * be used outside of this package.
 *
 * @author Juan Pablo Santos Rodríguez
 */
interface JobProxy< T extends Job > {

    /**
     * get all jobs of a given type.
     *
     * @return all jobs of a given type.
     */
    List< T > getAllJobs();

    /**
     * get the assigned label on which the job should run.
     *
     * @return the assigned label on which the job should run.
     */
    Label getAssignedLabel();

    /**
     * Set the label on which the job should run.
     *
     * @param label the label to set
     */
    void setAssignedLabel( Label label );

    /**
     * check if this job is disabled or not.
     *
     * @return {@code true} if disabled, {@code false} otherwise.
     */
    boolean isDisabled();

    /**
     * disable / enable a job.
     *
     * @param disabled boolean to set the job's disabled state.
     */
    void propagateDisabled( boolean disabled );

    /**
     * get the job's SCM configuration info. Might be null as some types of jobs carry this information elsewhere, f.ex., pipelines define
     * the SCM(s) on the pipeline itself.
     *
     * @return the job's SCM configuration info.
     */
    SCM getScm();

    /**
     * set the job's SCM configuration info. Might do nothing as some types of jobs carry this information elsewhere, f.ex., pipelines
     * define the SCM(s) on the pipeline itself.
     *
     * @param scm the job's SCM configuration info to set.
     */
    void setScm( SCM scm );

    /**
     * get all the triggers associated to a given job.
     *
     * @return all the triggers associated to a given job.
     */
    Map< TriggerDescriptor, Trigger > getTriggers();

    /**
     * get the list of triggers of a given job which are going to be replaced with the triggers of the implemented template.
     *
     * @return list of triggers of a given job.
     */
    List<Trigger<?>> getTriggersToReplace();

}
