// 
// Decompiled by Procyon v0.5.36
// 

package ir.fum.oop.pattern.detection.inheritance;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

public interface Enumeratable
{
    Enumeration getEnumeration();
    
    int size();
    
    DefaultMutableTreeNode getNode(final String p0);
}
