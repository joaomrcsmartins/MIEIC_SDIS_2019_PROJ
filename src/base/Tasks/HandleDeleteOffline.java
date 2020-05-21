package base.Tasks;

import base.Peer;
import base.messages.BackupMessage;
import base.messages.BaseMessage;

import javax.net.ssl.SSLSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class HandleDeleteOffline implements Runnable {

    private BaseMessage delete_message;
    private SSLSocket client_socket;

    public HandleDeleteOffline(BaseMessage msg, SSLSocket client_socket) {
        delete_message = msg;
        this.client_socket = client_socket;
    }

    @Override
    public void run() {
        /*ArrayList<String> delete_requests = Peer.getStorageManager().getDeleteRequests();
        delete_requests.forEach((value) -> Peer.getTaskManager().execute(new ManageDeleteFile(delete_message.getVersion(), Peer.getID(), value, client_socket)));*/
    }
}
