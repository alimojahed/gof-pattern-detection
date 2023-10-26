// 
// Decompiled by Procyon v0.5.36
// 

package ir.fum.oop.pattern.detection.inheritance;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.Vector;

public class NonInheritingClassVector implements Enumeratable
{
    private Vector<DefaultMutableTreeNode> nonInheritingClasses;
    
    public NonInheritingClassVector() {
        this.nonInheritingClasses = new Vector<DefaultMutableTreeNode>();
    }
    
    public DefaultMutableTreeNode getNode(final String nodeName) {
        final Enumeration<DefaultMutableTreeNode> e = this.nonInheritingClasses.elements();
        while (e.hasMoreElements()) {
            final DefaultMutableTreeNode node = e.nextElement();
            if (node.getUserObject().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }
    
    public void add(final DefaultMutableTreeNode node) {
        this.nonInheritingClasses.add(node);
    }
    
    public Enumeration getEnumeration() {
        return this.nonInheritingClasses.elements();
    }
    
    public int size() {
        return this.nonInheritingClasses.size();
    }
    
    public boolean equals(final InheritanceHierarchy ih) {
        return false;
    }
    
    @Override
    public String toString() {
        final DefaultMutableTreeNode node = this.nonInheritingClasses.get(0);
        if (node != null) {
            return (String)node.getUserObject();
        }
        return null;
    }
}
