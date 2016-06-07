package com.joelj.jenkins.eztemplates.exclusion;

import com.google.common.base.Throwables;
import com.joelj.jenkins.eztemplates.utils.EzReflectionUtils;
import hudson.model.AbstractProject;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class JobParametersExclusion extends HardCodedExclusion {

    private static final Logger LOG = Logger.getLogger("ez-templates");

    private List<ParameterDefinition> oldImplementationParameters;

    @Override
    public String getId() {
        return "job-params";
    }

    @Override
    public String getDescription() {
        return "Retain local job parameter values";
    }

    @Override
    public void preClone(AbstractProject implementationProject) {
        oldImplementationParameters = findParameters(implementationProject);
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        try {
            fixParameters(implementationProject, oldImplementationParameters);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    private static List<ParameterDefinition> findParameters(AbstractProject implementationProject) {
        List<ParameterDefinition> definitions = new LinkedList<ParameterDefinition>();
        @SuppressWarnings("unchecked")
        ParametersDefinitionProperty parametersDefinitionProperty = (ParametersDefinitionProperty) implementationProject.getProperty(ParametersDefinitionProperty.class);
        if (parametersDefinitionProperty != null) {
            for (String parameterName : parametersDefinitionProperty.getParameterDefinitionNames()) {
                definitions.add(parametersDefinitionProperty.getParameterDefinition(parameterName));
            }
        }
        return definitions;
    }

    private static void fixParameters(AbstractProject implementationProject, List<ParameterDefinition> oldImplementationParameters) throws IOException {
        List<ParameterDefinition> newImplementationParameters = findParameters(implementationProject);

        ParametersDefinitionProperty newParameterAction = findParametersToKeep(oldImplementationParameters, newImplementationParameters);
        @SuppressWarnings("unchecked") ParametersDefinitionProperty toRemove = (ParametersDefinitionProperty) implementationProject.getProperty(ParametersDefinitionProperty.class);
        if (toRemove != null) {
            //noinspection unchecked
            implementationProject.removeProperty(toRemove);
        }
        if (newParameterAction != null) {
            //noinspection unchecked
            implementationProject.addProperty(newParameterAction);
        }
    }

    private static ParametersDefinitionProperty findParametersToKeep(List<ParameterDefinition> oldImplementationParameters, List<ParameterDefinition> newImplementationParameters) {
        List<ParameterDefinition> result = new LinkedList<ParameterDefinition>();
        for (ParameterDefinition newImplementationParameter : newImplementationParameters) { //'new' parameters are the same as the template.
            boolean found = false;
            Iterator<ParameterDefinition> iterator = oldImplementationParameters.iterator();
            while (iterator.hasNext()) {
                ParameterDefinition oldImplementationParameter = iterator.next();
                if (newImplementationParameter.getName().equals(oldImplementationParameter.getName())) {
                    found = true;
                    iterator.remove(); //Make the next iteration a little faster.
                    // #17 Description on parameters should always be overridden by template
                    EzReflectionUtils.setFieldValue(ParameterDefinition.class, oldImplementationParameter, "description", newImplementationParameter.getDescription());
                    result.add(oldImplementationParameter);
                }
            }
            if (!found) {
                //Add new parameters not accounted for.
                result.add(newImplementationParameter);
                LOG.info(String.format("\t+++ new parameter [%s]", newImplementationParameter.getName()));
            }
        }

        if (oldImplementationParameters != null) {
            for (ParameterDefinition unused : oldImplementationParameters) {
                LOG.info(String.format("\t--- old parameter [%s]", unused.getName()));
            }
        }

        return result.isEmpty() ? null : new ParametersDefinitionProperty(result);
    }

}
