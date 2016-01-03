package com.joelj.jenkins.eztemplates;

import com.google.common.base.Throwables;
import com.joelj.jenkins.eztemplates.utils.TemplateUtils;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;

/**
 * React to changes being made on template implementation projects
 */
@Extension
public class TemplateImplementationProjectListener extends ItemListener {

    @Override
    public void onUpdated(Item item) {
        TemplateImplementationProperty property = TemplateUtils.getTemplateImplementationProperty(item);
        if (property != null) {
            try {
                TemplateUtils.handleTemplateImplementationSaved((AbstractProject) item, property);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

}
