package transportClient;

import java.io.IOException;
import java.net.*;
import java.util.*;

class Client {
	static final int MILLISECONDS = 10000;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		InetAddress ia = InetAddress.getByName("localhost");
		int count = 1;
		long timeStart = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - timeStart <= MILLISECONDS) {
			byte[] raw = new byte[1400];

			DatagramPacket packet = new DatagramPacket(raw, raw.length, ia, 4711);

			DatagramSocket dSocket = new DatagramSocket();

			dSocket.send(packet);

			System.out.println("packet send");

			count++;
			
			if (count % 10 == 0) {
				Thread.sleep(500);
			}

		}
	}
}