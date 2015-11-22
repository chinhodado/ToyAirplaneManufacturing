package manufacturing;

public class Util {
    static boolean LOG_VERBOSE = false;
    public static void logVerbose(String message) {
        if (LOG_VERBOSE)
            System.out.println(message);
    }
}
