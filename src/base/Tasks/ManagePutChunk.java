package base.Tasks;

import base.FailedPutChunk;
import base.Peer;
import base.TaskLogger;
import base.channel.MessageReceiver;
import base.channel.MessageSender;
import base.messages.BackupMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static base.Clauses.*;

/*
    Class that implements PutChunk has an initiator peer
 */
public class ManagePutChunk implements Runnable {

    private final Socket client_socket;
    private final BackupMessage bk_message;
    private int n_try;

    public ManagePutChunk(String v, int sid, String fid, int chunkno, int repd, int n_chunks, byte[] bdy, Socket client) throws IOException {
        bk_message = new BackupMessage(v, PUTCHUNK, sid, fid, chunkno, repd, n_chunks, bdy);
        n_try = 0;
        client_socket = client;
    }

    @Override
    public void run() {
        try {
            processMessage();
        } catch (IOException | FailedPutChunk e) {
            TaskLogger.putChunkFail(bk_message.getFileId(), bk_message.getNumber());
        }
    }

    public void processMessage() throws FailedPutChunk, IOException {
        int curr_rep_degree = Peer.getStorageManager().getCurrentRepDegree(bk_message.getFileId(), bk_message.getNumber());
        if (curr_rep_degree < bk_message.getReplicationDeg()) {

            if (n_try < 1) {
                Peer.getTaskManager().execute(new MessageSender(client_socket,bk_message));
                Peer.getStorageManager().addStoredChunkRequest(bk_message.getFileId(), bk_message.getNumber(),bk_message.getSenderId());
                Peer.getTaskManager().execute(new MessageReceiver(client_socket));
                if(bk_message.getSenderId() != NOT_INITIATOR) {
                    Peer.getTaskManager().schedule(this, (long) (TIMEOUT*3), TimeUnit.MILLISECONDS);
                    n_try++;
                }
            } else {
                Peer.getStorageManager().removeStoredOccurrenceChunk(bk_message.getFileId(), bk_message.getNumber());
                throw new FailedPutChunk();
            }
        } else {
            TaskLogger.putChunkOk();

        }
    }
}
