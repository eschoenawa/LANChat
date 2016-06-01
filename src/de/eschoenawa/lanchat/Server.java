package de.eschoenawa.lanchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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
			DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
			while (response) {
				serverSocket.receive(packet);
				InetAddress ip = packet.getAddress();
				String received = new String(packet.getData(), 0, packet.getLength());
				if (received.toLowerCase().startsWith(Config.load().getCommandPrefix()) && response) {
					String value = received.split(":")[1];
					parent.addValue(value);
					int port = packet.getPort();
					byte[] data = (Config.load().getResponsePrefix() + Config.load().getName()).getBytes();
					packet = new DatagramPacket(data, data.length, ip, port);
					serverSocket.send(packet);
				} else if (received.toLowerCase().startsWith(Config.load().getResponsePrefix()) && response) {
					String value = received.split(":")[1];
					parent.addValue(value);
				} else {
					parent.receive(received);
				}
			}
			serverSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
