package it.fulminazzo.tagparser.nodes;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Getter
@Setter
public class ContainerNode extends Node {
    protected Node child;
    protected String text;

    public ContainerNode(String tagName) {
        super(tagName);
    }

    public ContainerNode setAttribute(String key, String value) {
        return (ContainerNode) super.setAttribute(key, value);
    }

    public ContainerNode setAttributes(String... attributes) {
        return (ContainerNode) super.setAttributes(attributes);
    }

    public ContainerNode setAttributes(Map<String, String> attributes) {
        return (ContainerNode) super.setAttributes(attributes);
    }

    public ContainerNode addNext(String string) {
        return (ContainerNode) super.addNext(string);
    }

    public ContainerNode addNext(File file) {
        return (ContainerNode) super.addNext(file);
    }

    public ContainerNode addNext(InputStream stream) {
        return (ContainerNode) super.addNext(stream);
    }

    public ContainerNode addNext(Node next) {
        return (ContainerNode) super.addNext(next);
    }

    public ContainerNode removeNext(Node next) {
        return (ContainerNode) super.removeNext(next);
    }

    public ContainerNode removeNext(Predicate<Node> predicate) {
        return (ContainerNode) super.removeNext(predicate);
    }

    public ContainerNode setNext(String string) {
        return (ContainerNode) super.setNext(string);
    }

    public ContainerNode setNext(File file) {
        return (ContainerNode) super.setNext(file);
    }

    public ContainerNode setNext(InputStream stream) {
        return (ContainerNode) super.setNext(stream);
    }

    public ContainerNode setNext(Node next) {
        return (ContainerNode) super.setNext(next);
    }

    public ContainerNode addChild(String string) {
        return addChild(Node.newNode(string));
    }

    public ContainerNode addChild(File file) {
        return addChild(Node.newNode(file));
    }

    public ContainerNode addChild(InputStream stream) {
        return addChild(Node.newNode(stream));
    }

    public ContainerNode addChild(Node child) {
        if (this.child != null) this.child.addNext(child);
        else this.child = child;
        return this;
    }

    public ContainerNode removeChild(Node child) {
        return removeChild(n -> n.equals(child));
    }

    public ContainerNode removeChild(Predicate<Node> predicate) {
        if (this.child == null) return this;
        if (predicate.test(this.child)) {
            this.child.removeNext(predicate);
            this.child = this.child.next;
        }
        return this;
    }

    public ContainerNode setChild(String string) {
        return setChild(Node.newNode(string));
    }

    public ContainerNode setChild(File file) {
        return setChild(Node.newNode(file));
    }

    public ContainerNode setChild(InputStream stream) {
        return setChild(Node.newNode(stream));
    }

    public ContainerNode setChild(Node child) {
        this.child = child;
        return this;
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
