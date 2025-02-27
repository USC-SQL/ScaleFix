package usc.edu.OwlEye.VH;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;

public class AndroidNode {

    //    List<String> attributeNames = Arrays.asList(
//            "resourceId", "contentDescription", "text", "class", "xpath");
    Node<DomNode> nodeInfo;
    //    Map<String, String> attributes = new HashMap<>();
    String resourceId;
    String contentDescription;
    String text;
    String xpath;
    String className;

    public AndroidNode(Node<DomNode> node) {

        this.nodeInfo = node;

        compueAttributes(node);
    }

//    public AndroidNode(Node<DomNode> node, String resourceId, String contentDescription, String text, String clsName) {
//        this.nodeInfo = node;
//    }

    private void compueAttributes(Node<DomNode> node) {

        //Cacluate Xpaths and other attributes
        this.resourceId = computeResourceID(node);
//        this.contentDescription = computeContentDescription(node);
        this.text = computeText(node);
        this.className = computeClassName(node);
        this.xpath = computeXpath(node);
    }

    private String computeResourceID(Node<DomNode> targetNode) {
        String id = "";
        if (targetNode != null && targetNode.getData().getId() != null && !targetNode.getData().getId().isEmpty()) {
            id = targetNode.getData().getId();
        }
        return id;
    }


    public Node<DomNode> getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(Node<DomNode> nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String computeXpath(Node<DomNode> targetNode) {
        Node<DomNode> it = targetNode;
        try {
            List<String> names = new ArrayList<>();
            String nodeId = it.getData().getId();
//            Log.i(TAG,"NODEID: "+it.toString());
            String x = "";
            if (nodeId != null) {
                x = "[@id=" + nodeId + "]";
            }
//            String contentDes = computeContentDescription(it);
//            if (contentDes != null && !contentDes.isEmpty()) {
//                x += "[@CD=" + contentDes + "]";
//            }

            String text = computeText(it);
            if (text != null && !text.isEmpty()) {
                x += "[@TX=" + text + "]";
            }
//            int collection = computeCollectionNo(it);
//            if(collection!=-100) {
//                x += "[@Col=" + collection + "]";
//            }
         //   boolean isParentSpecial = A11yUtil.hasRecylceViewORListViewParent(it);
            boolean isParentSpecial = false;

            String extraInfo="";
//            if(!isParentSpecial){
//                extraInfo=calculateNormalClass(it);
//            }else{
//                extraInfo=calculateSpecialClass(it);
//            }

            if(extraInfo!=null && !extraInfo.isEmpty()) {
                x += extraInfo;
            }

            String name = String.valueOf(it.getData().getTagName());
            if (x != null && !x.isEmpty()) {
                name = x + name;
            }
            names.add(0, name);

//            names.add(0, String.valueOf(it.getClassName()));
            while (it.getParent() != null) {

                int count = 0;
                int length = 0;
//                String itClsName = String.valueOf(it.getClassName());
//                for (int i = 0; i < it.getParent().getChildCount(); i++) {
//                    // TODO: possibility of ArrayIndexOutOfBoundsException
//
//                    Node<DomNode> child = it.getParent().getChild(i);
//                    if (child == null)
//                        continue;
//                    String childClsName = String.valueOf(child.getClassName());
//                    if (!child.isVisibleToUser())
//                        continue;
//                    if (itClsName.equals(childClsName))
//                        length++;
//                    if (child.equals(it)) {
//                        count = length;
//                    }
//                }

                //TO test yelp and facebook activity
                // we only add number from 2 and more
//                if (length > 1) {
//                if (length >= 2) {
//                    names.set(0, String.format("%s[%d]", names.get(0), count));
//                }
                it = it.getParent();
                String pNodeId = it.getData().getTagName();
//            Log.i(TAG,"NODEID: "+it.toString());
                String pX = "";
                if (pNodeId != null) {
                    pX = "[@id=" + pNodeId + "]";
                }
//                String PContentDes = computeContentDescription(it);
//                if (PContentDes != null && !PContentDes.isEmpty()) {
//                    pX += "[@CD=" + PContentDes + "]";
//                }
                String pText = computeText(it);
                if (pText != null && !pText.isEmpty()) {
                    x += "[@TX=" + pText + "]";
                }


                // boolean isPParentSpecial =A11yUtil.hasRecylceViewORListViewParent(it);
                boolean isPParentSpecial =false;

                 String pExtraInfo="";
//                if(!isPParentSpecial){
//                    pExtraInfo=calculateNormalClass(it);
//                }else{
//                    pExtraInfo=calculateSpecialClass(it);
//                }

                if(pExtraInfo!=null && !pExtraInfo.isEmpty()) {
                    pX += pExtraInfo;
                }
                String pName = String.valueOf(it.getData().getTagName());
                if (pX != null && !pX.isEmpty()) {
                    pName = pX + pName;
                }

                names.add(0, pName);
//                names.add(0, String.valueOf(it.getClassName()));
                //            xpath = String.valueOf(it.getClassName()) + "/" + xpath;
            }
            String xpath = null;
            xpath = "/" + String.join("/", names);

            return xpath;
        } catch (Exception exception) {
            exception.getStackTrace();
        }

        return null;
    }

//    private String calculateSpecialClass(Node<DomNode> it) {
//        String extraInfo="";
//        if (it.getParent() != null && it.getParent().getClassName() != null) {
//            // Special handling for recycling views
//            if (it.getParent().getClassName().toString().contains("RecyclerView")||it.getParent().getClassName().toString().contains("DrawerLayout")) {
//
//                if (text != null && !text.isEmpty()) {
//                    // if it has text then maybe that is enough to handle the difference we do not need to add column as it is not always accurate
//                    System.out.println("Child of RecyclerView that contains text so no need for DR or Col");
//                } else {
////                int drawingOrder = computeDrawingOrder(it); // drawing order in the parent
////                x += "[@DR=" + drawingOrder + "]";
//                    int collection = computeCollectionNo(it);
//                    if (collection != -100) {
//                        extraInfo = "[@Col=" + collection + "]";
//                    }
//                }
//            } else {
//                // Not recyclerView
//                int drawingOrder = computeDrawingOrder(it); // drawing order in the parent
//                extraInfo= "[@DR=" + drawingOrder + "]";
//
//            }
//        }
//        return extraInfo;
//    }

//    private String calculateNormalClass(Node<DomNode> it) {
//        String newClssName= "";
//        if(it.getParent() != null) {
//
//            int count = 0;
//            int length = 0;
//            String itClsName = String.valueOf(it.getClassName());
//            for (int i = 0; i < it.getParent().getChildCount(); i++) {
//                // TODO: possibility of ArrayIndexOutOfBoundsException
//
//                Node<DomNode> child = it.getParent().getChild(i);
//                if (child == null)
//                    continue;
//                String childClsName = String.valueOf(child.getClassName());
//                if (!child.isVisibleToUser())
//                    continue;
//                if (itClsName.equals(childClsName))
//                    length++;
//                if (child.equals(it)) {
//                    count = length;
//                }
//            }
//
//            if (length > 1) {
//                newClssName=String.format("[@CLS=%d]", count);
//            }
//        }
//
//        return newClssName;
//    }



//    public String computeXpath(Node<DomNode> targetNode) {
//        Node<DomNode> it = targetNode;
//        try {
//            List<String> names = new ArrayList<>();
//            String nodeId = it.getData().getId();
////            Log.i(TAG,"NODEID: "+it.toString());
//            String x = "";
//            if (nodeId != null) {
//                x = "[@id=" + nodeId + "]";
//            }
//            String contentDes = computeContentDescription(it);
//            if (contentDes != null && !contentDes.isEmpty()) {
//                x += "[@CD=" + contentDes + "]";
//            }
//
//            String text = computeText(it);
//            if (text != null && !text.isEmpty()) {
//                x += "[@TX=" + text + "]";
//            }
////            int collection = computeCollectionNo(it);
////            if(collection!=-100) {
////                x += "[@Col=" + collection + "]";
////            }
//            if (it.getParent() != null && it.getParent().getClassName() != null) {
//                // Special handling for recycling views
//                if (it.getParent().getClassName().toString().contains("RecyclerView")) {
//
//                    if (text != null && !text.isEmpty()) {
//                        // if it has text then maybe that is enough to handle the difference we do not need to add column as it is not always accurate
//                        System.out.println("Child of RecyclerView that contains text so no need for DR or Col");
//                    } else {
////                int drawingOrder = computeDrawingOrder(it); // drawing order in the parent
////                x += "[@DR=" + drawingOrder + "]";
//                        int collection = computeCollectionNo(it);
//                        if (collection != -100) {
//                            x += "[@Col=" + collection + "]";
//                        }
//                    }
//                } else {
//                    // Not recyclerView
//                    int drawingOrder = computeDrawingOrder(it); // drawing order in the parent
//                    x += "[@DR=" + drawingOrder + "]";
//
//                }
//            }
//
//            String name = String.valueOf(it.getClassName());
//            if (x != null && !x.isEmpty()) {
//                name = x + name;
//            }
//            names.add(0, name);
//
////            names.add(0, String.valueOf(it.getClassName()));
//            while (it.getParent() != null) {
//
//                int count = 0;
//                int length = 0;
//                String itClsName = String.valueOf(it.getClassName());
//                for (int i = 0; i < it.getParent().getChildCount(); i++) {
//                    // TODO: possibility of ArrayIndexOutOfBoundsException
//
//                    Node<DomNode> child = it.getParent().getChild(i);
//                    if (child == null)
//                        continue;
//                    String childClsName = String.valueOf(child.getClassName());
//                    if (!child.isVisibleToUser())
//                        continue;
//                    if (itClsName.equals(childClsName))
//                        length++;
//                    if (child.equals(it)) {
//                        count = length;
//                    }
//                }
//
//                //TO test yelp and facebook activity
//                // we only add number from 2 and more
////                if (length > 1) {
////                if (length >= 2) {
////                    names.set(0, String.format("%s[%d]", names.get(0), count));
////                }
//                it = it.getParent();
//                String pNodeId = it.getData().getId();
////            Log.i(TAG,"NODEID: "+it.toString());
//                String pX = "";
//                if (pNodeId != null) {
//                    pX = "[@id=" + pNodeId + "]";
//                }
//                String PContentDes = computeContentDescription(it);
//                if (PContentDes != null && !PContentDes.isEmpty()) {
//                    pX += "[@CD=" + PContentDes + "]";
//                }
//                String pText = computeText(it);
//                if (pText != null && !pText.isEmpty()) {
//                    x += "[@TX=" + pText + "]";
//                }
////                int pDrawingOrder = computeDrawingOrder(it); // drawing order in the parent
////                pX += "[@DR=" + pDrawingOrder + "]";
//                if (it.getParent() != null && it.getParent().getClassName() != null) {
//                    // Special handling for recycling views
//                    if (it.getParent().getClassName().toString().contains("RecyclerView")) {
//
//                        if (pText != null && !pText.isEmpty()) {
//                            // if it has text then maybe that is enough to handle the difference we do not need to add column as it is not always accurate
//                            System.out.println("Child of RecyclerView that contains text so no need for DR or Col");
//                        } else {
////                int drawingOrder = computeDrawingOrder(it); // drawing order in the parent
////                x += "[@DR=" + drawingOrder + "]";
//                            int pCollection = computeCollectionNo(it);
//                            if (pCollection != -100) {
//                                pX += "[@Col=" + pCollection + "]";
//                            }
//                        }
//                    } else {
//                        // Not recyclerView
//                        int pDrawingOrder = computeDrawingOrder(it); // drawing order in the parent
//                        pX += "[@DR=" + pDrawingOrder + "]";
//
//                    }
//                }
//                String pName = String.valueOf(it.getClassName());
//                if (pX != null && !pX.isEmpty()) {
//                    pName = pX + pName;
//                }
//
//                names.add(0, pName);
////                names.add(0, String.valueOf(it.getClassName()));
//                //            xpath = String.valueOf(it.getClassName()) + "/" + xpath;
//            }
//            String xpath = null;
//            xpath = "/" + String.join("/", names);
//
//            return xpath;
//        } catch (Exception exception) {
//            exception.getStackTrace();
//        }
//
//        return null;
//    }

//    private int computeCollectionNo(Node<DomNode> it) {
//        int index = -100;
//        if (it.getCollectionItemInfo() != null) {
//            index = it.getCollectionItemInfo().getColumnIndex();
//        }
//        return index;
//    }
//
//    private int computeDrawingOrder(Node<DomNode> targetNode) {
//        String drawingOrder = "";
//        if (targetNode != null) {
//            return targetNode.getDrawingOrder();
//        }
//
//        return 0;
//    }

    public String getXpath() {
        return xpath;
    }

    private String getAllTextIncludingChildren(Node<DomNode> node, String finalText) {
        if (node == null) {
            return "";
        }
        Queue<Node<DomNode>> q = new LinkedList<>();
        q.add(node);

        while (!q.isEmpty()) {

            Node<DomNode> currNode = q.remove();
            if (currNode.getData().getAttributes().get("text") != null) {
                String text = currNode.getData().getAttributes().get("text");
                if (text.length() > 70) {
                    text = text.substring(0, 70);
                }
                finalText += text;

            }
            for (int i = 0; i < currNode.getChildren().size(); i++) {
                if (currNode.getChildren().get(i) != null) {
                    q.add(currNode.getChildren().get(i));
                }
            }
        }
        return finalText;
    }

//    private String getAllContentDescriptionIncludingChildren(Node<DomNode> node, String finalContentDescription) {
//        if (node == null) {
//            return "";
//        }
//        Queue<Node<DomNode>> q = new LinkedList<>();
//        q.add(node);
//
//        while (!q.isEmpty()) {
//
//            Node<DomNode> currNode = q.remove();
//            if (currNode.getContentDescription() != null) {
//                String contentDescription = currNode.getContentDescription().toString();
//                if (contentDescription.length() > 30) {
//                }
//                finalContentDescription += contentDescription;
//
//            }
//            for (int i = 0; i < currNode.getChildCount(); i++) {
//                if (currNode.getChild(i) != null) {
//                    q.add(currNode.getChild(i));
//                }
//            }
//        }
//        return finalContentDescription;
//    }

//    public String computeResource(Node<DomNode> targetNode) {
//        String resource = "";
//        if (targetNode != null && !targetNode.getText().toString().isEmpty()) {
//            text = targetNode.getText().toString();
//        }
//
//        if (targetNode.getParent() != null && computeClassName(targetNode.getParent()).contains("widget.RecyclerView")) {
//            String allText = "";
//            allText = getAllTextIncludingChildren(targetNode, allText);
//            text = allText;
//        }
//        return text;
//    }

    public String computeText(Node<DomNode> targetNode) {
        String text = "";
        if (targetNode.getData().getAttributes().get("text") != null && !targetNode.getData().getAttributes().get("text").isEmpty()) {
            text = targetNode.getData().getAttributes().get("text") .toString();
        }

        
        return text;
    }

//    public String computeContentDescription(Node<DomNode> targetNode) {
//        String content = "";
//        if (targetNode.getContentDescription() != null && !targetNode.getContentDescription().toString().isEmpty()) {
//            content = targetNode.getContentDescription().toString();
//        }
//
//
//        if (targetNode.getParent() != null && computeClassName(targetNode.getParent()).contains("widget.RecyclerView")) {
//            String allContent = "";
//            allContent = getAllContentDescriptionIncludingChildren(targetNode, allContent);
//            content = allContent;
//        }
//        return content;
//    }

    private String computeClassName(Node<DomNode> targetNode) {
        String className = "";
        if (targetNode.getData().getTagName() != null && !targetNode.getData().getTagName().toString().isEmpty()) {
            className += targetNode.getData().getTagName();
        }
        return className;

    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AndroidNode)) {
            return false;
        }
//        if (!this.getNodeInfo().equals((((AndroidNode) o).getNodeInfo()))) {
//            return false;
//        }
        /*** here means they are the same so we checkXpath ***/
        if (!this.getXpath().equals(((AndroidNode) o).getXpath())) {
            return false;
        }
        if (!this.getContentDescription().equals(((AndroidNode) o).getContentDescription())) {
            return false;
        }
        if (!this.getText().equals(((AndroidNode) o).getText())) {
            return false;
        }
        return true;
    }

//        Node<DomNode> other = (Node<DomNode>) object;
//        if (mSourceNodeId != other.mSourceNodeId) {
//            return false;
//        }
//        if (mWindowId != other.mWindowId) {
//            return false;
//        }


    @Override
    public String toString() {
        String xpath = "";
        xpath = !getXpath().equals("") ? " xpath: " + getXpath() : "";
        String id = !getResourceId().isEmpty() ? " ID: " + getResourceId() + " " : "";
        String cd = !getContentDescription().isEmpty() ? " CD: " + getContentDescription() + " " : "";
        String tx = !getText().isEmpty() ? " TX: " + getText() + " " : "";
        String cl = !getClassName().isEmpty() ? " CLS: " + getClassName() + " " : "";
        return String.format("WidgetInfo %s%s%s%s%s",

                id,
                cd,
                tx,
                cl,
                xpath
        );
    }

}
