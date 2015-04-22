/*
 * Copyright (C) 2015 Elia Calligaris <calligaris.elia@spes.uniud.it> 
 * and Luca Geatti <geatti.luca@spes.uniud.it>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nmap.utill;

import java.util.ArrayList;
import org.xml.sax.Attributes;

/**
 * Encapsulates an XML tag
 *
 * @author Elia Calligaris
 */
public class Tag {

    private String name;
    private ArrayList<Attribute> attributes;
    /**
     * Contains a reference to tags that are one "level of indentation" below
     * this tag
     *
     * Example:
     * <1>
     * <2>
     * <3></3>
     * </2>
     * </1>
     * 1 contains a reference to 2 but not to 3.
     */
    private final ArrayList<Tag> children;
    //private ArrayList<Attribute> attributes;

    public Tag(String name, Attributes attr) {
        this.name = name;
        attributes = new ArrayList<>();
        for (int i = 0; i < attr.getLength(); i++) {
            attributes.add(new Attribute(attr.getLocalName(i), attr.getValue(i)));
        }
        children = new ArrayList<>();
    }

    public Tag(String name) {
        this(name, null);
    }

    public Tag() {
        this("null", null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChild(Tag t) {
        children.add(t);
    }

    public ArrayList<Tag> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("<").append(name);
        for (int i = 0; i < attributes.size(); i++) {
            out.append(" ").append(attributes.get(i).name).append("=\"").append(attributes.get(i).value).append("\"");
        }
        if (children.size() > 0) {
            out.append(" childrenList=\"");
            for (Tag t : children) {
                out.append(" ").append(t.getName());
            }
            out.append("\"");
        }
        out.append("/>");
        return out.toString();
    }

    public void addAttribute(String name, String value) {
        attributes.add(new Attribute(name, value));
    }

    public String[] getAttribute(int i) {
        return new String[]{attributes.get(i).name, attributes.get(i).value};
    }

    
    // TODO: add exceptions in these 2 methods
    public String[] getAttribute(String name) {
        for (Attribute a : attributes) {
            if (a.name.equals(name)) {
                return new String[]{a.name, a.value};
            }
        }
        return new String[]{"null", "null"};
    }

    public String getAttributeValue(String name) {
        for (Attribute a : attributes) {
            if (a.name.equals(name)) {
                return a.value;
            }
        }
        return "null";
    }

    /**
     * Convenience class that encapsulates an XML tag attribute
     */
    private class Attribute {

        private String name, value;

        private Attribute(String n, String v) {
            name = n;
            value = v;
        }
    }
}
