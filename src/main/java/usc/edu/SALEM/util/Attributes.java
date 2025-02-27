package usc.edu.SALEM.util;

public class Attributes {
    String attribute;
    String value;
    boolean isNew;


    public Attributes() {
    }

    public Attributes(String attribute, String value, boolean isNew) {
        this.attribute = attribute;
        this.value = value;
        this.isNew = isNew;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


}
