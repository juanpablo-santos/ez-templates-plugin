package com.joelj.jenkins.eztemplates.jobtypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.kohsuke.stapler.Ancestor;

import com.google.common.base.Throwables;
import com.joelj.jenkins.eztemplates.exclusion.Exclusions;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.Label;
import hudson.scm.SCM;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;


/**
 * Facade to encapsulate the access to different types of Jenkins Jobs. Should be used when direct use of Job isn't possible and you have
 * to deal with concrete implementations of Jobs.
 *
 * @see pipeline's <a href="https://github.com/jenkinsci/pipeline-plugin/blob/master/DEVGUIDE.md#historical-background">DEVGUIDE.md#historical-background</a>
 * @author Juan Pablo Santos Rodríguez
 */
public class JobsFacade {

    private static boolean pipelinesPluginEnabled = Exclusions.checkPlugin("workflow-job") == null;

    /**
     * Verifies if the the plugin applies to the Jenkins job type.
     *
     * @param jobType Jenkins job type.
     * @return {@code true} if it is either an {@link AbstractProject} or a {@link WorkflowJob}.
     */
    public static boolean isPluginApplicableTo( Class< ? extends Job > jobType ) {
        if( pipelinesPluginEnabled ) {
            return AbstractProject.class.isAssignableFrom( jobType )
                || WorkflowJob.class.isAssignableFrom( jobType );
        }
        return AbstractProject.class.isAssignableFrom( jobType );
    }

    public static List< Job > getAllTemplatableJobs() {
        List< Job > jobs = new ArrayList<>();
        jobs.addAll( getApplicableJobOperationsFor( AbstractProject.class, null ).getAllJobs() );
        jobs.addAll( getApplicableJobOperationsFor( WorkflowJob.class, null ).getAllJobs() );
        return jobs;
    }

    public static List< Job > getAllJobs( Class< ? extends Job > jobType ) {
        List< Job > jobs = new ArrayList<>();
        jobs.addAll( getApplicableJobOperationsFor( jobType, null ).getAllJobs() );
        return jobs;
    }

    public static Ancestor findTemplatableAncestorFrom(Ancestor ancestor) {
        Ancestor job = ancestor;
        while (job != null && ( !(job.getObject() instanceof AbstractProject) ||
                                !(job.getObject() instanceof WorkflowJob) ) ) {
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
        return getApplicableJobOperationsFor( job.getClass(), job );
    }

    static JobProxy<? extends Job> getApplicableJobOperationsFor( Class<? extends Job> jobType, Job<?, ?> job ) {
        if( pipelinesPluginEnabled &&  WorkflowJob.class.isAssignableFrom( jobType ) ) {
            return new PipelineProxy( (WorkflowJob)job );
        } else if( AbstractProject.class.isAssignableFrom( jobType ) ) {
            return new AbstractProjectProxy( (AbstractProject<?,?>)job );
        }
        throw Throwables.propagate( new UnsupportedOperationException( "Need a pipeline or a job extending AbstractProject" ) );
    }

}
