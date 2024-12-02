import com.ydo4ki.datalayouts.Layout;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @since 12/1/2024 10:37 PM
 * @author Sulphuris
 */
public class Main {
	
	private static final ByteArrayOutputStream data = new ByteArrayOutputStream(64);
	
	public static void main(String[] args) throws IOException {
		System.out.println(Arrays.toString(PacketExample.class.getInterfaces()));
		if (true) return;
		DataOutputStream output = new DataOutputStream(data);
		PacketExample packet = new PacketExample(4, 6, 55, 66, 77);
		
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
class PacketExample extends ArrayList {
	public final int code;
	public final int other_code;
	public final long[] very_long_code_idk;
	
	public PacketExample(int code, int other_code, long... long_code_idk) {
		this.code = code;
		this.other_code = other_code;
		this.very_long_code_idk = long_code_idk;
	}
	
	// I want unsafe.allocateInstance by reflection =<
	public PacketExample() {
		this(0,0,0);
	}
	
	static final Layout.Of<PacketExample> layout = Layout.of(PacketExample.class, MethodHandles.lookup());
	
	public void send(DataOutputStream out) throws IOException {
		layout.write(this, out);
	}
	
	@Override
	public String toString() {
		return "PacketExample{" +
				"code=" + code +
				", other_code=" + other_code +
				", long_code_idk=" + Arrays.toString(very_long_code_idk) +
				'}';
	}
}