// 
// Decompiled by Procyon v0.5.36
// 

package ir.fum.oop.pattern.detection.inheritance;

import ir.fum.oop.model.ClassInfo;
import ir.fum.oop.pattern.description.SystemDescription;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

public class HierarchyDetection {
    private final List<Enumeratable> finalHierarchyList;

    public HierarchyDetection(final SystemDescription systemDescription) {
        final List<Enumeratable> superclassHierarchyList = this.getSuperclassHierarchyList(systemDescription);
        this.getNonInheritingClasses(systemDescription,
                this.finalHierarchyList = this.getInterfaceHierarchyList(systemDescription, superclassHierarchyList)
        );
    }

    private void getNonInheritingClasses(final SystemDescription systemDescription, final List<Enumeratable> hierarchyList) {
        final NonInheritingClassVector v = new NonInheritingClassVector();
        for (int i = 0; i < systemDescription.getNumberOfClasses(); ++i) {
            final ClassInfo classObject = systemDescription.getClassInfos().get(i);
            if (this.getHierarchy(hierarchyList, classObject.getClassName()) == null) {
                v.add(new DefaultMutableTreeNode(classObject.getClassName()));
            }
        }
        hierarchyList.add(v);
    }

    private List<Enumeratable> getSuperclassHierarchyList(final SystemDescription systemObject) {
        final List<Enumeratable> superclassHierarchyList = new ArrayList<Enumeratable>();
        for (int i = 0; i < systemObject.getNumberOfClasses(); ++i) {
            final ClassInfo classObject = systemObject.getClassInfos().get(i);
            if (systemObject.getClassIndexLocator().getOrDefault(classObject.getExtendedClass(), -1) != -1) {
                final InheritanceHierarchy childHierarchy = this.getHierarchy(superclassHierarchyList, classObject.getClassName());
                final InheritanceHierarchy parentHierarchy = this.getHierarchy(superclassHierarchyList, classObject.getExtendedClass());
                if (childHierarchy == null && parentHierarchy == null) {
                    final InheritanceHierarchy ih = new InheritanceHierarchy();
                    ih.addChildToParent(classObject.getClassName(), classObject.getExtendedClass());
                    superclassHierarchyList.add(ih);
                } else if (childHierarchy == null) {
                    parentHierarchy.addChildToParent(classObject.getClassName(), classObject.getExtendedClass());
                } else if (parentHierarchy == null) {
                    childHierarchy.addChildToParent(classObject.getClassName(), classObject.getExtendedClass());
                } else if (!childHierarchy.equals(parentHierarchy)) {
                    parentHierarchy.addChildRootNodeToParent(childHierarchy.getRootNode(), classObject.getExtendedClass());
                    superclassHierarchyList.remove(childHierarchy);
                }
            }
        }
        return superclassHierarchyList;
    }

    private List<Enumeratable> getInterfaceHierarchyList(final SystemDescription systemObject, final List<Enumeratable> superclassHierarchyList) {
        final List<Enumeratable> interfaceHierarchyList = new ArrayList<Enumeratable>(superclassHierarchyList);
        for (final Enumeratable enumeratable : superclassHierarchyList) {
            final InheritanceHierarchy ih = (InheritanceHierarchy) enumeratable;
            final Enumeration<DefaultMutableTreeNode> e = ih.getEnumeration();
            while (e.hasMoreElements()) {
                final DefaultMutableTreeNode node = e.nextElement();
                final ClassInfo co = systemObject.getClassInfoMap().get((String) node.getUserObject());
                final ListIterator<String> interfaceIt = co.getImplementedInterfaces().listIterator();
                while (interfaceIt.hasNext()) {
                    final String inter = interfaceIt.next();
                    if (systemObject.getClassIndexLocator().getOrDefault(inter, -1) != -1) {
                        final List<Enumeratable> parentHierarchies = this.getHierarchyList(interfaceHierarchyList, inter);
                        if (parentHierarchies.size() == 0) {
                            final InheritanceHierarchy tempIh = new InheritanceHierarchy();
                            if (node.getParent() == null) {
                                tempIh.addChildRootNodeToParent(node, inter);
                                interfaceHierarchyList.add(tempIh);
                                interfaceHierarchyList.remove(ih);
                            } else {
                                tempIh.addChildRootNodeToParent(ih.deepClone(node), inter);
                                interfaceHierarchyList.add(tempIh);
                            }
                        } else {
                            for (final Enumeratable parentEnumeratable : parentHierarchies) {
                                final InheritanceHierarchy parentHierarchy = (InheritanceHierarchy) parentEnumeratable;
                                if (node.getParent() == null) {
                                    parentHierarchy.addChildRootNodeToParent(node, inter);
                                    interfaceHierarchyList.remove(ih);
                                } else {
                                    parentHierarchy.addChildRootNodeToParent(ih.deepClone(node), inter);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < systemObject.getNumberOfClasses(); ++i) {
            final ClassInfo classObject = systemObject.getClassInfos().get(i);
            if (this.getHierarchy(interfaceHierarchyList, classObject.getClassName()) == null) {
                final ListIterator<String> interfaceIt2 = classObject.getImplementedInterfaces().listIterator();
                while (interfaceIt2.hasNext()) {
                    final String inter2 = interfaceIt2.next();
                    if (systemObject.getClassIndexLocator().getOrDefault(inter2, -1) != -1) {
                        final InheritanceHierarchy parentHierarchy2 = this.getHierarchy(interfaceHierarchyList, inter2);
                        if (parentHierarchy2 == null) {
                            final InheritanceHierarchy tempIh2 = new InheritanceHierarchy();
                            tempIh2.addChildToParent(classObject.getClassName(), inter2);
                            interfaceHierarchyList.add(tempIh2);
                        } else {
                            parentHierarchy2.addChildToParent(classObject.getClassName(), inter2);
                        }
                    }
                }
            }
        }
        return interfaceHierarchyList;
    }

    public List<Enumeratable> getHierarchyList() {
        return this.finalHierarchyList;
    }

    private InheritanceHierarchy getHierarchy(final List<Enumeratable> hierarchyList, final String nodeName) {
        for (final Enumeratable enumeratable : hierarchyList) {
            final InheritanceHierarchy ih = (InheritanceHierarchy) enumeratable;
            if (ih.getNode(nodeName) != null) {
                return ih;
            }
        }
        return null;
    }

    private List<Enumeratable> getHierarchyList(final List<Enumeratable> hierarchyList, final String nodeName) {
        final List<Enumeratable> outputList = new ArrayList<Enumeratable>();
        for (final Enumeratable enumeratable : hierarchyList) {
            final InheritanceHierarchy ih = (InheritanceHierarchy) enumeratable;
            if (ih.getNode(nodeName) != null) {
                outputList.add(ih);
            }
        }
        return outputList;
    }
}
