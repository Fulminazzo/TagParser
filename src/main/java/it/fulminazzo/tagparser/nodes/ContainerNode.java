package it.fulminazzo.tagparser.nodes;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@Setter
public class ContainerNode extends Node {
    protected Node child;
    protected String text;

    public ContainerNode(String tagName) {
        super(tagName);
    }

    public void addChild(String string) {
        addChild(Node.newNode(string));
    }

    public void addChild(File file) {
        addChild(Node.newNode(file));
    }

    public void addChild(InputStream stream) {
        addChild(Node.newNode(stream));
    }

    public void addChild(Node child) {
        if (this.child != null) this.child.addNext(child);
        else this.child = child;
    }

    public void removeChild(Node child) {
        removeChild(n -> n.equals(child));
    }

    public void removeChild(Predicate<Node> predicate) {
        if (this.child == null) return;
        if (predicate.test(this.child)) {
            this.child.removeNext(predicate);
            this.child = this.child.next;
        }
    }

    public void setChild(String string) {
        setChild(Node.newNode(string));
    }

    public void setChild(File file) {
        setChild(Node.newNode(file));
    }

    public void setChild(InputStream stream) {
        setChild(Node.newNode(stream));
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public int countChildren() {
        if (this.child != null) return 1 + this.child.countNextNodes();
        else return 0;
    }

    public List<Node> getChildren() {
        final List<Node> children = new LinkedList<>();
        Node c = child;
        while (c != null) {
            children.add(c);
            c = c.getNext();
        }
        return children;
    }

    @Override
    public String toHTML() {
        final StringBuilder builder = new StringBuilder(super.toHTML());
        builder.setLength(builder.length() - 2);
        builder.append(">");
        if (child != null) builder.append(child.toHTML());
        builder.append("</").append(tagName).append(">");
        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getSimpleName() + " {");

        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields())
                if (!Modifier.isStatic(field.getModifiers())) {
                    builder.append("\n    ");
                    if (field.getName().equals("child"))
                        builder.append("children: ").append(countChildren());
                    else builder.append(printField(field));
                }
            clazz = clazz.getSuperclass();
        }
        return builder.append("\n}").toString();
    }
}
