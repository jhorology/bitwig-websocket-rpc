package com.github.jhorology.bitwig.extended.api;

/**
 * A POJO(JSON Serializable) class for DirectParameter 
 */
public class DirectParameter {
    private String id;
    private String name;
    private String displayValue;
    private double normalizedValue = Double.NaN;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public double getNormalizedValue() {
        return normalizedValue;
    }

    public void setNormalizedValue(double normalizedValue) {
        this.normalizedValue = normalizedValue;
    }
}
