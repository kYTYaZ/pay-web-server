package com.huateng.pay.common.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 网关调用后台socket接口 
 * @author justin
 * @see 1.0
 * @since 1.0
 */
public class SocketConnectionClient {
	public SocketChannel client = null;
	public InetSocketAddress isa = null;
	public RecvThread rt = null;
	public String result = null;
	private Logger logger = LoggerFactory.getLogger(SocketConnectionClient.class
			.getName());

	public SocketConnectionClient() {
	}

	public void makeConnection(String socketadd, int socketport)
			throws IOException {
		client = SocketChannel.open();
		isa = new InetSocketAddress(socketadd, socketport);
		client.connect(isa);
		System.out.println(".................");
		client.configureBlocking(false);
		receiveMessage();
	}

	public int sendMessage(String msg) {
		logger.info("Inside SendMessage");
		ByteBuffer bytebuf = ByteBuffer.allocate(msg.getBytes().length);
		int nBytes = 0;
		try {
			logger.info("msg is " + msg);
			bytebuf = ByteBuffer.wrap(msg.getBytes());
			nBytes = client.write(bytebuf);
			logger.info("nBytes is " + nBytes);
			if (msg.equals("quit") || msg.equals("shutdown")) {
				logger.info("time to stop the client");
				interruptThread();
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				client.close();
				return -1;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		logger.info("Wrote " + nBytes + " bytes to the server");
		return nBytes;
	}
	
	public int sendMessage(byte[] msg) {
        logger.info("Inside SendMessage");
        ByteBuffer bytebuf = ByteBuffer.allocate(msg.length);
        int nBytes = 0;
        try {
            logger.info("msg is " + msg);
            bytebuf = ByteBuffer.wrap(msg);
            nBytes = client.write(bytebuf);
            logger.info("nBytes is " + nBytes);
            if (msg.equals("quit") || msg.equals("shutdown")) {
                logger.info("time to stop the client");
                interruptThread();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                	logger.error(e.getMessage(),e);
                }
                client.close();
                return -1;
            }
        } catch (IOException e) {
        	logger.error(e.getMessage(),e);
        }
        logger.info("Wrote " + nBytes + " bytes to the server");
        return nBytes;
    }
	

	public void receiveMessage() {
		rt = new RecvThread("Receive THread", client);
		rt.start();
	}

	public void interruptThread() {
		rt.val = false;
	}

	public void stopClient() throws IOException {
		client.close();
	}

	public class RecvThread extends Thread {
		public SocketChannel sc = null;
		public boolean val = true;

		public RecvThread(String str, SocketChannel client) {
			super(str);
			sc = client;
		}

		@SuppressWarnings("unused")
		public void run() {
			logger.info("Inside receivemsg");
			int nBytes = 0;
			ByteBuffer buf = ByteBuffer.allocate(2048);
			try {
				while (val) {
					while ((nBytes = client.read(buf)) > 0) {
						buf.flip();
						Charset charset = Charset.forName("us-ascii");
						// CharsetDecoder decoder = charset.newDecoder();
						CharBuffer charBuffer = charset.decode(buf);
						result = charBuffer.toString();
						logger.info("SocketConnectionClient : " + result);
						buf.flip();
					}
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

	public String getResult() {
		return result;
	}
}