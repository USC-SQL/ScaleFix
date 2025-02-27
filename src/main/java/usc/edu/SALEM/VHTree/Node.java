//package usc.edu.SALEM.VHTree;
//
//import gatech.xpert.dom.DomNode;
//import gatech.xpert.dom.generalNode;
//import gatech.xpert.dom.visitors.DomVisitor2;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Node<T extends generalNode> {
//    private Node<T> parent;
//    private T data;
//    private int level;
//    private List<Node<T>> children;
//    private boolean matched;
//
//
//    public Node(Node<T> parent, T data) {
//        this.parent = parent;
//        if (parent != null) {
//            if (this.parent.getChildren() == null) {
//                this.parent.setChildren(new ArrayList<Node<T>>());
//            }
//            this.parent.getChildren().add(this);
//            this.setChildren(null);
//        }
//        this.setData(data);
//    }
//
//
//    public Node<T> getParent() {
//        return parent;
//    }
//
//    public void setParent(Node<T> parent) {
//        this.parent = parent;
//    }
//
//    public void setData(T data) {
//        this.data = data;
//    }
//
//    public T getData() {
//        return data;
//    }
//
//    public void setChildren(List<Node<T>> children) {
//        this.children = children;
//    }
//
//    public List<Node<T>> getChildren() {
//        return children;
//    }
//
//    public List<Node<T>> getNodeSiblings() {
//        if (this.getParent() == null)
//            return null;
//
//        List<Node<T>> siblings = this.getParent().getChildren();
//        List<Node<T>> temp = new ArrayList<Node<T>>();
//
//        for (Node<T> node : siblings) {
//            if (!node.equals(this)) {
//                temp.add(node);
//            }
//        }
//        return temp;
//    }
//
//    public int getLevel() {
//        return level;
//    }
//
//    public void setLevel(int level) {
//        this.level = level;
//    }
//
//    public void accept(DomVisitor2 visitor) {
//        visitor.visit((Node<DomNode>) this);
//
//        for (Node<T> child : this.children) {
//            child.accept(visitor);
//        }
//    }
//
//    public boolean isMatched() {
//        return matched;
//    }
//
//    public void setMatched(boolean matched) {
//        this.matched = matched;
//    }
//
//    public int getCurrentNodeSiblingIndex() {
//        if (this.getParent() == null)
//            return -1;
//
//        List<Node<T>> allSiblings = this.getParent().getChildren();
//        int index = -1;
//        if (allSiblings != null && allSiblings.size() > 1) {
//            for (Node<T> node : allSiblings) {
//                ++index;
//                if (node.getData().equals(this.getData())) {
//                    return index;
//                }
//            }
//        }
//        return index;
//    }
//
//    public Node<T> getSiblingNodeAtIndex(int index) {
//        if (this.getParent() == null)
//            return null;
//
//        List<Node<T>> allSiblings = this.getParent().getChildren();
//        if (allSiblings != null) {
//            try {
//                Node<T> node = allSiblings.get(index);
//                return node;
//            } catch (ArrayIndexOutOfBoundsException e) {
//                return null;
//            }
//        }
//        return null;
//    }
//
//    public Node<T> copy() {
//        /*** not Fully correct : Ali: double check *///
//
////        T dataCopy = (T) data.copy();
////
////        Node<T> copy = new Node<T>(this.parent, dataCopy);
////        copy.setChildren(this.getChildren());
////
////
////        if (children != null) {
////            copy.children = new ArrayList<>(children);
////        }
//
//        T dataCopy = (T) data.copy();
//
//        Node<T> copy = new Node<T>(null, dataCopy);
//        copy.setChildren(null);
//
//
////        if (children != null) {
////            copy.children = new ArrayList<>(children);
////        }
//        return copy;
//    }
//}