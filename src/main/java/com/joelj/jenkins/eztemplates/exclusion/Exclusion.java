package com.joelj.jenkins.eztemplates.exclusion;

public interface Exclusion extends Cloneable {
    String getId();

    String getDescription();

    String getDisabledText();

    Exclusion clone() throws CloneNotSupportedException;
}
