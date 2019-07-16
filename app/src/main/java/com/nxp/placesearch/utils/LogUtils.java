package com.nxp.placesearch.utils;

import android.text.TextUtils;
import android.util.Log;

import com.nxp.placesearch.BuildConfig;

public class LogUtils {
    public static final boolean ENABLE_LOG = BuildConfig.DEBUG;
    private static final String LOG_PREFIX = "mWallet2Go_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;
    private static final String ENTER_TAG;
    private static final String EXIT_TAG;

    static {
        ENTER_TAG = " <<<<<<<<";
        EXIT_TAG = " >>>>>>>>";
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static String getMethodName() {
        return new Exception().getStackTrace()[1].getMethodName();
    }

    public static String getClassName() {
        return new Exception().getStackTrace()[1].getClassName();
    }

    private static void outLog(LogLevel loglevel, String tag, String message) {

        if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(message)) {
            return;
        }

        switch (loglevel) {
            case LOG_LEVEL_ERROR:
                Log.e(tag, message);
                break;
            case LOG_LEVEL_WARNING:
                Log.w(tag, message);
                break;
            case LOG_LEVEL_INFO:
                Log.i(tag, message);
                break;
            case LOG_LEVEL_DEBUG:
                Log.d(tag, message);
                break;
            case LOG_LEVEL_VERBOSE:
                Log.v(tag, message);
                break;
            default:
                break;
        }

    }

    public static void trace(String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_DEBUG, tag, message);
        }
    }

    /**
     * for display debug. working same as Log.d(tag,log) with enter tag
     * <p/>
     * use at starting of method to mark initial point.
     */
    public static void enter(String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_DEBUG, tag, message + ENTER_TAG);
        }
    }

    /**
     * for display debug. working same as Log.d(tag,log) with exit tag
     * <p/>
     * use at ending of method to mark exit point.
     */
    public static void exit(String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_DEBUG, tag, message + EXIT_TAG);
        }
    }

    public static void checkIf(String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_DEBUG, tag, message);
        }
    }

    public static void checkIf(final String tag, String message, Throwable cause) {
        if (ENABLE_LOG) {
            Log.d(tag, message, cause);
        }
    }

    public static void checkAll(final String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_VERBOSE, tag, message);
        }
    }

    public static void checkAll(final String tag, String message, Throwable cause) {
        if (ENABLE_LOG) {
            Log.v(tag, message, cause);
        }
    }

    /**
     * for display information . working same as Log.i(tag,message)
     */
    public static void info(String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_INFO, tag, message);
        }
    }

    /**
     * for display warning. working same as Log.d(tag,message)
     */
    public static void warning(String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_WARNING, tag, message);
        }
    }

    /**
     * for display error. working same as Log.e(tag,message)
     */
    public static void error(String tag, String message) {
        if (ENABLE_LOG) {
            outLog(LogLevel.LOG_LEVEL_ERROR, tag, message);
        }
    }

    /**
     * for display error. working same as Log.e(tag,message). Added print stack track and Crashlytics
     * caught exception logging
     */
    public static void error(String tag, String message, Throwable cause) {
        error(tag, message);
    }

    /**
     * for display error. Added print stack track and Crashlytics caught exception
     * logging
     */
    public static void error(String tag, Throwable cause) {
        error(tag, cause.toString());
    }

    enum LogLevel {
        LOG_LEVEL_ERROR, LOG_LEVEL_WARNING, LOG_LEVEL_INFO, LOG_LEVEL_DEBUG, LOG_LEVEL_VERBOSE
    }
}