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
interface JobProxy {

    List< ? extends Job< ?, ? > > getAllJobs();

    Label getAssignedLabel();

    void setAssignedLabel( Label label );

    boolean isDisabled();

    void propagateDisabled( boolean disabled );

    SCM getScm();

    void setScm( SCM scm );

    Map< TriggerDescriptor, Trigger > getTriggers();

    List<Trigger<?>> getTriggersToReplace();

}
