package serial;
//여기서 사용되는 모든 문서는 스트링 can에서는 string으로 만 통신이된다
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialReadWrite implements SerialPortEventListener {
	//can으로 뭔가가 들어오면 왔냐라는 이벤트가 발생하느데 SerialPortEventListener의미가  왔냐라는 이벤트 발생 되서serialEvent 여기서 받는다 

	CommPortIdentifier commPortIdentifier;
	CommPort comPort;//실질적으로 접속하는 api
	InputStream in;
	BufferedInputStream bin;
	OutputStream out;
	 
	 public SerialReadWrite() {
	 }
	 //portName 넘버를 넣어주면 
	 public SerialReadWrite(String portName) throws Exception {
	  commPortIdentifier = 
	  CommPortIdentifier.getPortIdentifier(portName);
	  //portName 넘버로 접근이 가능하다
	  System.out.println("Identifier Com Port!");
	  //접속
	  connect();
	  System.out.println("Connect Com Port!");
	  //접속한후 바로 송수신 시작 한다 를 바로 날려 준다 
	  new Thread(new SerialWrite()).start();
	  System.out.println("Start Can Network...");

	 }
	 
	 public void connect() throws Exception {
		 //commPortIdentifier정상적으로 작동하고 있는지 확인한후 작동
		 if(commPortIdentifier.isCurrentlyOwned()) {
			  System.out.println("Port is currently in us");
			 
		 }else {
			comPort = commPortIdentifier.open(this.getClass().getName(),5000);//(누가 접속한지 , 시간동안 응답없으면 닫음)
			//다른 격로로 들어올수있는 경우가 있기 때문에 SerialPort로만 작업한다 
			if(comPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort)comPort;
					serialPort.addEventListener(this);
				    serialPort.notifyOnDataAvailable(true);
				    serialPort.setSerialPortParams(921600, // 통신속도
				      SerialPort.DATABITS_8, // 데이터 비트// 프로토클 이 A와 B가 있는데 B가 8피트 B를 쓰겠다 
				      SerialPort.STOPBITS_1, // stop 비트
				      SerialPort.PARITY_NONE); // 패리티
				    in = serialPort.getInputStream();//소켓을 만든 
				    bin = new BufferedInputStream(in);//BufferedInputStream bin 으로 읽어드리가 쉽기떄문에 이걸로 씀 다른걸로하면 while루프 사용 해야된다
				    out = serialPort.getOutputStream();
				    //소캣을 만들고 input/outputStream을 만들기 까지 완료
				    
				    
			}else {
				 System.out.println("this port is not SerialPort");	
			}
		}
		 
	 }
	 
	 	
	 class SerialWrite implements Runnable{
			//write 를 하면서 다른 동작도 할려고 쓰래드를 사용 한다  
		String data;
		
		public SerialWrite() {
		this.data = ":G11A9\r";//can통신에서 주고 받는 데이터의 형식//G11A9: 송수신 시작이라는 코드 같은 느낌같음 can언어에서 
				}
		
		public SerialWrite(String msg) {
			//보내고자 하는 내용이 있으면 msg에 넣어서 보내준다
			this.data =converData(msg);
		}
		
		
		
		//프포토클 언어로 변화해주는 함수 
		public String converData(String msg) {
			msg =msg.toUpperCase();
			msg = "W28";
			//W28 00000000 000000000000
			char c[]= msg. toCharArray();
			int checkSum= 0;
			for(char ch:c) {
				checkSum += ch;
				
			}
			checkSum =(checkSum &0xFF);
			
			String result=":";
			result+= msg + Integer.toHexString(checkSum).toUpperCase()+"\r" ;//숫자를 10진수를 16진수로 바꾼다 그럼 checkSum 이 무조건 두자리로 나옴

			
			return result;
			
		}
		@Override
		public void run() {
			byte[] outData = data.getBytes();
			try {
				out.write(outData);// 송수신을 시작한다는 data를 cannetwork에 보낸다 근데 이게 어디에 존재하는지 아직 이해가 안됨
				//쌤이 올린 프로토클 사용 메뉴얼 참조 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	 }
	 //보내는 함수 
	 public void send(String msg) {
		 new Thread(new SerialWrite(msg)).start();
		 
	 }
	 
	 
	 
	 
	 
	 
	 @Override
	public void serialEvent(SerialPortEvent event) {
		 //can이 보낸 신호,데이터 다 받는 
		
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
			SerialReadWrite sc =null;
			String id ="00000000";
			String data ="0000000000000000";
			String msg = id+data;
			try {
				sc = new SerialReadWrite("COM8");
				sc.send(msg);//메세지(data)를 보낸다 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	


