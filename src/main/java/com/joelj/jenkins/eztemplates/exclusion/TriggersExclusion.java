package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.utils.ProjectUtils;
import hudson.model.AbstractProject;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;

import java.util.List;
import java.util.Map;

public class TriggersExclusion extends HardCodedExclusion {
    private Map<TriggerDescriptor, Trigger> oldTriggers;

    @Override
    public String getId() {
        return "triggers";
    }

    @Override
    public String getDescription() {
        return "Retain local build triggers";
    }

    @Override
    public void preClone(AbstractProject implementationProject) {
        oldTriggers = implementationProject.getTriggers();
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        fixBuildTriggers(implementationProject, oldTriggers);
    }

    private static void fixBuildTriggers(AbstractProject implementationProject, Map<TriggerDescriptor, Trigger> oldTriggers) {
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
