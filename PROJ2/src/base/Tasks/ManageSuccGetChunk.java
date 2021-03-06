package base.Tasks;

import base.Peer;
import base.channel.MessageReceiver;
import base.channel.MessageSender;
import base.messages.MessageChunkNo;

import javax.net.ssl.SSLSocket;

import static base.Clauses.*;

/*
    Class that manages restore requests as initiator peer
 */
public class ManageSuccGetChunk implements Runnable {

    private final MessageChunkNo succGetChunk;
    private final SSLSocket client_socket;

    public ManageSuccGetChunk(String file_id, int chunk_no, SSLSocket client) {
        succGetChunk = new MessageChunkNo(SUCCGETCHUNK, Peer.getID(), file_id, chunk_no);
        client_socket = client;
    }

    @Override
    public void run() {
        Peer.getTaskManager().execute(new MessageSender(client_socket, succGetChunk));
        Peer.getTaskManager().execute(new MessageReceiver(client_socket));
    }
}
