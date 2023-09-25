public class ServerMessages {
    public static final String SERVER_CONFIRMATION = "<16-bitové číslo v decimální notaci>\u0007\u0008";
    public static final String SERVER_MOVE = "102 MOVE\u0007\u0008";
    public static final String SERVER_TURN_LEFT = "103 TURN LEFT\u0007\u0008";
    public static final String SERVER_TURN_RIGHT = "104 TURN RIGHT\u0007\u0008";
    public static final String SERVER_PICK_UP = "105 GET MESSAGE\u0007\u0008";
    public static final String SERVER_LOGOUT = "106 LOGOUT\u0007\u0008";
    public static final String SERVER_KEY_REQUEST = "107 KEY REQUEST\u0007\u0008";
    public static final String SERVER_OK = "200 OK\u0007\u0008";
    public static final String SERVER_LOGIN_FAILED = "300 LOGIN FAILED\u0007\u0008";
    public static final String SERVER_SYNTAX_ERROR = "301 SYNTAX ERROR\u0007\u0008";
    public static final String SERVER_LOGIC_ERROR = "302 LOGIC ERROR\u0007\u0008";
    public static final String SERVER_KEY_OUT_OF_RANGE_ERROR = "303 KEY OUT OF RANGE\u0007\u0008";

    public ServerMessages() {
    }
}
