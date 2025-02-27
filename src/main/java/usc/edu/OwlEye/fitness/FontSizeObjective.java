package usc.edu.OwlEye.fitness;

import gatech.xpert.dom.DomNode;
import usc.edu.OwlEye.fitness.fitnessViolations.FontSizeViolation;
import usc.edu.SALEM.SALEM;
//import usc.edu.SALEM.VHTree.Node;
import usc.edu.SALEM.VHTree.XMLUtils;
import gatech.xpert.dom.Node;
import gatech.xpert.dom.Rectangle;
import java.util.ArrayList;

import static usc.edu.SALEM.VHTree.XMLUtils.getAllTextNodes;

public class FontSizeObjective {

    private double fontSizeScore;

    ArrayList<FontSizeViolation> violations;

    public FontSizeObjective() {

        this.fontSizeScore = Double.MIN_VALUE;
        violations = new ArrayList<>();
    }


    public double calculateFontSizeScore(Node<DomNode> newLayoutRoot) {

        /* compare the font size of each element in default UI and then in largeset UI
         * if the font size of an element in default UI is larger than the font size of an element in largest UI,
         * report violation
         */

        Node<DomNode> originalRoot = XMLUtils.getRoot();
        Node<DomNode> repairedRoot = newLayoutRoot;

        ArrayList<Node<DomNode>> originalTextNodes = getAllTextNodes(originalRoot);
        ArrayList<Node<DomNode>> repairedTextNodes = getAllTextNodes(repairedRoot);
        System.out.println("originalTextNodes.size()" + originalTextNodes.size());
        System.out.println("repairedTextNodes.size()" + repairedTextNodes.size());
        for (Node<DomNode> originalNode : originalTextNodes) {
            for (Node<DomNode> repairedNode : repairedTextNodes) {
                if (originalNode.getData().getTagName().equals(repairedNode.getData().getTagName())
                        && originalNode.getData().getId().equals(repairedNode.getData().getId())) {
                    String originalFontSizeText = originalNode.getData().getAttributes().get("android:scaledTextSize");
                    String repairedFontSizeText = repairedNode.getData().getAttributes().get("android:scaledTextSize");
                    double originalFontSize = 0;
                    double repairedFontSize = 0;
                    System.out.println("originalFontSizeText: " + originalFontSizeText);
                    System.out.println("repairedFontSizeText: " + repairedFontSizeText);
                    if (originalFontSizeText != null) {
                        originalFontSize = Double.parseDouble(originalFontSizeText);
                    }
                    if (repairedFontSizeText != null) {
                        repairedFontSize = Double.parseDouble(repairedFontSizeText);
                    }

                    if (originalFontSize > repairedFontSize) {
                        FontSizeViolation violation = new FontSizeViolation();
                        violations.add(violation);
                    }
                }
            }

        }

        return 0;
    }

}

