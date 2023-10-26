// 
// Decompiled by Procyon v0.5.36
// 

package ir.fum.oop.pattern.detection.inheritance;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

public class InheritanceHierarchy implements Enumeratable
{
    private DefaultMutableTreeNode rootNode;
    
    public InheritanceHierarchy() {
        this.rootNode = null;
    }
    
    public DefaultMutableTreeNode getNode(final String nodeName) {
        if (this.rootNode != null) {
            final Enumeration<DefaultMutableTreeNode> e = this.rootNode.breadthFirstEnumeration();
            while (e.hasMoreElements()) {
                final DefaultMutableTreeNode node = e.nextElement();
                if (node.getUserObject().equals(nodeName)) {
                    return node;
                }
            }
        }
        return null;
    }
    
    public void addChildToParent(final String childNode, final String parentNode) {
        DefaultMutableTreeNode cNode = this.getNode(childNode);
        if (cNode == null) {
            cNode = new DefaultMutableTreeNode(childNode);
        }
        DefaultMutableTreeNode pNode = this.getNode(parentNode);
        if (pNode == null) {
            pNode = new DefaultMutableTreeNode(parentNode);
            this.rootNode = pNode;
        }
        pNode.add(cNode);
    }
    
    public void addChildRootNodeToParent(final DefaultMutableTreeNode childRootNode, final String parentNode) {
        DefaultMutableTreeNode pNode = this.getNode(parentNode);
        if (pNode == null) {
            pNode = new DefaultMutableTreeNode(parentNode);
            this.rootNode = pNode;
        }
        pNode.add(childRootNode);
    }
    
    public DefaultMutableTreeNode deepClone(final DefaultMutableTreeNode root) {
        final DefaultMutableTreeNode nroot = (DefaultMutableTreeNode)root.clone();
        final Enumeration<DefaultMutableTreeNode> children = root.children();
        while (children.hasMoreElements()) {
            final DefaultMutableTreeNode child = children.nextElement();
            final DefaultMutableTreeNode nchild = this.deepClone(child);
            nroot.add(nchild);
        }
        return nroot;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof InheritanceHierarchy) {
            final InheritanceHierarchy inheritanceHierarchy = (InheritanceHierarchy)o;
            this.rootNode.getUserObject().equals(inheritanceHierarchy.rootNode.getUserObject());
        }
        return false;
    }
    
    public DefaultMutableTreeNode getRootNode() {
        return this.rootNode;
    }
    
    public Enumeration getEnumeration() {
        return this.rootNode.breadthFirstEnumeration();
    }
    
    public int size() {
        final Enumeration e = this.rootNode.breadthFirstEnumeration();
        int counter = 0;
        while (e.hasMoreElements()) {
            e.nextElement();
            ++counter;
        }
        return counter;
    }
    
    @Override
    public String toString() {
        return (String)this.rootNode.getUserObject();
    }
}
