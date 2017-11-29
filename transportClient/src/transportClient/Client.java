package transportClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

class Client {
	static final int MILLISECONDS = 30000;
	static boolean TCP;
	static final int SLEEPTIME = 100;
	static final int WHENTOSLEEP = 100;
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
		
		ia = InetAddress.getByName("10.179.15.93");
		byte[] raw = new byte[1400];
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
			System.out.printf("Packets send: %d %n", count);

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
			System.out.printf("Packets send: %d %n", count);

		} catch (SocketException e) {
			System.out.println("Can´t connect to server.");
		} catch (IOException e) {
			System.out.println("Can´t send to server.");
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted.");
		}

	}

}
