package com.joelj.jenkins.eztemplates;

import com.google.common.base.Throwables;
import com.joelj.jenkins.eztemplates.utils.TemplateUtils;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;

/**
 * React to changes being made on template projects
 */
@Extension
public class TemplateProjectListener extends ItemListener {

    @Override
    public void onUpdated(Item item) {
        TemplateProperty property = TemplateUtils.getTemplateProperty(item);
        if (property != null) {
            try {
                TemplateUtils.handleTemplateSaved((AbstractProject) item, property);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public void onDeleted(Item item) {
        TemplateProperty property = TemplateUtils.getTemplateProperty(item);
        if (property != null) {
            try {
                TemplateUtils.handleTemplateDeleted((AbstractProject) item, property);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public void onLocationChanged(Item item, String oldFullName, String newFullName) {
        TemplateProperty property = TemplateUtils.getTemplateProperty(item);
        if (property != null) {
            try {
                TemplateUtils.handleTemplateRename((AbstractProject) item, property, oldFullName, newFullName);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public void onCopied(Item src, Item item) {
        TemplateProperty property = TemplateUtils.getTemplateProperty(item);
        if (property != null) {
            try {
                TemplateUtils.handleTemplateCopied((AbstractProject) item, (AbstractProject) src);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

}
