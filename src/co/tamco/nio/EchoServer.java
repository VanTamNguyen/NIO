package co.tamco.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by tam-co on 17/07/2017.
 */

public class EchoServer {

	private int port;

	private ServerSocketChannel serverSocketChannel;

	private Selector selector;

	private ByteBuffer buffer = ByteBuffer.allocate(512);

	public EchoServer(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		selector = Selector.open();

		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(port));

		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		Iterator<SelectionKey> iterator;
		while (true) {
			selector.select();
			iterator = selector.selectedKeys().iterator();

			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();

				if (key.isAcceptable()) {
					accept(key);
				} else if (key.isReadable()) {
					read(key);
				}
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);

		String address = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
		System.out.println("Accepted connection from: [" + address + "]");
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		StringBuilder sb = new StringBuilder();

		buffer.clear();
		while(socketChannel.read(buffer) > 0) {
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			sb.append(new String(bytes));
			buffer.clear();
		}

		String msg = sb.toString();

		ByteBuffer responseBuf = ByteBuffer.wrap(msg.getBytes());
		socketChannel.write(responseBuf);
		responseBuf.rewind();
	}

	public static void main(String... args) throws IOException {
		EchoServer server = new EchoServer(9999);
		server.start();
	}
}
