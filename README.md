# TagParser
**TagParser** is a generic parser that supports _&#60;tags&#62;_ recognition.
This is **NOT** an **HTML** or **XML** parser, but it does **support** both these languages.

To start using the project, import it with Maven or Gradle:
- **Maven**:
```xml
<repository>
    <id>fulminazzo-repo</id>
    <url>https://repo.fulminazzo.it/releases</url>
</repository>
```
```xml
<dependency>
    <groupId>it.fulminazzo</groupId>
    <artifactId>TagParser</artifactId>
    <version>LATEST</version>
</dependency>
```
- **Gradle**:
```groovy
repositories {
    maven { url = "https://repo.fulminazzo.it/releases" }
}

dependencies {
  implementation 'it.fulminazzo:TagParser:latest.release'
}
```

## Nodes
The main components of this program are nodes. There are two types:

### Node
The most basic node that supports only tag recognition and attributes.
It does not require a closing tag.

For example, 
```html
<test attr1=value1 attr2=value2 />
```
will be parsed as:
- **test** as the tag name;
- `attr1: value1` and `attr2: value2` as the key-value pair attributes.

### ContainerNode
A more advanced node which, along with the basic functions, allows for content (text) to be specified, as well as children nodes.
It requires a closing tag and will throw an exception if none are found.

For example,
```html
<test attr1="value1" attr2="value2">
    <child/>
    <child2/>
</test>
```
will be parsed as:
- **test** as the tag name;
- `attr1: value1` and `attr2: value2` as the key-value pair attributes;
- `child` and `child2` as the children.

## NodeBuilder
Those described above are the core functions of **TagParser**.
However, a user may want a more controlled and determined way of parsing tags, to prevent invalid ones from being specified or to check attributes and so on.
This is possible thanks to a [NodeBuilder](src/main/java/it/fulminazzo/tagparser/nodes/NodeBuilder.java),
a class responsible to creating **Nodes** from the given options.

These are all the available settings:
- `allowingGeneralTags`: if this option is enabled, every tag specified will be accepted.
  If it is disabled, only the one specified in `validTags` will be allowed;
- `allowingClosingTags`: if this option is enabled, it will be possible to specify tags in the format `<tag></tag>`.
  If it is disabled and a closed tag is specified, a [ClosingTagsNotAllowedException](src/main/java/it/fulminazzo/tagparser/nodes/exceptions/ClosingTagsNotAllowedException.java) will be thrown;
- `allowingNotClosedTags`: if this option is enabled, it will be possible to specify tags in the format `<tag/>`.
  If it is disabled and a closed tag is specified, a [NotClosedTagsNotAllowedException](src/main/java/it/fulminazzo/tagparser/nodes/exceptions/NotClosedTagsNotAllowedException.java) will be thrown;
- `checkingNext`: if enabled and there is a remainder from the input, it will be parsed from a clone of the current NodeBuilder;
- `validTags`: a list of all the allowed tag names.
If it is empty, the parser will skip this check (allowing every tag).
If it is not empty, it will see if the provided tag is valid and check if it should require closing tags or not.
If not valid, a [NotClosedTagsNotAllowedException](src/main/java/it/fulminazzo/tagparser/nodes/exceptions/NotValidTagException.java) will be thrown;
- `requiredAttributes`: a map of all the required attributes.
For every required attribute, check if it is specified.
If it is not, a [MissingRequiredAttributeException](src/main/java/it/fulminazzo/tagparser/nodes/exceptions/MissingRequiredAttributeException.java) will be thrown;
If it is, then uses the associated [AttributeValidator](src/main/java/it/fulminazzo/tagparser/nodes/validators/AttributeValidator.java) 
to verify its validity.
If it fails, a [NotValidAttributeException](src/main/java/it/fulminazzo/tagparser/nodes/exceptions/NotValidAttributeException.java) will be thrown.
Check [Validators](#validators) to see how they work and how to implement your own;
- `contentsRegex`: a regular expression applied to the content of the node (only for **ContainerNode**).
If it does not match, a [NotValidContentException](src/main/java/it/fulminazzo/tagparser/nodes/exceptions/NotValidContentException.java) will be thrown.

## Validators
An [AttributeValidator](src/main/java/it/fulminazzo/tagparser/nodes/validators/AttributeValidator.java)
is just a functional interface that acts like a Predicate.
It required a `validate(String, String)` method to be implemented that may throw a [NotValidAttributeException](src/main/java/it/fulminazzo/tagparser/nodes/exceptions/NotValidAttributeException.java)
upon failing the validation.

To better understand the functioning of this class, let's create our own validator.
It will verify that the passed string is a **negative number**.
```java
class NegativeValidator implements AttributeValidator {

  @Override
  public void validate(String attributeName, String attributeValue) throws NotValidAttributeException {
    try {
      // Try to convert the given string into a number.
      int num = Integer.parseInt(attributeValue);
      // If it is higher than 0, throw an exception.
      if (num > 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
      // Catch a NumberFormatException and convert it into a NotValidAttributeException
      throw new NotValidAttributeException(attributeName, "negative number", attributeValue);
    }
  }
}
```

## INodeObject
An [INodeObject](src/main/java/it/fulminazzo/tagparser/markup/INodeObject.java) is an interface that holds one node: the **root node**.
It is a **Container** that can hold various children, and it has the possibility to be converted into a **Map** or to be written in an output stream.

These are all the **INodeObject** available by default:

| Type                                                                       | Description                                     |
|----------------------------------------------------------------------------|-------------------------------------------------|
| [XMLObject](src/main/java/it/fulminazzo/tagparser/markup/XMLObject.java)   | An **INodeObject** capable of reading XML data  |
| [HTMLObject](src/main/java/it/fulminazzo/tagparser/markup/HTMLObject.java) | An **INodeObject** capable of reading HTML data |