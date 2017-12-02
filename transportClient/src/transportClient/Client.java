package transportClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

class Client {
	static final int MILLISECONDS = 2000;
	static final int DATA = 1400;
	static boolean TCP;
	static final int SLEEPTIME = 10;
	static final int WHENTOSLEEP = 5;
	static final int PORT = 4711;
	private static InetAddress ia;
	private static int count = 0;
	

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("Do you want to use TCP? [y/n]:");
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
        sc.close();
		
		ia = InetAddress.getByName("localhost");
		byte[] raw = new byte[DATA];
		raw[0] = 1;
		if (TCP) {
			tcpSend(raw);
		} else {
			udpSend(raw);
		}

	}

	private static void tcpSend(byte[] raw) throws InterruptedException {
		try (Socket sSocket = new Socket(ia, PORT);
				DataOutputStream outputStream = new DataOutputStream(sSocket.getOutputStream())) {
			long timeStart = System.currentTimeMillis();
			while (System.currentTimeMillis() - timeStart <= MILLISECONDS) {

				outputStream.write(raw);
				outputStream.flush();
				count++;

				System.out.printf("packet no. %d send to %s %n", count, sSocket.getInetAddress());

				if (count % WHENTOSLEEP == 0) {
					Thread.sleep(SLEEPTIME);
				}

			}
			long timeEnd = System.currentTimeMillis();
			double difTime = timeEnd - timeStart;
			
			System.out.printf("Packets send: %d %n", count);
			System.out.printf("Data send: %d bytes %n", count * DATA);
			System.out.printf("Time difference: %.2f s %n", difTime / 1000);
			System.out.printf("Sendrate: %.0f kbits/s %n %n", (count * DATA / difTime) * 8);
		

		} catch (IOException e) {
			System.out.println("Can´t connect to server.");
		}

	}

	private static void udpSend(byte[] raw) {
		DatagramPacket packet = new DatagramPacket(raw, raw.length, ia, PORT);

		try (DatagramSocket dSocket = new DatagramSocket();) {
			long timeStart = System.currentTimeMillis();
			while (System.currentTimeMillis() - timeStart <= MILLISECONDS) {

				dSocket.send(packet);

				count++;

				System.out.printf("packet no. %d send to %s %n", count, packet.getAddress());

				if (count % WHENTOSLEEP == 0) {
					Thread.sleep(SLEEPTIME);
				}

			}
			long timeEnd = System.currentTimeMillis();
			double difTime = timeEnd - timeStart;
			
			System.out.printf("Packets send: %d %n", count);
			System.out.printf("Data send: %d bytes %n", count * DATA);
			System.out.printf("Time difference: %.2f s %n", difTime / 1000);
			System.out.printf("Sendrate: %.0f kbits/s %n %n", (count * DATA / difTime) * 8);
		

		} catch (SocketException e) {
			System.out.println("Can´t connect to server.");
		} catch (IOException e) {
			System.out.println("Can´t send to server.");
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted.");
		}

	}

}
