package base.Tasks;

import base.TaskLogger;
import base.channels.ChannelManager;
import base.messages.MessageChunkNo;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

public class ManageRemoveChunk implements Runnable {

    MessageChunkNo rmv_message;

    public ManageRemoveChunk(String v, String type, int sid, String fid, int number) {
        rmv_message = new MessageChunkNo(v, type, sid, fid, number);
    }

    @Override
    public void run() {
        try {
            processMessage();
        } catch (UnknownHostException e) {
            TaskLogger.sendMessageFail();
        }
    }

    private void processMessage() throws UnknownHostException {
        ChannelManager.getCntrChannel().sendMessage(rmv_message.createMessageFinal().getBytes());
    }
}
