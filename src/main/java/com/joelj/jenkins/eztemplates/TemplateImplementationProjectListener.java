package com.joelj.jenkins.eztemplates;

import java.io.IOException;

import com.joelj.jenkins.eztemplates.utils.PropertyListener;
import com.joelj.jenkins.eztemplates.utils.TemplateUtils;

import hudson.Extension;
import hudson.model.Job;


/**
 * React to changes being made on template implementation projects
 */
@Extension
public class TemplateImplementationProjectListener extends PropertyListener<TemplateImplementationProperty> {

    @Override
    public void onUpdatedProperty(Job item, TemplateImplementationProperty property) throws IOException {
        TemplateUtils.handleTemplateImplementationSaved(item, property);
    }

}
