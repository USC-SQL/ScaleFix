//package uiautomator;
//
//import TTfix.util.Attributes;
//
//import java.util.ArrayList;
//
//public class Modification {
//    ArrayList<Attributes> modifications;
//    String id;
//
//
//    public Modification(ArrayList<Attributes> modifications, String id) {
//        this.modifications = modifications;
//        this.id = id;
//    }
//
//    public Modification() {
//
//    }
//
//    public ArrayList<Attributes> getModifications() {
//        return modifications;
//    }
//
//    public void setModifications(ArrayList<Attributes> modifications) {
//        this.modifications = modifications;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public Modification(String id) {
//        this.modifications = new ArrayList<>();
//        this.id = id;
//    }
//
//    public void addModification(String attribute, String value, boolean isNew) {
//
//        for (Attributes att : modifications
//        ) {
//            if (att.getAttribute().equalsIgnoreCase(attribute.trim()) && att.getValue().equalsIgnoreCase(value)) {
//                return;
//            }
//        }
//        Attributes attr = new Attributes();
//        attr.setAttribute(attribute);
//        attr.setValue(value);
//        attr.setNew(isNew);
//        modifications.add(attr);
//    }
//}
