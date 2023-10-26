// 
// Decompiled by Procyon v0.5.36
// 

package ir.fum.oop.pattern.description;



import ir.fum.oop.pattern.detection.inheritance.Enumeratable;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClusterSet
{
    private TreeSet<Entry> entrySet;
    
    public ClusterSet() {
        this.entrySet = new TreeSet<Entry>();
    }
    
    public void addClusterEntry(final Entry e) {
        this.entrySet.add(e);
    }
    
    public SortedSet<Entry> getInvokingClusterSet() {
        for (final Entry e : this.entrySet) {
            if (e.getNumberOfMethodInvocations() == 0) {
                return this.entrySet.headSet(e);
            }
        }
        return this.entrySet;
    }
    
    public class Entry implements Comparable
    {
        private volatile int hashCode;
        private int numberOfMethodInvocations;
        private List<Enumeratable> hierarchyList;
        
        public Entry() {
            this.hashCode = 0;
            this.hierarchyList = new ArrayList<Enumeratable>();
        }
        
        public void addHierarchy(final Enumeratable ih) {
            this.hierarchyList.add(ih);
        }
        
        public void setNumberOfMethodInvocations(final int n) {
            this.numberOfMethodInvocations = n;
        }
        
        public int getNumberOfMethodInvocations() {
            return this.numberOfMethodInvocations;
        }
        
        public List<Enumeratable> getHierarchyList() {
            return this.hierarchyList;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Entry) {
                final Entry entry = (Entry)o;
                return this.numberOfMethodInvocations == entry.numberOfMethodInvocations && this.hierarchyList.equals(entry.hierarchyList);
            }
            return false;
        }
        
        public int compareTo(final Object o) {
            final Entry entry = (Entry)o;
            if (this.numberOfMethodInvocations != entry.numberOfMethodInvocations) {
                return -(this.numberOfMethodInvocations - entry.numberOfMethodInvocations);
            }
            for (int i = 0; i < this.hierarchyList.size(); ++i) {
                final Enumeratable thisIh = this.hierarchyList.get(i);
                final Enumeratable otherIh = entry.hierarchyList.get(i);
                if (!thisIh.equals(otherIh)) {
                    return thisIh.toString().compareTo(otherIh.toString());
                }
            }
            return 0;
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int result = 17;
                result = 37 * result + this.numberOfMethodInvocations;
                for (int i = 0; i < this.hierarchyList.size(); ++i) {
                    final Enumeratable thisIh = this.hierarchyList.get(i);
                    result = 37 * result + thisIh.toString().hashCode();
                }
                this.hashCode = result;
            }
            return this.hashCode;
        }
        
        @Override
        public String toString() {
            return this.numberOfMethodInvocations + " " + this.hierarchyList.toString();
        }
    }
}
