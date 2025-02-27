package usc.edu.OwlEye.BuildModels;

import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
import org.tinylog.Logger;
import usc.edu.OwlEye.ElementsProperties.Height;
import usc.edu.OwlEye.ElementsProperties.Width;
import usc.edu.OwlEye.UIModels.SRG;
import usc.edu.OwlEye.UIModels.SRGNode;
import usc.edu.OwlEye.UIModels.TRG;
import usc.edu.OwlEye.UIModels.TRGNode;
import usc.edu.OwlEye.VH.UI;
import usc.edu.OwlEye.VHTree.XMLUtils;
import usc.edu.OwlEye.util.Utils;
import usc.edu.SALEM.util.Util;

import java.util.*;

public class ConstructTRG {

    protected static ConstructTRG instance = null;


    private ConstructTRG(String subjectID) {
    }

    public static ConstructTRG getInstance() {

        if (instance == null)
            instance = new ConstructTRG(null);  // do whatever else you need to here

        return instance;
    }

    public TRG buildGraph(UI ui) {
        XMLUtils xmlTree = ui.getXMLTree();
        // Initialize the SRG
        TRG trg = new TRG(ui.getUITitle());
        // (1) create height relationship
        createTextRelations(trg,xmlTree, Height.propertyName);


        return trg;

    }

    private void createTextRelations(TRG trg, XMLUtils xmlTree, String propertyName) {

        Node<DomNode> root = xmlTree.getRoot();

        Queue<Node<DomNode>> q = new LinkedList<Node<DomNode>>();
        if (root.getChildren() != null) {
            for (Node<DomNode> child : root.getChildren()) {
                q.add(child);
            }
        }

        //Start the BFS traversal

        while (!q.isEmpty()) {


            Node<DomNode> node = q.remove();
            DomNode e = node.getData();
            String id=e.getId();
            Logger.trace("Element that contains text atts for TRG graph: "+id);
            if (id==null || id.isEmpty()) {
                 continue;
                }

            Map<String, String> attributes = e.getAttributes();
            if (attributes == null) {
                continue;
            }
            HashMap<String, String> textAttributes = new HashMap<String, String>();
            for (String key: TRG.TRG_ATTRIBUTES.keySet()) {
                String val=TRG.TRG_ATTRIBUTES.get(key);
                if (attributes.containsKey(val)) {
                    String nodeVal=attributes.get(val);
                    if (nodeVal == null || nodeVal.isEmpty()) {
                        continue;
                    }
                    System.out.println("Attribute: "+key+" Value: "+nodeVal);


                    textAttributes.put(key, nodeVal);
                }
            }

            if (!textAttributes.isEmpty()) {
                TRGNode trgNode = new TRGNode(node, textAttributes);
                trg.getDependentNodesMap().put(id, trgNode);
            }



//            for (String key : attributes.keySet()
//                 ) {
//                if (TRG.TRG_ATTRIBUTES.containsValue(key)) {
//                    String value = attributes.get(key);
//                    if (value == null || value.isEmpty()) {
//                        continue;
//                    }
//
//                   System.out.println("Attribute: "+key+" Value: "+value);
//
//
//                }

         //   }


            if(node.getChildren() != null) {
                for (Node<DomNode> child : node.getChildren()) {
                    q.add(child);
                }
            }
        }


    }




            }









