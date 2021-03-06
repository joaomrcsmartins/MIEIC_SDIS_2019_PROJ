package base.Tasks;

import static base.Clauses.STORED;

import base.Peer;
import base.channel.MessageSender;
import base.messages.MessageChunkNo;

import javax.net.ssl.SSLSocket;

public class ManageStored implements Runnable {

    private final MessageChunkNo st_message;
    private final SSLSocket client_socket;

    public ManageStored(int sid, String fid, int chunkno, SSLSocket socket) {
        st_message = new MessageChunkNo(STORED, sid, fid, chunkno);
        client_socket = socket;
    }

    @Override
    public void run() {
        Peer.getTaskManager().execute(new MessageSender(client_socket, st_message));
    }
}
