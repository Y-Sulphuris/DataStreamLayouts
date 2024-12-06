# DataStreamLayouts
Simple java library that automates sending objects via DataOutput and DataInput streams<br>


Usage example:

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
	
	// it's necessary to have no args constructor
	private PacketExample() {
		this("", null, 0, 0, 0);
	}
	
	static final Layout.Of<PacketExample> layout = Layout.of(PacketExample.class, MethodHandles.lookup());
	
	public void send(DataOutputStream out) throws IOException {
		// write all class fields to 'out' via layout
		layout.write(this, out);
	}
	
	public static PacketExample get(DataInputStream in) throws IOException {
		// object will be created by layout using data from 'in'
		return layout.read(this, out);
	}
}
```