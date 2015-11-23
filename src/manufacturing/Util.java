package manufacturing;

public class Util {
    static final int LOG_NONE = 0,
                     LOG_BASIC = 1,
                     LOG_DETAIL = 2,
                     LOG_VERBOSE = 3;
    static int logLevel = LOG_VERBOSE;
    public static void logVerbose(String message) {
        if (logLevel >= LOG_VERBOSE)
            System.out.println(message);
    }

    public static void logDetail(String message) {
        if (logLevel >= LOG_DETAIL)
            System.out.println(message);
    }

    public static void logBasic(String message) {
        if (logLevel >= LOG_BASIC)
            System.out.println(message);
    }
}
