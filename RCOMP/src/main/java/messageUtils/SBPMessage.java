package messageUtils;

public class SBPMessage {
    private static final int MESSAGE_MAX_SIZE_BYTES = 510;
    private final int version;
    private final int code;
    private final int d_length_1;
    private final int d_length_2;
    private final String data;

    public SBPMessage(int version, int code, String data) {
        int dataLength, dataLength1, dataLength2;
        if(code < 0 || code > SharedConstants.CURRENT_MAX_CODE) {
            throw new IllegalArgumentException("Invalid code!");
        }
        dataLength = data.length();
        if(dataLength > MESSAGE_MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("Message exceeds capacity limits");
        }

        if(dataLength > 255) {
            dataLength1 = 255;
            dataLength2 = dataLength - 255;
        } else {
            dataLength1 = dataLength;
            dataLength2 = 0;
        }

        this.version = version;
        this.code = code;
        this.d_length_1 = dataLength1;
        this.d_length_2 = dataLength2;
        this.data = data;
    }

    public SBPMessage(int version, int code, int d_length_1, int d_length_2, String data) {
        this.version = version;
        this.code = code;
        this.d_length_1 = d_length_1;
        this.d_length_2 = d_length_2;
        this.data = data;
    }

    public int version() {
        return version;
    }
    public int code() {
        return code;
    }
    public int d_length_1() {
        return d_length_1;
    }
    public int d_length_2() {
        return d_length_2;
    }
    public String data() {
        return data;
    }
}
