package com.joelj.jenkins.eztemplates.jobtypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.Job;
import hudson.model.Label;
import hudson.scm.SCM;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import jenkins.model.Jenkins;


@SuppressFBWarnings( justification="Possible NPEs are handled by callers" )
class PipelineProxy implements JobProxy<WorkflowJob> {

    private WorkflowJob job;

    public PipelineProxy( Job<?, ?> job ) {
        this.job = (WorkflowJob)job;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#getAllJobs()
     */
    @Override
    public List< WorkflowJob > getAllJobs() {
        return Jenkins.getInstance().getAllItems( WorkflowJob.class );
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#getAssignedLabel()
     */
    @Override
    public Label getAssignedLabel() {
        return job.getAssignedLabel();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#setAssignedLabel(hudson.model.Label)
     */
    @Override
    public void setAssignedLabel( Label label ) {
        // the label where the job is going to be run on is defined on the pipeline itself, we do nothing.
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#isDisabled()
     */
    @Override
    public boolean isDisabled() {
        // JENKINS-27299: workflows can't currently be disabled, return false.
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#propagateDisabled(boolean)
     */
    @Override
    public void propagateDisabled( boolean disabled ) {
        // JENKINS-27299: workflows can't currently be disabled, we do nothing.
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#getScm()
     */
    @Override
    public SCM getScm() {
        // SCM to poll is defined on the pipeline itself (in fact, you can have several SCMs to poll), so we do nothing.
        // See http://stackoverflow.com/a/31148178
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#setScm(hudson.scm.SCM)
     */
    @Override
    public void setScm( SCM scm ) {
        // SCM to poll is defined on the pipeline itself (in fact, you can have several SCMs to poll), so we do nothing.
        // See http://stackoverflow.com/a/31148178
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#getTriggers()
     */
    @Override
    public Map< TriggerDescriptor, Trigger > getTriggers() {
        Map< TriggerDescriptor, Trigger > triggers = new HashMap<>();
        triggers.putAll( job.getTriggers() );
        return triggers;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#getTriggersToReplace()
     */
    @Override
    public List< Trigger< ? > > getTriggersToReplace() {
        return job.getTriggersJobProperty().getTriggers();
    }

}
