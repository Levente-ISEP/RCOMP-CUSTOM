package messageUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageService {
    public void sendMessage(SBPMessage message, DataOutputStream sOut) throws IOException {

        sOut.write((byte) message.version());    //version
        sOut.write((byte) message.code());       //code
        sOut.write((byte) message.d_length_1()); //data lenght 1
        sOut.write((byte) message.d_length_2()); //data lenght 2
        sOut.writeBytes(message.data());         //message
    }

    public SBPMessage readMessage(DataInputStream sIn) throws IOException {
        int version = sIn.read();
        int code = sIn.read();
        int d_length_1 = sIn.read();
        int d_length_2 = sIn.read();
        String message = "";

        for(int i = 0; i < (d_length_1 + d_length_2); i++) {
            message = message.concat(String.valueOf((char) sIn.read()));
        }
        return new SBPMessage(version, code, d_length_1, d_length_2, message);
    }
}
