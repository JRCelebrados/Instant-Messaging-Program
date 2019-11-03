import javax.swing.JFrame;

public class ClientTest {
	
	public static void main(String[] args) {
		
		Client client1 = new Client("127.0.0.1");
		client1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client1.startRunning();
		
	}

}
