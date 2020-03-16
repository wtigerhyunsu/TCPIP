package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import msg.Msg;

public class Server {

	int port;

	ServerSocket serverSocket;
	HashMap<String, ObjectOutputStream> maps = new HashMap<>();

	boolean aflag = true; // while roop �ȿ��� ����� flag

	public Server() {

	}

	// ��Ʈ�� �޾ƿ��� �ν��Ͻ� �޽�� //
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Start Server");
		// ���Ͽ� ���� ������ ���� //
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// ������ ��� ���� ����� ������ ��������� �ƴ� ������ ���� while �ȿ� �ֱ�. //
				Socket socket = null;
				while (aflag) {
					try {
						System.out.println("Server is Ready.. ");
						socket = serverSocket.accept();
						System.out.println(socket.getInetAddress() + ": Connected");
						System.out.println(socket.getInetAddress().getHostAddress());
						System.out.println(socket.getPort() + "");
						// ���Ͽ��� � IP �� ����ߴ��� Ȯ���� �� �ִ�. //
						// ������ ��������� makeOut �̶�� �Լ� ȣ�� //
						makeOut(socket);
//						System.out.println(socket.getInputStream().read() + "");
//						while (socket.getInputStream().read() == -1) {
//							System.out.println("asdf");
//							continue;
//						}
						new Receiver(socket).start();
						System.out.println("HI");
						// outputstream �� ������ְ� ���� ����

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		};
		new Thread(r).start();
	}

	public void makeOut(Socket socket) throws IOException {
		OutputStream os;
		ObjectOutputStream oos;

		os = socket.getOutputStream();
		oos = new ObjectOutputStream(os);
		maps.put(socket.getInetAddress().toString(), oos);
		System.out.println("Visitor ip : " + socket.getInetAddress().toString());
		System.out.println("Visitor size : " + maps.size());

	}

	// Client �� Sender �� ����� Server �� Receiver //
	class Receiver extends Thread {

		InputStream is;
		ObjectInputStream ois;

		// Receiver �� ������ �ֱ⶧���� ���⼭ oos �����. //
//		OutputStream os;
//		ObjectOutputStream oos;

		Socket socket;

		public Receiver(Socket socket) {
			System.out.println("1");
			// Client ������ Output stream , Server ������ Input Stream //
			this.socket = socket;
			System.out.println("2");
			System.out.println(socket.isConnected());
			System.out.println((socket == null) + "");
//			is = socket.getInputStream();
//			System.out.println(socket.getInputStream().read() + "");
//			System.out.println(is.read() + "");
			System.out.println("3");
			try {
				is = socket.getInputStream();
				ois = new ObjectInputStream(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("4");

//			os = socket.getOutputStream();
//			oos = new ObjectOutputStream(os);

			// ���� �������� �ּҸ� Ű������ , object ���� value �� �ؽ��� maps �� ���� //
//			maps.put(socket.getInetAddress().toString(), oos);
//			System.out.println("Visitors: " + maps.size());
		}

		@Override
		public void run() {
			System.out.println("5");
			// while : Client �� ������ �������� �ʴ� �� ��� //
			System.out.println("6");
			while (ois != null) {
				System.out.println("7");
				Msg msg = null;
				try {
					System.out.println("Receiver run() while(ois !=null) try");

					msg = (Msg) ois.readObject();
					System.out.println(
							socket.getInetAddress() + ":" + msg.getId() + ":" + msg.getTxt() + ":" + msg.getTid());
//					msg = (Msg) ois.readObject();
					// ���� data�� ��´�. //

//					if (msg.getTxt().equals("q")) {
//						System.out.println(socket.getInetAddress() + msg.getId() + ":Exit");
//						// q �� ������ ���� ���̹Ƿ� //
//						Thread.sleep(1000);
//						maps.remove(socket.getInetAddress().toString());
//						System.out.println("Visitors:" + maps.size());
//						break;
//					}
//					int count = 0;
//					// ��ε�ĳ��Ʈ //
//					while (true) {
//
//						Thread.sleep(1000);
//						System.out.println("reconnecting pad server : " + ++count);
//						if (msg.getId().equals("tabregister")) {
//							sendMsg(msg);
//							break;
//						}
//					}
					sendMsg(msg);

				} catch (Exception e) {
					e.printStackTrace();
					// ������ �����Ͽ��� � ����� �������� Ȯ�� ����. //
					maps.remove(socket.getInetAddress().toString());
					System.out.println(socket.getInetAddress() + ":Exit");
					System.out.println("Visitor size : " + maps.size());
					break; // dis.readUTF �� ������ ����� while roop �ߴ�.
				} 
			}

			System.out.println("ois == null");
			try {
				if (ois != null) {
					ois.close();
				}
				if (socket != null) {

					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	class Sender extends Thread {
		Msg msg;

		public Sender(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			System.out.println("Sender run() msg.getId : " + msg.getId());
			System.out.println("Sender run() msg.getTxt : " + msg.getTxt());
			System.out.println("Sender run() msg.getTid : " + msg.getTid());
			// HashMap �� �ִ� oos �� �������� for ���� �����鼭 �����Ѵ�. //
			System.out.println("Sender run() entered");
			Collection<ObjectOutputStream> cols = maps.values();
			Iterator<ObjectOutputStream> its = cols.iterator();
			while (its.hasNext()) {
				try {
					its.next().writeObject(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	// sender 2 ���� , �ӼӸ� ���//
	class Sender2 extends Thread {
		Msg msg;

		public Sender2(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {

			String ip = msg.getTid();
			System.out.println("Sender2 run() msg.getId : " + msg.getId());
			System.out.println("Sender2 run() msg.getTxt : " + msg.getTxt());
			System.out.println("Sender2 run() msg.getTid : " + msg.getTid());
			try {
//				maps.get("/192.168.43.180").writeObject(msg);
//				maps.get("/192.168.43.53").writeObject(msg);
//				maps.get("/192.168.43.111").writeObject(msg);
				maps.get(ip).writeObject(msg);
//				maps.get("/70.12.225.91").writeObject(msg);
			} catch (

			IOException e) {
				e.printStackTrace();
			}

		}

	}

	// ������ ����ڿ��� �޽��� ������.//
	public void sendMsg(Msg msg) {
		System.out.println("sendMsg(Msg msg) entered");
		String ip = msg.getTid();
		// null �� �׻� ���� ������. //
		if (ip == null || ip.equals("")) {
//		 ��ü�� broadcast //
			Sender sender = new Sender(msg);
			sender.start();
		} else {
			// �Ѹ��� //
			System.out.println("Sender2");
			Sender2 sender2 = new Sender2(msg);
			sender2.start();
		}
	}

	public void serverStart() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Input ip");
			String ip = sc.nextLine();
			System.out.println("Input Msg");
			String txt = sc.nextLine();
			Msg msg = null;
			if (txt.equals("q"))
				break;
			// �ӼӸ� ����� �ֱ����� ip�� �Է¹޴´�. constructor ���� ip �߰�
			if (ip == null || ip.equals("")) {
				msg = new Msg("Admin", txt, null);
			} else {
				msg = new Msg("Admin", txt, ip);
			}
			sendMsg(msg);
		}
		sc.close();
	}

	// ���� ������ //
	public static void main(String[] args) {

		Server server = null;
		try {
			// server ȣ�� = ������ ���� //
			server = new Server(8888);
			server.serverStart();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}