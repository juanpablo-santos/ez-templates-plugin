package com.joelj.jenkins.eztemplates.exclusion;

import com.joelj.jenkins.eztemplates.utils.ReflectionUtils;
import hudson.matrix.AxisList;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;

public class MatrixAxisExclusion extends HardCodedExclusion {
    private AxisList axes;

    @Override
    public String getId() {
        return "matrix-axis";
    }

    @Override
    public String getDescription() {
        return "Retain local matrix axes";
    }

    @Override
    public void preClone(AbstractProject implementationProject) {
        if (implementationProject instanceof MatrixProject) {
            MatrixProject matrixProject = (MatrixProject) implementationProject;
            axes = matrixProject.getAxes();
        }
    }

    @Override
    public void postClone(AbstractProject implementationProject) {
        if (implementationProject instanceof MatrixProject) {
            fixAxisList((MatrixProject) implementationProject, axes);
        }
    }

    /**
     * Inlined from {@link MatrixProject#setAxes(hudson.matrix.AxisList)} except it doesn't call save.
     *
     * @param matrixProject The project to set the Axis on.
     * @param axisList      The Axis list to set.
     */
    private static void fixAxisList(MatrixProject matrixProject, AxisList axisList) {
        if (axisList == null) {
            return; //The "axes" field can never be null. So just to be extra careful.
        }
        ReflectionUtils.setFieldValue(MatrixProject.class, matrixProject, "axes", axisList);

        //noinspection unchecked
        ReflectionUtils.invokeMethod(MatrixProject.class, matrixProject, "rebuildConfigurations", ReflectionUtils.MethodParameter.get(MatrixBuild.MatrixBuildExecution.class, null));
    }

}
