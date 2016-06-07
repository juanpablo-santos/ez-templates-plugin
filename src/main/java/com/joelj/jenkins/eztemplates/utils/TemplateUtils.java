package com.joelj.jenkins.eztemplates.utils;

import com.joelj.jenkins.eztemplates.*;
import com.joelj.jenkins.eztemplates.exclusion.Exclusions;
import com.joelj.jenkins.eztemplates.exclusion.HardCodedExclusion;
import com.joelj.jenkins.eztemplates.promotedbuilds.PromotedBuildsTemplateUtils;
import hudson.model.*;
import jenkins.model.Jenkins;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateUtils {
    private static final Logger LOG = Logger.getLogger("ez-templates");

    public static void handleTemplateSaved(AbstractProject templateProject, TemplateProperty property) throws IOException {
        LOG.info(String.format("Template [%s] was saved. Syncing implementations.", templateProject.getFullDisplayName()));
        for (AbstractProject impl : property.getImplementations()) {
            handleTemplateImplementationSaved(impl, getTemplateImplementationProperty(impl));
        }
    }

    public static void handleTemplateDeleted(AbstractProject templateProject, TemplateProperty property) throws IOException {
        LOG.info(String.format("Template [%s] was deleted.", templateProject.getFullDisplayName()));
        for (AbstractProject impl : property.getImplementations()) {
            LOG.info(String.format("Removing template from [%s].", impl.getFullDisplayName()));
            impl.removeProperty(TemplateImplementationProperty.class);
            ProjectUtils.silentSave(impl);
        }
    }

    public static void handleTemplateRename(AbstractProject templateProject, TemplateProperty property, String oldFullName, String newFullName) throws IOException {
        LOG.info(String.format("Template [%s] was renamed. Updating implementations.", templateProject.getFullDisplayName()));
        for (AbstractProject impl : TemplateProperty.getImplementations(oldFullName)) {
            LOG.info(String.format("Updating template in [%s].", impl.getFullDisplayName()));
            TemplateImplementationProperty implProperty = getTemplateImplementationProperty(impl);
            if (oldFullName.equals(implProperty.getTemplateJobName())) {
                implProperty.setTemplateJobName(newFullName);
                ProjectUtils.silentSave(impl);
            }
        }
    }

    public static void handleTemplateCopied(AbstractProject copy, AbstractProject original) throws IOException {
        LOG.info(String.format("Template [%s] was copied to [%s]. Forcing new project to be an implementation of the original.",original.getFullDisplayName(), copy.getFullDisplayName()));
        copy.removeProperty(TemplateProperty.class);
        copy.removeProperty(TemplateImplementationProperty.class);
        TemplateImplementationProperty implProperty = new TemplateImplementationProperty(
                original.getFullName(),
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                true
        );
        copy.addProperty(implProperty);
    }

    public static void handleTemplateImplementationSaved(AbstractProject implementationProject, TemplateImplementationProperty property) throws IOException {
    	
        if (property.getTemplateJobName().equals("null")) {
            LOG.warning(String.format("Implementation [%s] was saved. No template selected.", implementationProject.getFullDisplayName()));
            return;
        }
    	
        LOG.info(String.format("Implementation [%s] was saved. Syncing with [%s].", implementationProject.getFullDisplayName(), property.getTemplateJobName()));
        
        AbstractProject templateProject = property.findTemplate();        
        if (templateProject == null) {
        	// If the template can't be found, then it's probably a bug
            throw new IllegalStateException(String.format("Cannot find template [%s] used by job [%s]", property.getTemplateJobName(), implementationProject.getFullDisplayName()));
        }

        Collection<HardCodedExclusion> exclusions = Exclusions.configuredExclusions(property);

        // Capture values we want to keep
        for (HardCodedExclusion exclusion : exclusions) {
            try {
                exclusion.preClone(implementationProject);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, String.format("Templating failed analyse %s", exclusion), e);
            }
        }

        implementationProject = cloneTemplate(implementationProject, templateProject);

        // Restore values we want to keep - via reflection to prevent infinite save recursion
        for (HardCodedExclusion exclusion : exclusions) {
            try {
            exclusion.postClone(implementationProject);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, String.format("Templating failed apply %s", exclusion), e);
            }
        }

        ProjectUtils.silentSave(implementationProject);
    }

    private static AbstractProject cloneTemplate(AbstractProject implementationProject, AbstractProject templateProject) throws IOException {
        AbstractProject cloned = synchronizeConfigFiles(implementationProject, templateProject);
        if (Jenkins.getInstance().getPlugin("promoted-builds") != null) {
            PromotedBuildsTemplateUtils.addPromotions(cloned, templateProject);
        }
        return cloned;
    }

    private static AbstractProject synchronizeConfigFiles(AbstractProject implementationProject, AbstractProject templateProject) throws IOException {
        File templateConfigFile = templateProject.getConfigFile().getFile();
        BufferedReader reader = new BufferedReader(new FileReader(templateConfigFile));
        try {
            Source source = new StreamSource(reader);
            implementationProject = ProjectUtils.updateProjectWithXmlSource(implementationProject, source);
        } finally {
            reader.close();
        }
        return implementationProject;
    }

    /**
     * @param item A changed project
     * @return null if this is not a template implementation project
     */
    public static TemplateImplementationProperty getTemplateImplementationProperty(Item item) {
        if (item instanceof AbstractProject) {
            return (TemplateImplementationProperty) ((AbstractProject) item).getProperty(TemplateImplementationProperty.class);
        }
        return null;
    }

    /**
     * @param item A changed project
     * @return null if this is not a template project
     */
    public static TemplateProperty getTemplateProperty(Item item) {
        if (item instanceof AbstractProject) {
            return (TemplateProperty) ((AbstractProject) item).getProperty(TemplateProperty.class);
        }
        return null;
    }

}
