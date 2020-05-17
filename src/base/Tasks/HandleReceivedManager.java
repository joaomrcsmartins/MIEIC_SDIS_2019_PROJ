package base.Tasks;

import base.Peer;
import base.TaskLogger;
import base.messages.BackupMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import static base.Clauses.*;

/*
    Class that manages incoming messages sent through multicast channels
*/
public class HandleReceivedManager implements Runnable {
    private final String[] msg_header;
    private final byte[] msg_body;
    private final Socket client_socket;

    public HandleReceivedManager(List<byte[]> msg, Socket c_socket) {
        this.msg_header = parseHeader(msg.get(0));
        this.msg_body = msg.get(1);
        this.client_socket = c_socket;
    }

    @Override
    public void run() {
        try {
            switch (msg_header[1]) {
                case PUTCHUNK:
                    handlePutChunk();
                    break;
                case STORED:
                    handleStored();
                    break;
                case DECLINED: //TODO: check if any handling is necessary with this message
                    break;
                case GETCHUNK:
                    handleGetChunk();
                    break;
                case CHUNK:
                    handleChunk();
                    break;
                case DELETE:
                    handleDeleteFile();
                    break;
                case ASKDELETE:
                    handleAskDelete();
                    break;
                case REMOVED:
                    handleRemovedChunk();
                    break;
                case DELETEREPLY:
                    handleDeleteReply();
                    break;
                case SUCINFO:
                    handleSucInfo();
                    break;
                case GETCHUNKSUC:
                    handleGetChunkSuc();
                    break;
                default:
                    TaskLogger.invalidMessage(msg_header[1]);
                    client_socket.close();
                    break;
            }
        } catch (NumberFormatException e) {
            TaskLogger.invalidSenderID(msg_header[2]);
        } catch (IOException e) {
            e.printStackTrace(); //log error
        }
    }

    private void handleGetChunkSuc() {
        Peer.getTaskManager().execute(new HandleForwardGet(msg_header,client_socket));
    }

    private void handleSucInfo() {
        Peer.getTaskManager().execute(new HandleGetToIdeal(msg_header,client_socket));
    }

    private void handleDeleteReply() {
        Peer.getTaskManager().execute(new HandleDeleteReply(msg_header));
    }

    private void handlePutChunk() {
        BackupMessage bckup = new BackupMessage(msg_header, msg_body);
        Peer.getTaskManager().execute(new HandlePutChunk(bckup, client_socket));
    }

    private void handleStored() {
        Peer.getTaskManager().execute(new HandleStored(msg_header, new InetSocketAddress(client_socket.getInetAddress(), client_socket.getPort())));
    }

    private void handleGetChunk() {
        Peer.getTaskManager().execute(new HandleGetChunk(msg_header, client_socket));
    }

    private void handleChunk() {
        Peer.getTaskManager().execute(new HandleChunk(msg_header, msg_body));
    }

    private void handleDeleteFile() {
        Peer.getTaskManager().execute(new HandleDeleteFile(msg_header, client_socket));
    }

    private void handleAskDelete() {
        Peer.getTaskManager().execute(new HandleDeleteOffline(msg_header, client_socket));
    }

    private void handleRemovedChunk() {
        Peer.getTaskManager().execute(new HandleRemovedChunk(msg_header));
    }
}
