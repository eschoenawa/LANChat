package de.eschoenawa.lanchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Server implements Runnable {

	private GUI parent;
	private boolean response;
	private DatagramSocket serverSocket;

	public Server(GUI parent) {
		super();
		this.parent = parent;
		this.response = true;
	}

	@Override
	public void run() {
		try {
			serverSocket = new DatagramSocket(55545);
			byte[] receiveData = new byte[1400];
			while (response) {
				DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(packet);
				InetAddress ip = packet.getAddress();
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println("Received: " + received + " from " + ip.toString());
				if (received.toLowerCase().startsWith(Config.load().getCommandPrefix()) && response) {
					String value = received.split(":")[1];
					parent.addValue(value);
					send(ip, Config.load().getResponsePrefix() + Config.load().getName());
				} else if (received.toLowerCase().startsWith(Config.load().getResponsePrefix()) && response) {
					String value = received.split(":")[1];
					parent.addValue(value);
				} 
				else if (received.toLowerCase().startsWith(Config.load().getUpdatePrefix())) {
					if (response)
						parent.discover();
				}
				
				else {
					parent.receive(received);
				}
			}
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void stopResponse() {
		this.response = false;
	}

	public boolean doesRespond() {
		return this.response;
	}

	public void sendDiscoveryMessage() {
		try {
			sendToBroadcast(Config.load().getCommandPrefix() + Config.load().getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToBroadcast(String s) {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback())
					continue; // Don't want to broadcast to the loopback
				// interface
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;
					send(broadcast, s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(InetAddress ip, String s) {
		int port = serverSocket.getLocalPort();
		DatagramPacket packet = new DatagramPacket(s.getBytes(), s.getBytes().length, ip, port);
		try {
			serverSocket.send(packet);
			System.out.println("Sent: " + s + " to " + ip.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
