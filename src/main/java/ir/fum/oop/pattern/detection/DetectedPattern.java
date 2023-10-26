// 
// Decompiled by Procyon v0.5.36
// 

package ir.fum.oop.pattern.detection;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DetectedPattern
{
    private List<ClassEntry> classEntryList;
    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Pair<String, String> getNameAndRole(int index) {
        if (classEntryList.size() > index) {
            return Pair.of(classEntryList.get(index).getClassName(), classEntryList.get(index).getRole());
        }


        return Pair.of("", "");
    }

    public DetectedPattern() {
        this.classEntryList = new ArrayList<ClassEntry>();
    }
    
    public void addEntry(final ClassEntry e) {
        this.classEntryList.add(e);
    }
    
    public ListIterator<ClassEntry> getRoleIterator() {
        return this.classEntryList.listIterator();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DetectedPattern) {
            final DetectedPattern instance = (DetectedPattern)o;
            for (int i = 0; i < instance.classEntryList.size(); ++i) {
                final ClassEntry e = instance.classEntryList.get(i);
                if (!this.classEntryList.contains(e)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.classEntryList.size() - 1; ++i) {
            final ClassEntry e = this.classEntryList.get(i);
            sb.append(e.toString()).append(" | ");
        }
        sb.append(this.classEntryList.get(this.classEntryList.size() - 1).toString());
        return sb.toString();
    }
    
    public class ClassEntry
    {
        private String role;
        private String className;
        private int position;

        public double score;
        
        public ClassEntry(final String role, final String className, final int position, final double score) {
            this.role = role;
            this.className = className;
            this.position = position;
            this.score = score;
        }
        
        public String getRole() {
            return this.role;
        }
        
        public String getClassName() {
            return this.className;
        }
        
        public int getPosition() {
            return this.position;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ClassEntry) {
                final ClassEntry classEntry = (ClassEntry)o;
                return this.role.equals(classEntry.role) && this.className.equals(classEntry.className);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return this.role + ": " + this.className;
        }


        public double getScore() {
            return score;
        }
    }
}
