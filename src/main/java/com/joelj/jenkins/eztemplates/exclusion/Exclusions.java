package com.joelj.jenkins.eztemplates.exclusion;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.joelj.jenkins.eztemplates.TemplateImplementationProperty;
import jenkins.model.Jenkins;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Exclusions {

    public static final Map<String, Exclusion> ALL;
    public static final List<String> DEFAULT;

    static {
        ImmutableList.Builder<Exclusion> builder = ImmutableList.builder();
        builder.add(new EzTemplatesExclusion());
        builder.add(new JobParametersExclusion());
        builder.add(new TriggersExclusion());
        builder.add(new DisabledExclusion());
        builder.add(new DescriptionExclusion());
        builder.add(new JobPropertyExclusion("ownership", "Retain local ownership property", "com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerJobProperty"));
        builder.add(new JobPropertyExclusion("matrix-security", "Retain local matrix-build security", "hudson.security.AuthorizationMatrixProperty"));
        builder.add(new ScmExclusion());
        builder.add(new AssignedLabelExclusion());
        builder.add(new MatrixAxisExclusion());
        List<Exclusion> l = builder.build();
        ALL = Maps.uniqueIndex(l, new Function<Exclusion, String>() {
            @Override
            public String apply(Exclusion exclusion) {
                return exclusion.getId();
            }
        });
    }

    static {
        DEFAULT = ImmutableList.of(
                "ez-templates",
                "job-params",
                "disabled",
                "description"
        );
    }

    public static Collection<Exclusion> configuredExclusions(TemplateImplementationProperty property) {
        return Maps.filterKeys(ALL, Predicates.in(property.getExclusions())).values();
    }

    public static String checkPlugin(String id) {
        return Jenkins.getInstance().getPlugin(id)==null?null:String.format("Plugin %s is not installed", id);
    }

}
