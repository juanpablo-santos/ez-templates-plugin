package com.joelj.jenkins.eztemplates;

import com.joelj.jenkins.eztemplates.utils.PropertyListener;
import com.joelj.jenkins.eztemplates.utils.TemplateUtils;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;

import java.io.IOException;

/**
 * React to changes being made on template implementation projects
 */
@Extension
public class TemplateImplementationProjectListener extends PropertyListener<TemplateImplementationProperty> {

    @Override
    public void onUpdatedProperty(Job item, TemplateImplementationProperty property) throws IOException {
        TemplateUtils.handleTemplateImplementationSaved((AbstractProject) item, property);
    }

}
