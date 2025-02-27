package eval.testSubFunctions;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;

public class parseSasha {

    public static void main(String[] args) {
        String subject = "com.alibaba.aliexpresshd";
        subject = "com.adzenze.FashionDesignFlatSketch";
        String activity = "com.alibaba.aliexpresshd.MainActivity";
        String file = "/home/testing/SQLGithub/AndroidStateDumper/xml/" + subject + "/result/15_Refined.xml";
        SALEM.setCurrentActivityName(activity);
//        String file="/home/testing/SQLGithub/AndroidStateDumper/xml/uk.co.yahoo.p1rpp.calendartrigger/result/1.xml";
//
//        mFix.setCurrentActivityName("uk.co.yahoo.p1rpp.calendartrigger.activites.SettingsActivity");
        XMLUtils.getInstance(file);
        Node<DomNode> root = XMLUtils.getRoot();
        System.out.println(XMLUtils.getRoot());

    }
}
