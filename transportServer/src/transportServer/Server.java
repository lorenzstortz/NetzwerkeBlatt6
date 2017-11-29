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
		while(true){
			String s = sc.next();
			if(s.equals("y")||s.equals("Y")){
				TCP = true;
				break;
			}
			else if(s.equals("n")||s.equals("N")){
				TCP = false;
				break;
			}
		}
		try {
			if (TCP) tcpReciever();
			else udpReciever();
		}catch(SocketException e){
			e.printStackTrace();
		}
	}

	private static void tcpReciever() throws IOException{
		ServerSocket socket = new ServerSocket(PORT);
		Socket client = socket.accept();
		int bytes = 1400;
		long timeStart = 0;
		long timeEnd = 0;
		int data = 0;
		DataInputStream dIn = new DataInputStream(client.getInputStream());
		int length;
		while (true) {
			count = 0;
			data = 0;
			timeStart = System.currentTimeMillis();
			while (true) {
				// wait for packed
				long timeForPackage = System.currentTimeMillis();

				//count++;
				//byte[] test = new byte[1400];
				//dIn.readFully(test);                    // read length of incoming message
				//System.out.println("Package Length is: " + length);
				try {
					length = dIn.readInt();
				}catch (IOException e) {
					break;
				}
				if(length > 0) {

					count++;
					System.out.printf("package no. %d from %s recieved %n", count, client.getInetAddress());
					data += 1400;
					timeForPackage = System.currentTimeMillis();
				}
				if(System.currentTimeMillis()-timeForPackage > timeout) {
					System.out.println("Timeout!");
					break;
				}


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

	private static void udpReciever() throws SocketException{
		DatagramSocket UDPSocket =  new DatagramSocket(PORT);
		UDPSocket.setSoTimeout(timeout);
		int bytes = 1400;
		long timeStart = 0;
		long timeEnd = 0;
		int data = 0;
		System.out.println("Hallo?");
		while (true) {
			count = 0;
			data = 0;
			timeStart = System.currentTimeMillis();
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