package serial;
//���⼭ ���Ǵ� ��� ������ ��Ʈ�� can������ string���� �� ����̵ȴ�
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialConnect implements SerialPortEventListener {
	//can���� ������ ������ �ԳĶ�� �̺�Ʈ�� �߻��ϴ��� SerialPortEventListener�ǹ̰�  �ԳĶ�� �̺�Ʈ �߻� �Ǽ�serialEvent ���⼭ �޴´� 

	CommPortIdentifier commPortIdentifier;
	CommPort comPort;//���������� �����ϴ� api
	InputStream in;
	BufferedInputStream bin;
	OutputStream out;
	 
	 public SerialConnect() {
	 }
	 //portName �ѹ��� �־��ָ� 
	 public SerialConnect(String portName) throws Exception {
	  commPortIdentifier = 
	  CommPortIdentifier.getPortIdentifier(portName);
	  //portName �ѹ��� ������ �����ϴ�
	  System.out.println("Identifier Com Port!");
	  //����
	  connect();
	  System.out.println("Connect Com Port!");
	  //�������� �ٷ� �ۼ��� ���� �Ѵ� �� �ٷ� ���� �ش� 
	  new Thread(new SerialWrite()).start();
	  System.out.println("Start Can Network...");

	 }
	 
	 public void connect() throws Exception {
		 //commPortIdentifier���������� �۵��ϰ� �ִ��� Ȯ������ �۵�
		 if(commPortIdentifier.isCurrentlyOwned()) {
			  System.out.println("Port is currently in us");
			 
		 }else {
			comPort = commPortIdentifier.open(this.getClass().getName(),5000);//(���� �������� , �ð����� ��������� ����)
			//�ٸ� �ݷη� ���ü��ִ� ��찡 �ֱ� ������ SerialPort�θ� �۾��Ѵ� 
			if(comPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort)comPort;
					serialPort.addEventListener(this);
				    serialPort.notifyOnDataAvailable(true);
				    serialPort.setSerialPortParams(921600, // ��żӵ�
				      SerialPort.DATABITS_8, // ������ ��Ʈ// ������Ŭ �� A�� B�� �ִµ� B�� 8��Ʈ B�� ���ڴ� 
				      SerialPort.STOPBITS_1, // stop ��Ʈ
				      SerialPort.PARITY_NONE); // �и�Ƽ
				    in = serialPort.getInputStream();//������ ���� 
				    bin = new BufferedInputStream(in);//BufferedInputStream bin ���� �о�帮�� ���⋚���� �̰ɷ� �� �ٸ��ɷ��ϸ� while���� ��� �ؾߵȴ�
				    out = serialPort.getOutputStream();
				    //��Ĺ�� ����� input/outputStream�� ����� ���� �Ϸ�
				    
				    
			}else {
				 System.out.println("this port is not SerialPort");	
			}
		}
		 
	 }
	 
	 	
	 class SerialWrite implements Runnable{
			//write �� �ϸ鼭 �ٸ� ���۵� �ҷ��� �����带 ��� �Ѵ�  
		String data;
		
		public SerialWrite() {
		this.data = ":G11A9\r";//can��ſ��� �ְ� �޴� �������� ����//G11A9: �ۼ��� �����̶�� �ڵ� ���� �������� can���� 
				}
		
		@Override
		public void run() {
			byte[] outData = data.getBytes();
			try {
				out.write(outData);// �ۼ����� �����Ѵٴ� data�� cannetwork�� ������ �ٵ� �̰� ��� �����ϴ��� ���� ���ذ� �ȵ�
				//���� �ø� ������Ŭ ��� �޴��� ���� 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	 }
	 
	 
	 
	 
	 
	 
	 @Override
	public void serialEvent(SerialPortEvent event) {
		 //can�� ���� ��ȣ,������ �� �޴� 
		
			  switch (event.getEventType()) {
			  case SerialPortEvent.BI:
			  case SerialPortEvent.OE:
			  case SerialPortEvent.FE:
			  case SerialPortEvent.PE:
			  case SerialPortEvent.CD:
			  case SerialPortEvent.CTS:
			  case SerialPortEvent.DSR:
			  case SerialPortEvent.RI:
			  case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			   break;
			  case SerialPortEvent.DATA_AVAILABLE:
			   byte[] readBuffer = new byte[128];

			   try {

			    while (bin.available() > 0) {
			     int numBytes = bin.read(readBuffer);
			    }

			    String ss = new String(readBuffer);
			    System.out.println("Receive Low Data:" + ss + "||");
			    
			   } catch (Exception e) {
			    e.printStackTrace();
			   }
			   break;
			  }
			 }
	 

	 public static void main(String[] args) {
			SerialConnect sc =null;
			try {
				sc = new SerialConnect("COM8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	


