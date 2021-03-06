package base.channel;

import base.Peer;
import base.PeerLogger;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class MessageListener implements Runnable {
    SSLServerSocket server_socket;
    int serverPort;

    public MessageListener(int port) throws IOException {
        serverPort = port;
        server_socket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(serverPort);
        server_socket.setNeedClientAuth(true);
        server_socket.setEnabledCipherSuites(new String[]{
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA"
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                SSLSocket client = (SSLSocket) server_socket.accept();
                Peer.getTaskManager().execute(new MessageReceiver(client));
            } catch (IOException e) {
                PeerLogger.disconnectPeer();
            }
        }
    }
}
