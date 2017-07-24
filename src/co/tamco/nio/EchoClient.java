package co.tamco.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by tam-co on 24/07/2017.
 */
public class EchoClient {

	Socket echoSocket;

	public EchoClient() {
	}

	public void connect(String address, int port) throws IOException {
		echoSocket = new Socket(address, port);

		PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		String userInput;
		while ((userInput = stdIn.readLine()) != null) {
			out.println(userInput);
			System.out.println("Echo: " + in.readLine() + "\n");
		}
	}

	public static void main(String... args) throws IOException {
		EchoClient client = new EchoClient();
		client.connect(InetAddress.getLocalHost().getHostAddress(), 9999);
	}
}
