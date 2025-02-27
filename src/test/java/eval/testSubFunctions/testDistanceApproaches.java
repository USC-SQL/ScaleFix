package eval.testSubFunctions;

import gatech.xpert.dom.DomNode;
import usc.edu.layoutgraph.edge.NeighborEdge;
import usc.edu.layoutgraph.node.LayoutNode;
import usc.edu.SALEM.Constants;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;

import java.io.IOException;

import static java.lang.Math.abs;

public class testDistanceApproaches {
//    private static String computeDegree(LayoutNode node1,LayoutNode node2) {
//        double yDiff = node1.getCenter()[1] - node2.getCenter()[1];
//        double xDiff = node1.getCenter()[0] - node2.getCenter()[0];
//        angleDegree = Math.toDegrees(Math.atan2(yDiff, xDiff));
//        angleDegree = (angleDegree + 360) % 360;
//    }

    public static void main(String[] args) {
//        Node node1;
//        Node node2;
        String device_label = "Nexus P6";
        if (device_label.equalsIgnoreCase("Nexus P6")) {
            Constants.PHONE_DENSITY = 3.5;
        }
        String user = "ali";

        String screen_res = "Physical size: 1440x2560";  //adb shell  wm size
        boolean IsThreadExecuted = false;
        String device_name = "emulator-5554";
        String RUNNING_MODE = "activity"; // or "app" // are we running at app level or going through activities seperately
        String DYNAMIC_LAYOUT_APPROACH = "sasha";
        String basePath = "/home/testing/AppSet/accessibility/TT_scripts";
        if (user.contains("paul")) {
            basePath = "/home/paul-sql/touchTarget/";
        }
        String activitiesToRun = "apks_folder/crawling_scripts/activities_to_scritpt_mapping.csv";
        try {
            SALEM.initialize(IsThreadExecuted, device_name, screen_res, basePath, RUNNING_MODE,
                    DYNAMIC_LAYOUT_APPROACH, activitiesToRun,false,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node<DomNode> node1;
        Node<DomNode> node2;
        XMLUtils.getInstance("/home/testing/AppSet/accessibility/TT_scripts/apks_folder/merged_layouts/androdns.android.leetdreams.ch.androdns/androdns.android.leetdreams.ch.androdns.DNSFormActivity.xml");
        Node<DomNode> root = XMLUtils.getRoot();
        node1 = XMLUtils.searchByID_T("androdns.android.leetdreams.ch.androdns:id/cbTCP", null);
        node2 = XMLUtils.searchByID_T("androdns.android.leetdreams.ch.androdns:id/button", null);
        //node2= XMLUtils.searchByID_T("androdns.android.leetdreams.ch.androdns:id/cbTCP",null);

        NeighborEdge neighborEdge = new NeighborEdge(new LayoutNode(node1),
                new LayoutNode(node2));
        boolean leftRight = neighborEdge.isStrictLeftRight(new LayoutNode(node1),
                new LayoutNode(node2));
        boolean topbottom = neighborEdge.isStrictTopBottom(new LayoutNode(node1),
                new LayoutNode(node2));
        double distance = Double.MAX_VALUE;
        double[] t;
        if (leftRight) {
            double e1x2 = node1.getData().x + node1.getData().width;
            double e2x1 = node2.getData().x;
            distance = abs(e1x2 - e1x2);
        } else if (topbottom) {
            double e1y2 = node1.getData().y + node1.getData().height;
            double e2y1 = node2.getData().y;
            distance = abs(e1y2 - e2y1);

        }
      //  t = Util.isElementCloseToAnotherWithDistance(node1.getData(), node2.getData(), Constants.TAP_TARGETS_RADIUS);


        System.out.println("Ali");
        //    node1= new LayoutNode()
//        computeDegree(node1,node2);
    }
}
