package usc.edu.OwlEye.ElementsProperties;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import usc.edu.OwlEye.OwlConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AlignParent extends Property {

    //String texts
    public static String  alignParentBottom="alignParentBottom";
    public static String alignParentTop="alignParentTop";
    public static String alignParentLeft="alignParentLeft";
    public static String alignParentRight="alignParentRight";
    public static String alignParentStart="alignParentStart";
    public static String alignParentEnd="alignParentEnd";
    public static String alignWithParentIfMissing="alignWithParentIfMissing";
    public static String centerInParent="centerInParent";

    //Boolean values
    public Boolean  isAlignParentBottom;
    public Boolean  isAlignParentTop;
    public Boolean  isAlignParentEnd;
    public Boolean  isAlignParentStart;
    public Boolean  isAlignParentLeft;
    public Boolean  isAlignParentRight;
    public Boolean  isAlignWithParentIfMissing;
    public Boolean  isCenterInParent;


    public static Boolean  canBeNumerical=false;
    public static Boolean isBooleanValue=true;
    public static Boolean canBeNull=true;
    public static ArrayList<Boolean> possibleValues = new ArrayList<>(Arrays.asList(true,false));


    public AlignParent() {
    super("alignParent");
        this.isAlignParentBottom = false;
        this.isAlignParentTop = false;
        this.isAlignParentEnd = false;
        this.isAlignParentStart = false;
        this.isAlignParentLeft = false;
        this.isAlignParentRight = false;
    }

    public AlignParent(Boolean alignParentBottom, Boolean alignParentTop, Boolean alignParentEnd, Boolean alignParentStart, Boolean alignParentLeft, Boolean alignParentRight) {
        super("alignParent");
        this.isAlignParentBottom = alignParentBottom;
        this.isAlignParentTop = alignParentTop;
        this.isAlignParentEnd = alignParentEnd;
        this.isAlignParentStart = alignParentStart;
        this.isAlignParentLeft = alignParentLeft;
        this.isAlignParentRight = alignParentRight;
    }
    public AlignParent(Node<DomNode> element){
        super("alignParent");
        this.isAlignParentBottom = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(alignParentBottom));
        this.isAlignParentTop = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(alignParentTop));
        this.isAlignParentEnd = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(alignParentEnd));
        this.isAlignParentStart = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(alignParentStart));
        this.isAlignParentLeft = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(alignParentLeft));
        this.isAlignParentRight = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(alignParentRight));
        this.isAlignWithParentIfMissing = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(alignWithParentIfMissing));
        this.isCenterInParent = element.getData().getAttributes().containsKey(OwlConstants.TO_FuLL_ATTRIBUTES_MAPPING.get(centerInParent));
    }
    public Boolean getAlignParentBottom() {
        return isAlignParentBottom;
    }

    public void setAlignParentBottom(Boolean alignParentBottom) {
        isAlignParentBottom = alignParentBottom;
    }

    public Boolean getAlignParentTop() {
        return isAlignParentTop;
    }

    public void setAlignParentTop(Boolean alignParentTop) {
        isAlignParentTop = alignParentTop;
    }

    public Boolean getAlignParentEnd() {
        return isAlignParentEnd;
    }

    public void setAlignParentEnd(Boolean alignParentEnd) {
        isAlignParentEnd = alignParentEnd;
    }

    public Boolean getAlignParentStart() {
        return isAlignParentStart;
    }

    public void setAlignParentStart(Boolean alignParentStart) {
        isAlignParentStart = alignParentStart;
    }

    public Boolean getAlignParentLeft() {
        return isAlignParentLeft;
    }

    public void setAlignParentLeft(Boolean alignParentLeft) {
        isAlignParentLeft = alignParentLeft;
    }

    public Boolean getAlignParentRight() {
        return isAlignParentRight;
    }

    public void setAlignParentRight(Boolean alignParentRight) {
        isAlignParentRight = alignParentRight;
    }

    public Boolean getAlignWithParentIfMissing() {
        return isAlignWithParentIfMissing;
    }

    public void setAlignWithParentIfMissing(Boolean alignWithParentIfMissing) {
        isAlignWithParentIfMissing = alignWithParentIfMissing;
    }

    public Boolean getCenterInParent() {
        return isCenterInParent;
    }

    public void setCenterInParent(Boolean centerInParent) {
        isCenterInParent = centerInParent;
    }
}
