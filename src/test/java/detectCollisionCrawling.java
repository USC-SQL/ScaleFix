import gatech.xpert.dom.DomNode;
import gatech.xpert.dom.Node;
//import gatech.xpert.dom.Rectangle;
import org.xml.sax.SAXException;
import usc.edu.SALEM.SALEM;
import usc.edu.SALEM.VHTree.XMLUtils;
import usc.edu.SALEM.util.Util;
import usc.edu.layoutgraph.LayoutGraphBuilder;
import usc.edu.layoutgraph.edge.NeighborEdge;
import usc.edu.layoutgraph.node.LayoutNode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static usc.edu.SALEM.util.Util.getRandomColor;

public class detectCollisionCrawling {




    public static List<String> findFiles(Path path, String fileExtension)
            throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        List<String> result;

        try (Stream<Path> walk = Files.walk(path)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    // this is a path, not string,
                    // this only test if path end with a certain path
                    //.filter(p -> p.endsWith(fileExtension))
                    // convert path to string first
                    .map(p -> p.toString())
                    .filter(f -> f.endsWith(fileExtension))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static void drawClusters(String imagePath, Map<String, List<Rectangle>> clusterRectangles, Color color) throws IOException {
//        List<String> visitedColors = new ArrayList<String>();

        // clusterRectangles first rect: outermost cluster rect
        // other rectangles: cluster elements
        BufferedImage bi;
        try {
             bi = ImageIO.read(new File(imagePath));
        }catch (Exception e){
            System.out.println("Error reading image: " + imagePath);
            return;
        }

        Random rand = new Random();
        for (String cId : clusterRectangles.keySet()) {
            List<Rectangle> rects = clusterRectangles.get(cId);
            if (rects.size() == 0)
                continue;

            Graphics graphics = bi.getGraphics();
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Arial Black", Font.BOLD, 14));

            Rectangle rect = rects.get(0);
            int x = rect.x + 10 + rand.nextInt(11);
            int y = rect.y + 10 + rand.nextInt(11);
          //  graphics.drawString(cId, x, y);

            Graphics2D g2D = (Graphics2D) graphics;

            g2D.setColor(color);
            g2D.setStroke(new BasicStroke(3F));
            g2D.drawRect(rect.x, rect.y, rect.width, rect.height);

            if (rects.size() > 1) {
                // draw dashed rectangles around individual cluster elements
                float[] dash = {10.0f};
                g2D.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                int alpha = 50;
                Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                g2D.setPaint(c);
                for (int m = 1; m < rects.size(); m++) {
                    g2D.fill((Shape) rects.get(m));
                }
            }
        }
        ImageIO.write(bi, "png", new File(imagePath));
    }

    public static void main(String[] args) throws SAXException, IOException, InterruptedException {
        // read txt file into a list of strings
        List<String> listOfAPKS = Files.readAllLines(Paths.get("/home/ali/crawlOwlEye/maxim_crawling_output/detectCollision.txt"));
        String folderName = "unique_files";
        String basePath = "/home/ali/crawlOwlEye/maxim_crawling_output/non_empty_processed";
        for (String apk : listOfAPKS) {
            {
                String apkName = apk.trim();
                System.out.println("******************  Processing: " + apkName+ "  ****************************");
                if (apkName.contains("com.medicalutilitytools.bloodpressure.diary.bp.info.chart")||apkName.contains("com.michiganlabs.danielplan")||apkName.contains("yamayka")){
                    continue;}

                processAPK(apkName, basePath, folderName);
            }


        }
    }
    public static void processAPK(String apkName,String basePath,String folderName) throws IOException {

//        String subject ="/home/ali/crawlOwlEye/maxim_crawling_output/non_empty_processed/net.difer.appsreader.apk/net.difer.appsreader/unique_files";
        String folderLayout=findLayoutFolder(apkName,basePath);
        if (folderLayout == null){
            return;}
        String uniqueLayoutFolder = folderLayout+File.separator+folderName;
        SALEM.DYNAMIC_LAYOUT_APPROACH="sasha";
        // Iterate through all xml files in the folder
     //   List<String> xmlFiles = findFiles(new File(layoutFolder).toPath(), ".xml");
        List<String> xmlFiles = findFiles2(uniqueLayoutFolder, ".xml");
        LinkedHashMap <String, String>nodes=new LinkedHashMap();
//        LinkedHashMap<String, NeighborEdge> intersectionEdges=new LinkedHashMap();


        //check if folder exists
        String newFolder=uniqueLayoutFolder+File.separator+"intersection_edges";
        // if it does not exist, we create it later if it has intersection edges
        File folder = new File(newFolder);
        if (folder.exists()) {
            //remove old files
            return; // No need to process this apk again for now
//            File dir = new File(newFolder);
//            for(File file: dir.listFiles())
//                if (!file.isDirectory())
//                    file.delete();
        }
        for (String xmlFile : xmlFiles) {
            //System.out.println(xmlFile);
            LinkedHashMap<String, NeighborEdge> intersectionEdges=new LinkedHashMap();


            XMLUtils.getInstance(xmlFile);
            Node<DomNode> originalRoot = XMLUtils.getRoot();
            if(originalRoot==null)
                continue;
            if(originalRoot.getChildren()==null) {
                continue;
            }
//            System.out.println(originalRoot.getChildren().size());
            LayoutGraphBuilder lgb = new LayoutGraphBuilder(originalRoot,"crawler");
            ArrayList<NeighborEdge> edges = lgb.getBaselineLG().getEdges();
//            System.out.println(edges.size());
            int numOfWebViewsEdges =0;
            for (NeighborEdge edge : edges) {
                if (edge.isIntersect() ){//|| edge.isContains()|| edge.isCentered()) {
                   if(!isEdgeAlreadyExist(edge,intersectionEdges)){
                        if(isWebViewEdge(edge,intersectionEdges)){
                            numOfWebViewsEdges++;
                            continue;
                        }

                          intersectionEdges.put(edge.toString(),edge);
//                       System.out.println("Found  Intersection " + xmlFile);
//                       System.out.println(edge.getNode1().toString() + " " + edge.getNode2().toString());
                   }
//                   else {
//                       System.out.println("Edge already exist ");
//                   }
                }
            }




            if(intersectionEdges.size()>0){
                System.out.println("\t\t"+xmlFile.split(folderName)[1]+" | Number of intersection edges: " + intersectionEdges.size()+ " | Number of ignored webviews edges: "+numOfWebViewsEdges);

                Util.createFolder(newFolder);

               // new File(newFolder).mkdirs();
                String d=Paths.get(xmlFile).getFileName().toString();
                String newXpathPath=newFolder+File.separator+d;
                copyFile(xmlFile,newXpathPath);

                String png_file_name=xmlFile.replace(".xml",".png");
                String newPNGPath=newFolder+File.separator+d.replace(".xml",".png");
                copyFile(png_file_name,newPNGPath);






//                d=Paths.get(png_file_name).getFileName().toString();
//                copyFile(png_file_name,newFolder+File.separator+d);
                Map<String, List<Rectangle>> clusterRectangles = new HashMap<String, List<Rectangle>>();
                List<String> visitedColors = new ArrayList<String>();

                for (NeighborEdge edge : intersectionEdges.values()) {
                    {
                        Color color = getRandomColor(visitedColors);
                        LayoutNode node1 = edge.getNode1();
                        LayoutNode node2 = edge.getNode2();
                        Rectangle rect1 = new Rectangle(node1.getX1(), node1.getY1(), node1.getX2()-node1.getX1(), node1.getY2()-node1.getY1());
                        Rectangle rect2 = new Rectangle(node2.getX1(), node2.getY1(), node2.getX2()-node2.getX1(), node2.getY2()-node2.getY1());
                        clusterRectangles.put(edge.toString(), Arrays.asList(rect1, rect2));
                        drawClusters(newPNGPath, clusterRectangles,color);
                    }
            }


            }
        }

//        File[] files = new File(layoutFolder).listFiles();
//        for (File file : files) {
//
//                  XMLUtils.getInstance(file.getAbsolutePath());
//                  Node<DomNode> originalRoot = XMLUtils.getRoot();
//                  System.out.println(originalRoot.getChildren().size());
//
//            System.out.println(file.getName());
//        }

    }

    private static String findLayoutFolder(String apkName, String basePath) {
        //Iterate through all files in the folder
        File[] files = new File(basePath + File.separator +apkName ).listFiles();
        //check if file is a folder
        for (File file : files) {
            if (file.isDirectory()) {
                //check if folder name is the same as the apk name
                if (apkName.contains(file.getName())) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    private static boolean isWebViewEdge(NeighborEdge edge, LinkedHashMap<String, NeighborEdge> intersectionEdges) {
        if(edge.getNode1().toString().contains("WebView")||edge.getNode2().toString().contains("WebView")){
            return true;
        }
        return false;
    }

    private static List<String> findFiles2(String layoutFolder, String extension) {
                File[] files = new File(layoutFolder).listFiles();
                List<String> xmlFiles = new ArrayList<String>();
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().endsWith(extension)) {
                    xmlFiles.add(file.getAbsolutePath());

                }
            }
        }
        return xmlFiles;
    }

    public static void copyFile(String from, String to) throws IOException{
        Path src = Paths.get(from);
        Path dest = Paths.get(to);
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean isEdgeAlreadyExist(NeighborEdge edge, LinkedHashMap<String, NeighborEdge> intersectionEdges){
        for (String key : intersectionEdges.keySet()) {
            NeighborEdge currEdge = intersectionEdges.get(key);
            Boolean NodesAreEqualInOrder = edge.getNode1().toString().equals(currEdge.getNode1().toString()) && edge.getNode2().toString().equals(currEdge.getNode2().toString());
            Boolean NodesAreEqualInOpposite = edge.getNode1().toString().equals(currEdge.getNode2().toString()) && edge.getNode2().toString().equals(currEdge.getNode1().toString());

            if (NodesAreEqualInOrder || NodesAreEqualInOpposite) {
                return true;
            }

        }

        return false;


        }

}

