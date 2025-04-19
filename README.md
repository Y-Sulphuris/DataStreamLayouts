# DataStreamLayouts
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A Java library that simplifies serialization and deserialization of objects using `DataOutput` and `DataInput` streams.
It provides a type-safe, efficient way to handle binary data formats with minimal boilerplate code.
(The main purpose of this is to simplify creation of packet classes in networking)

## Overview

DataStreamLayouts automatically handles the serialization and deserialization of Java objects to and from binary streams.
It uses MethodHandles for efficient field access, and provides a flexible layout system that can be customized with annotations.

### Key Features

- **Automatic serialization** of Java objects with minimal code
- **Support for all primitive types** and common objects like String and UUID
- **Custom layouts** for special serialization needs
- **Annotations** for fine-tuning serialization behavior
- ~~**Depends on** objenesis~~ **Bonus:** extra Objenesis library x1 as a gift

## Installation

### Maven

```xml
<dependency>
    <groupId>com.ydo4ki</groupId>
    <artifactId>DataStreamLayouts</artifactId>
    <version>1.2.1</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.ydo4ki:DataStreamLayouts:1.2.1'
```

## Usage

### Simple Example

```java
import java.io.*;
import java.util.UUID;
import com.ydo4ki.datalayouts.Layout;
import com.ydo4ki.datalayouts.StringEncoding;
import com.ydo4ki.datalayouts.annotation.*;
import java.lang.invoke.MethodHandles;

class PacketExample {
    @Encoding(StringEncoding.UTF8) // string will be encoded in utf-8
    public final String name;
    
    public final UUID uuid;
    public final int code;
    public final int other_code;
    public final long[] very_long_code_idk;
    
    public PacketExample(String name, UUID uuid, int code, int other_code, long... long_code_idk) {
        this.name = name;
        this.uuid = uuid;
        this.code = code;
        this.other_code = other_code;
        this.very_long_code_idk = long_code_idk;
    }
    
    static final Layout.Of<PacketExample> layout = Layout.of(PacketExample.class, MethodHandles.lookup());
    
    public void send(DataOutputStream out) throws IOException {
        // write all class fields to 'out' via layout
        layout.write(this, out);
    }
    
    public static PacketExample get(DataInputStream in) throws IOException {
        // object will be created by layout using data from 'in'
        return layout.read(in);
    }
}
```

## Annotations

Although you can create your custom annotations to customize serialization behavior, DataStreamLayouts provides several default annotations:

### `@Encoding`

Specifies the character encoding to use for string fields.

```java
@Encoding(StringEncoding.UTF8)
private String text; // Will be encoded as UTF-8
```

### `@Length`

Specifies a fixed length for arrays and strings.

```java
@Length(10)
private String fixedLengthString; // Always serialized as 10 characters

@Length(5)
private int[] fixedLengthArray; // Always serialized as 5 elements
```

### `@NullTerminated`

Specifies that strings should be null-terminated (C-style strings). String will store its size as first 4 bytes otherwise.

```java
@NullTerminated
private String cStyleString; // Terminated with a null character
```

### `@UnsignedByte` and `@UnsignedShort`

Specifies that an integer field should be treated as an unsigned byte or short.

```java
@UnsignedByte
private int smallValue; // 0-255, serialized as a single byte

@UnsignedShort
private int mediumValue; // 0-65535, serialized as two bytes
```

### Custom Layouts

You can create custom layouts for special serialization needs:

```java
// Create a layout for a specific class
Layout.Of<MyClass> myClassLayout = Layout.of(MyClass.class);

// Create a layout for a raw object (tuple-like)    (I honestly don't remember when and why I created this method XD)
Layout.Of<RawObject> rawLayout = Layout.ofRaw(String.class, UUID.class, int.class);

// Create a layout that skips bytes (for padding or alignment)
Layout.Of<Void> skipLayout = Layout.skip(4); // Skip 4 bytes
```

### Array Layouts

DataStreamLayouts supports both dynamic and static arrays:

```java
// Dynamic array (length is written to the stream as 4 bytes int)
int[] dynamicArray = {1, 2, 3, 4, 5};
Layout<int[]> dynamicLayout = Layout.of(int[].class);

// Static array (fixed length)
@Length(10)
int[] staticArray;
```

### Working with Streams

```java
// Writing to a file
// (it's actually not recommended to save objects to non-temp files but in case you're interested)
try (FileOutputStream fos = new FileOutputStream("data.bin");
     DataOutputStream dos = new DataOutputStream(fos)) {
    
    MyClass obj = new MyClass();
    Layout.Of<MyClass> layout = Layout.of(MyClass.class);
    layout.write(obj, dos);
}

// Reading from a file
try (FileInputStream fis = new FileInputStream("data.bin");
     DataInputStream dis = new DataInputStream(fis)) {
    
    Layout.Of<MyClass> layout = Layout.of(MyClass.class);
    MyClass obj = layout.read(dis);
}
```

### Registering Custom Encodings

You can register custom string encodings:

```java
// Create a custom encoding
class CustomEncoding extends StringEncoding {
    @Override
    public Integer size() {
        return 1; // 1 byte per character
    }
    
    @Override
    public void write(char x, DataOutput out) throws IOException {
        // Custom write logic
    }
    
    @Override
    public char read(DataInput in) throws IOException {
        // Custom read logic
    }
}

// Register the encoding
StringEncoding.registerEncoding(new CustomEncoding(), "custom");

// Use the encoding
@Encoding("custom")
private String customEncodedString;
```

## Performance Considerations

- DataStreamLayouts uses MethodHandles for efficient field access
- Layouts are typically created once and reused for multiple operations
- Static layouts (fixed size) can be more efficient than dynamic layouts

## Limitations

- All fields must be accessible (public or accessible via MethodHandle Lookup that you passed to Layout::of)
- Circular references are not handled automatically
- Inheritance requires special handling for virtual layouts (in case if you want to create common layout for all subclasses)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.<br>
<sub><sup>why am i even writing this 😭😭</sup></sub>

## Support

If you encounter any issues or have questions, please open an issue on the project repository.<br>
I will probably solve it. Someday definitely.