package com.joelj.jenkins.eztemplates.promotedbuilds;

import hudson.Util;
import hudson.model.AbstractProject;
import hudson.plugins.promoted_builds.JobPropertyImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * Utility class to handle the promoted builds plugin.
 */
public class PromotedBuildsTemplateUtils {

    private static Logger LOG = Logger.getLogger("ez-templates");

    /**
     * Adds all the promotions from the template project into the implementation one. All existing promotions from the
     * implementation project are lost.
     *
     * @param implementationProject
     * @param templateProject
     * @throws IOException
     */
    public static void addPromotions(AbstractProject implementationProject, AbstractProject templateProject) throws IOException {

        JobPropertyImpl promotions = (JobPropertyImpl) implementationProject.getProperty(JobPropertyImpl.class);
        if (promotions != null) {
            LOG.info(String.format("Merging [%s].", promotions.getFullDisplayName()));

            // remove existing promotions on implementationProject, if any
            implementationProject.removeProperty(JobPropertyImpl.class);
            Util.deleteRecursive(new File(implementationProject.getRootDir(), "promotions"));
            promotions.getItems().clear();

            // obtain templateProject promotions. Each promotion is stored under a different folder under $JOB/promotions
            File templatePromotions = new File(templateProject.getRootDir(), "promotions");
            String[] list = templatePromotions.list();
            if (list != null) {
                for (String promotionDir : list) {
                    File templatePromotionProcess = new File(templatePromotions, promotionDir);
                    // JENKINS-38695 Don't clone .svn or .git directories, only ones that look like real promotions
                    if (templatePromotionProcess.isDirectory() && new File(templatePromotionProcess, "config.xml").exists()) {
                        // for each promotion, create a process from its configuration
                        promotions.createProcessFromXml(promotionDir, new FileInputStream(new File(templatePromotionProcess, "config.xml")));
                    }
                }
            }

            // update implementationProject with the resulting promotions
            implementationProject.addProperty(promotions);
        }
    }

}
