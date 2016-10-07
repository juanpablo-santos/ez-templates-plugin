package com.joelj.jenkins.eztemplates;

import com.google.common.base.Throwables;
import com.joelj.jenkins.eztemplates.utils.TemplateUtils;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
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
                TemplateUtils.handleTemplateSaved((Job) item, property);
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
                TemplateUtils.handleTemplateDeleted((Job) item, property);
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
                TemplateUtils.handleTemplateRename((Job) item, property, oldFullName, newFullName);
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
                TemplateUtils.handleTemplateCopied((Job) item, (Job) src);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

}
