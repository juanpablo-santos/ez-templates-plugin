package com.joelj.jenkins.eztemplates.jobtypes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import com.joelj.jenkins.eztemplates.utils.EzReflectionUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.Label;
import hudson.scm.SCM;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import jenkins.model.Jenkins;


@SuppressFBWarnings( justification="Possible NPEs are handled by callers" )
class AbstractProjectProxy implements JobProxy {

    private AbstractProject< ?, ? > job;

    public AbstractProjectProxy( AbstractProject< ?, ? > job ) {
        this.job = job;
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
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#getAllJobs()
     */
    @Override
    public List< ? extends Job > getAllJobs() {
        return (List< ? extends Job >)Jenkins.getInstance().getAllItems( AbstractProject.class );
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#setAssignedLabel(hudson.model.Label)
     */
    @Override
    public void setAssignedLabel( Label label ) {
        try {
            job.setAssignedLabel(label);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#isDisabled()
     */
    @Override
    public boolean isDisabled() {
        return job.isDisabled();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#propagateDisabled(boolean)
     */
    @Override
    public void propagateDisabled( boolean disabled ) {
        EzReflectionUtils.setFieldValue(AbstractProject.class, job, "disabled", disabled);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#getScm()
     */
    @Override
    public SCM getScm() {
        return job.getScm();
    }

    /**
     * {@inheritDoc}
     *
     * @see com.joelj.jenkins.eztemplates.jobtypes.JobProxy#setScm(hudson.scm.SCM)
     */
    @Override
    public void setScm( SCM scm ) {
        try {
            job.setScm(scm);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
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
        try {
            Field triggers = AbstractProject.class.getDeclaredField("triggers");
            triggers.setAccessible(true);
            Object result = triggers.get(job);
            //noinspection unchecked
            return (List<Trigger<?>>) result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
