package com.joelj.jenkins.eztemplates.exclusion;

import java.util.List;
import java.util.Map;

import com.joelj.jenkins.eztemplates.jobtypes.JobsFacade;
import com.joelj.jenkins.eztemplates.utils.ProjectUtils;

import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;

public class TriggersExclusion extends HardCodedExclusion {

    public static final String ID = "build-triggers";
    private Map<TriggerDescriptor, Trigger> oldTriggers;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDescription() {
        return "Retain local Build Triggers";
    }

    @Override
    public String getDisabledText() {
        return null;
    }

    @Override
    public void preClone(Job implementationProject) {
        oldTriggers = JobsFacade.getTriggers( implementationProject );
    }

    @Override
    public void postClone(Job implementationProject) {
        fixBuildTriggers(implementationProject, oldTriggers);
    }

    private static void fixBuildTriggers(Job implementationProject, Map<TriggerDescriptor, Trigger> oldTriggers) {
        List<Trigger<?>> triggersToReplace = ProjectUtils.getTriggers(implementationProject);
        if (triggersToReplace == null) {
            throw new NullPointerException("triggersToReplace");
        }

        if (!triggersToReplace.isEmpty() || !oldTriggers.isEmpty()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (triggersToReplace) {
                triggersToReplace.clear();
                for (Trigger trigger : oldTriggers.values()) {
                    triggersToReplace.add(trigger);
                }
            }
        }
    }
}
