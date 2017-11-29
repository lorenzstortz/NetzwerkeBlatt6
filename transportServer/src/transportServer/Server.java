package transportServer;

import java.io.IOException;
import java.net.*;

public class Server {
	public static void main(String[] args) throws IOException {
		DatagramSocket socket = new DatagramSocket(4711);
		int timeout = 1000;
		socket.setSoTimeout(timeout);
		int count = 0;
		int bytes = 1400;
		long timeStart = 0;
		long timeEnd = 0;
		int data = 0;
		while (true) {
			count = 0;
			data = 0;
			timeStart = System.currentTimeMillis();
			while (true) {
				// wait for packed
				DatagramPacket packet = new DatagramPacket(new byte[bytes], bytes);
				try {
					if (count == 0) {
					}
					socket.receive(packet);
				} catch (SocketTimeoutException e) {
					// timeout
					break;
				}

				count++;
				// get data
				InetAddress address = packet.getAddress();
				data += packet.getLength();
				System.out.printf("package no. %d from %s recieved %n", count, address);

			}
			timeEnd = System.currentTimeMillis();
			double difTime = timeEnd - timeStart - timeout;
			if (count != 0) {
				System.out.printf("Packets recieved: %d %n", count);
				System.out.printf("Data recieved: %d bytes %n", count * data);
				System.out.printf("Time difference: %.2f s %n", difTime / 1000);
				System.out.printf("Throughput: %.0f kbits/s %n %n", (data / difTime) * 8);
			}
		}
	}
}