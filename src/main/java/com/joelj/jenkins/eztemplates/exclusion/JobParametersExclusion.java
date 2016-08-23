package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.utils.EzReflectionUtils;
import hudson.model.AbstractProject;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class JobParametersExclusion extends JobPropertyExclusion {

    private static final Logger LOG = Logger.getLogger("ez-templates");
    public static final String ID = "job-params";

    public JobParametersExclusion() {
        super(ID, "Retain local job parameter values", ParametersDefinitionProperty.class.getName());
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        super.cached = merge(
                parameters((ParametersDefinitionProperty) cached),
                parameters(implementationProject)
        );
        super.postClone(implementationProject);
    }

    private static List<ParameterDefinition> parameters(AbstractProject implementationProject) {
        @SuppressWarnings("unchecked")
        ParametersDefinitionProperty parametersDefinitionProperty = (ParametersDefinitionProperty) implementationProject.getProperty(ParametersDefinitionProperty.class);
        return parameters(parametersDefinitionProperty);
    }

    private static List<ParameterDefinition> parameters(ParametersDefinitionProperty parametersDefinitionProperty) {
        return (parametersDefinitionProperty == null)? Collections.<ParameterDefinition>emptyList():parametersDefinitionProperty.getParameterDefinitions();
    }

    private static ParametersDefinitionProperty merge(List<ParameterDefinition> oldParameters, List<ParameterDefinition> newParameters) {
        List<ParameterDefinition> result = new LinkedList<ParameterDefinition>();
        for (ParameterDefinition newParameter : newParameters) { //'new' parameters are the same as the template.
            boolean found = false;
            Iterator<ParameterDefinition> iterator = oldParameters.iterator();
            while (iterator.hasNext()) {
                ParameterDefinition oldParameter = iterator.next();
                if (newParameter.getName().equals(oldParameter.getName())) {
                    found = true;
                    iterator.remove(); //Make the next iteration a little faster.
                    // #17 Description on parameters should always be overridden by template
                    EzReflectionUtils.setFieldValue(ParameterDefinition.class, oldParameter, "description", newParameter.getDescription());
                    result.add(oldParameter);
                }
            }
            if (!found) {
                //Add new parameters not accounted for.
                result.add(newParameter);
                LOG.info(String.format("\t+++ new parameter [%s]", newParameter.getName()));
            }
        }

        // Anything left in oldParameters was not matched and will be removed
        for (ParameterDefinition unused : oldParameters) {
            LOG.info(String.format("\t--- old parameter [%s]", unused.getName()));
        }

        return result.isEmpty() ? null : new ParametersDefinitionProperty(result);
    }

}
