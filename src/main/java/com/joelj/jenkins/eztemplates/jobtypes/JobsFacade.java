package com.joelj.jenkins.eztemplates.jobtypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.Ancestor;

import com.google.common.base.Throwables;
import com.joelj.jenkins.eztemplates.exclusion.Exclusions;
import com.joelj.jenkins.eztemplates.utils.EzReflectionUtils;

import hudson.model.Job;
import hudson.model.Label;
import hudson.scm.SCM;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;


/**
 * Facade to encapsulate the access to different types of Jenkins Jobs. Should be used when direct use of Job isn't possible and you have
 * to deal with concrete implementations of Jobs.
 *
 * @see <a href="https://github.com/jenkinsci/pipeline-plugin/blob/master/DEVGUIDE.md#historical-background">pipeline's DEVGUIDE.md#historical-background</a>
 * @author Juan Pablo Santos Rodríguez
 */
public class JobsFacade {

    private static final String ABSTRACT_PROJECT_CLASS = "hudson.model.AbstractProject";
    private static final String WORKFLOW_JOB_CLASS = "org.jenkinsci.plugins.workflow.job.WorkflowJob";

    private static boolean isPipelinesPluginEnabled() {
        return Exclusions.checkPlugin("workflow-job") == null;
    }

    /**
     * Verifies if the the plugin applies to the Jenkins job type.
     *
     * @param jobType Jenkins job type.
     * @return {@code true} if it is either an {@value #ABSTRACT_PROJECT_CLASS} or a {@value #WORKFLOW_JOB_CLASS}.
     */
    public static boolean isPluginApplicableTo( Class< ? extends Job > jobType ) {
        if( isPipelinesPluginEnabled() ) {
            return EzReflectionUtils.isAssignable( ABSTRACT_PROJECT_CLASS, jobType )
                || EzReflectionUtils.isAssignable( WORKFLOW_JOB_CLASS, jobType );
        }
        return EzReflectionUtils.isAssignable( ABSTRACT_PROJECT_CLASS, jobType );
    }

    public static List< Job > getAllTemplatableJobs() {
        List< Job > jobs = new ArrayList<>();
        jobs.addAll( getApplicableJobOperationsFor( ABSTRACT_PROJECT_CLASS, null ).getAllJobs() );
        jobs.addAll( getApplicableJobOperationsFor( WORKFLOW_JOB_CLASS, null ).getAllJobs() );
        return jobs;
    }

    public static List< Job > getAllJobs( Class< ? extends Job > jobType ) {
        List< Job > jobs = new ArrayList<>();
        jobs.addAll( getApplicableJobOperationsFor( jobType.getName(), null ).getAllJobs() );
        return jobs;
    }

    public static Ancestor findTemplatableAncestorFrom(Ancestor ancestor) {
        Ancestor job = ancestor;
        while (job != null && ( !(EzReflectionUtils.isInstanceOf( ABSTRACT_PROJECT_CLASS, job.getObject() )) ||
                                !(EzReflectionUtils.isInstanceOf( WORKFLOW_JOB_CLASS, job.getObject() )) ) ) {
            job = job.getPrev();
        }
        return job;
    }

    public static Label getAssignedLabel( Job job ) {
        return getApplicableJobOperationsFor( job ).getAssignedLabel();
    }

    public static void setAssignedLabel( Job job, Label label ) {
        getApplicableJobOperationsFor( job ).setAssignedLabel( label );
    }

    public static boolean isDisabled( Job job ) {
        return getApplicableJobOperationsFor( job ).isDisabled();
    }

    public static void disable( Job job, boolean disabled ) {
        getApplicableJobOperationsFor( job ).propagateDisabled( disabled );
    }

    public static SCM getScm( Job job ) {
        return getApplicableJobOperationsFor( job ).getScm();
    }

    public static void setScm( Job job, SCM scm ) {
        getApplicableJobOperationsFor( job ).setScm( scm );
    }

    public static Map<TriggerDescriptor, Trigger> getTriggers( Job job ) {
        return getApplicableJobOperationsFor( job ).getTriggers();
    }

    public static List<Trigger<?>> getTriggersToReplace( Job job ) {
        return getApplicableJobOperationsFor( job ).getTriggersToReplace();
    }

    static JobProxy<? extends Job> getApplicableJobOperationsFor( Job< ?, ? > job ) {
        return getApplicableJobOperationsFor( job.getClass().getName(), job );
    }

    static JobProxy<? extends Job> getApplicableJobOperationsFor( String jobType, Job<?, ?> job ) {
        if( isPipelinesPluginEnabled() && EzReflectionUtils.isAssignable( WORKFLOW_JOB_CLASS, jobType ) ) {
            return new PipelineProxy( job );
        } else if( EzReflectionUtils.isAssignable( ABSTRACT_PROJECT_CLASS, jobType ) ) {
            return new AbstractProjectProxy( job );
        }
        throw Throwables.propagate( new UnsupportedOperationException( "Need a pipeline or a job extending AbstractProject" ) );
    }

}
