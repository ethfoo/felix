package provider;

import java.util.HashMap;
import java.util.Map;

import classSource.Hello;
import classSource.HelloImple;

import com.ethfoo.registry.LocalAddressProvider;
import com.ethfoo.server.Server;

public class Provider {

	public static void main(String[] args) {

		Map<String, Object> beans = new HashMap<String, Object>();
		System.out.println("provider put name: " + Hello.class.getName());
		beans.put(Hello.class.getName(), new HelloImple());
		
		Server server = new Server(new LocalAddressProvider());
		try {
			server.export(beans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
