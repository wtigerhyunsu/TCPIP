package client;

import java.util.Random;

import msg.Msg;

public class SendData implements Runnable {
	boolean flag = false;
	Sender sender;
	String CID;

	public SendData() {

	}

	public SendData(Sender sender) {
		this.sender = sender;
		CID = sender.getMsg().getId();
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void run() {
		while (true) {
			if (flag == false) {
				continue;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Sending random number");
			Random r = new Random();
			int data = r.nextInt(100);
			Msg msg = new Msg("car1", data + "", CID);
			sender.setMsg(msg);
			new Thread(sender).start();
		}
	}

}
