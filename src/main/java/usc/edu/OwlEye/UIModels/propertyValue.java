package usc.edu.OwlEye.UIModels;

public class propertyValue <T> {
    private String property;
    private T value;
    private String valType; //String, int, double, boolean, or ID of other element

    public propertyValue(String property, T value,String valType) {
        this.property = property;
        this.value = value;
        this.valType = valType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getValType() {
        return valType;
    }

    public void setValType(String valType) {
        this.valType = valType;
    }

    public String toString(){
        return "(property: "+property+" value: "+value+" valType: "+valType+")"   ;
    }
}
