import com.ydo4ki.datalayouts.Layout;
import com.ydo4ki.datalayouts.StringEncoding;
import com.ydo4ki.datalayouts.annotations.*;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.UUID;

/**
 * @since 12/1/2024 10:37 PM
 * @author Sulphuris
 */
public class Main {
	
	private static final ByteArrayOutputStream data = new ByteArrayOutputStream(64);
	
	public static void main(String[] args) throws IOException {
		DataOutputStream output = new DataOutputStream(data);
		
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid);
		PacketExample packet = new PacketExample("oaoa", uuid,4, 6, 55, 66, 77);
		
		packet.send(output);
		
		byte[] bytes = data.toByteArray();
		
		System.out.println(Arrays.toString(bytes));
		DataInput input = new DataInputStream(new ByteArrayInputStream(bytes));
		packet = PacketExample.layout.read(input);
		
		// ok now that beautiful library works
		// at least i have to add arrays support and add binds for classes like String and primitive wrappers
		System.out.println(packet);
	}
}


@SuppressWarnings("FieldCanBeLocal")
class PacketExample {
	// so i came up with system that allows anyone to create serialization rules via field annotations
	// FINALLY
	// so now we can create any custom annotation and use it to specify serialization rules
	@Encoding(StringEncoding.UTF8)
	@NullTerminated // yeeey it works too
	//@Length(4) // YEEEEEEY IT WORKS TOOOOO
	// works perfectly
	// yes
	// that's it i'm happy
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
	
	// I want unsafe.allocateInstance by reflection =<
	public PacketExample() {
		this("", UUID.randomUUID(), 0,0,0);
	}
	
	static final Layout.Of<PacketExample> layout = Layout.of(PacketExample.class, MethodHandles.lookup());
	
	public void send(DataOutputStream out) throws IOException {
		layout.write(this, out);
	}
	
	@Override
	public String toString() {
		return "PacketExample{" +
				"name='" + name + '\'' +
				", uuid=" + uuid +
				", code=" + code +
				", other_code=" + other_code +
				", very_long_code_idk=" + Arrays.toString(very_long_code_idk) +
				'}';
	}
}
