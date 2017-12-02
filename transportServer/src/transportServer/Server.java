package transportServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Server {
	private static boolean TCP = false;
	private final static int PORT = 4711;
	private static int timeout = 1000;
	private static long count = 0;

	public static void main(String[] args) throws IOException {
		System.out.println("Do you want to use TCP: [y,n]");
		Scanner sc = new Scanner(System.in);
		while (true) {
			String s = sc.next();
			if (s.equals("y") || s.equals("Y")) {
				TCP = true;
				break;
			} else if (s.equals("n") || s.equals("N")) {
				TCP = false;
				break;
			}
		}
		try {
			if (TCP)
				tcpReciever();
			else
				udpReciever();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Can´t connect to server");
		}
	}

	private static void tcpReciever() throws IOException {
		long timeStart = 0;
		long timeEnd = 0;
		int data = 0;
		int length;
		while (true) {
			ServerSocket socket = new ServerSocket(PORT);
			Socket client = socket.accept();
			client.setSoTimeout(timeout);
			DataInputStream dIn = new DataInputStream(client.getInputStream());
			count = 0;
			data = 0;
			while (true) {
				long timeForPackage = System.currentTimeMillis();
				try {
					length = dIn.readInt();
				} catch (IOException e) {
					break;
				}
				if (length > 0) {
					if (count == 0) {
						timeStart = System.currentTimeMillis();
					}
					count++;
					data += 1400;
					timeForPackage = System.currentTimeMillis();
					timeEnd = System.currentTimeMillis();
					System.out.printf("package no. %d from %s recieved", count, client.getInetAddress());
					System.out.printf(" time: %s %n", timeEnd - timeStart);
				}
				if (System.currentTimeMillis() - timeForPackage > timeout) {
					System.out.println("Timeout!");
					break;
				}

			}
			double difTime = timeEnd - timeStart;
			System.out.printf(" time: %s %n", difTime);
			if (count != 0) {
				System.out.printf("Packets recieved: %d %n", count);
				System.out.printf("Data recieved: %d bytes %n", data);
				System.out.printf("Time difference: %.2f s %n", difTime / 1000);
				System.out.printf("Throughput: %.0f kbits/s %n %n", (data / difTime) * 8);
			}
			client.close();
			socket.close();
		}
	}

	private static void udpReciever() throws SocketException {
		DatagramSocket UDPSocket = new DatagramSocket(PORT);
		UDPSocket.setSoTimeout(timeout);
		int bytes = 1400;
		long timeStart = 0;
		long timeEnd = 0;
		int data = 0;
		while (true) {
			count = 0;
			data = 0;
			
			while (true) {
				// wait for packet
				DatagramPacket packet = new DatagramPacket(new byte[bytes], bytes);
				try {
					UDPSocket.receive(packet);
				} catch (SocketTimeoutException e) {
					// timeout
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (count == 0) {
					timeStart = System.currentTimeMillis();
				}
				timeEnd = System.currentTimeMillis();
				count++;
				// get data
				InetAddress address = packet.getAddress();
				data += packet.getLength();
				System.out.printf("package no. %d from %s recieved %n", count, address);

			}
			
			double difTime = timeEnd - timeStart;
			if (count != 0) {
				System.out.printf("Packets recieved: %d %n", count);
				System.out.printf("Data recieved: %d bytes %n", data);
				System.out.printf("Time difference: %.2f s %n", difTime / 1000);
				System.out.printf("Throughput: %.0f kbits/s %n %n", (data / difTime) * 8);
			}
		}
	}
}