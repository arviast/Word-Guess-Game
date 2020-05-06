import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	String ip;
	int port;
	GuessInfo guessClass = new GuessInfo();
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call, String ipNum, int portNum){
		ip = ipNum;
		port = portNum;
		callback = call;
	}
	
	public void run() {
		
		try {
		socketClient= new Socket(ip,port);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}
		
		while(true) {
			try {
				String message = in.readObject().toString();
				callback.accept(message);
			}
			catch(Exception e) {}
		}
	
    }
	
	public void send(GuessInfo info) {
		
		try {
			out.writeObject(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
