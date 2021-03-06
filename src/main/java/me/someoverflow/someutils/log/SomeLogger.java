package me.someoverflow.someutils.log;

import me.someoverflow.someutils.file.SomeFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author SomeOverflow
 */
public class SomeLogger {

    private final String name;

    private final SomeFile fileManager;
    private final boolean toFile;
    private boolean toConsole;

    private boolean debugging;

    private SomeLogFormatter consoleFormatter;
    private SomeLogFormatter fileFormatter;

    /**
     * Create a Logger with the given name
     * @param name The Name
     */
    public SomeLogger(String name) {
        this(name, false, true, new SomeLogFormatter.Default(), new SomeLogFormatter.Default(), false, null, null);
    }

    /**
     * Create a Logger with the given name and enable/disable debugging
     * @param name Logger name
     * @param debugging Enable/Disable debugging
     */
    public SomeLogger(String name, boolean debugging) {
        this(name, debugging, true, new SomeLogFormatter.Default(), new SomeLogFormatter.Default(), false, null, null);
    }

    /**
     * Create a Logger with the given name and with a custom console formatter
     * @param name Logger name
     * @param consoleF Custom Formatter for Console
     */
    public SomeLogger(String name, SomeLogFormatter consoleF) {
        this(name, false, true, consoleF, consoleF, false, null, null);
    }

    /**
     * Create a Logger with the given name and with file writing
     * @param name Logger name
     * @param toConsole Should the Logger write to Console
     * @param filePath The path of the File (should end with / )
     * @param fileName The name of the File
     */
    public SomeLogger(String name, boolean toConsole, String filePath, String fileName) {
        this(name, false, toConsole, new SomeLogFormatter.Default(), new SomeLogFormatter.Default(), true, filePath, fileName);
    }

    /**
     * Create a Logger with the given Values
     * @param name Logger name
     * @param debugging Enable/Disable debugging
     * @param toConsole Should the Logger write to Console
     * @param consoleFormatter Custom Formatter for Console
     * @param fileFormatter Custom Formatter for File
     * @param toFile Should the Logger write to a File
     * @param filePath The path of the File (should end with / )
     * @param fileName The name of the File
     */
    public SomeLogger(
            String name, boolean debugging, boolean toConsole,
            SomeLogFormatter consoleFormatter, SomeLogFormatter fileFormatter,
            boolean toFile, String filePath, String fileName
    ) {
        this.name               = name;
        this.debugging          = debugging;
        this.toConsole          = toConsole;
        this.toFile             = toFile;

        this.consoleFormatter   = consoleFormatter;
        this.fileFormatter      = fileFormatter;

        SomeFile tFileManager;
        if (toFile) {
            tFileManager = new SomeFile(filePath, fileName);

            if (!tFileManager.fileCreated) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                try {
                    tFileManager.move(filePath + "logs/");
                    String createName = "." + fileName.substring(fileName.lastIndexOf(".") + 1);
                    tFileManager.rename(sdf.format(new Date()) + createName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                tFileManager = new SomeFile(filePath, fileName);
            }
        } else
            tFileManager = null;
        this.fileManager = tFileManager;
    }

    /**
     * Set whether the logger should log {@link LogLevel Debug LogLevel's}
     *
     * @param debugging should he debug
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    /**
     * @return When the logger is debugging
     */
    public boolean isDebugging() {
        return debugging;
    }

    /**
     * Set whether the logger should log to the console
     *
     * @param toConsole should he write to the console
     */
    public void setToConsole(boolean toConsole) {
        this.toConsole = toConsole;
    }

    /**
     * Set the format in which everything will be output
     * #Use now {@link SomeLogger#setBFormatter(SomeLogFormatter)}
     * (Will be deleted in newer versions!)
     *
     * @param defaultFormatter Format
     */
    @Deprecated
    public void setFormatter(SomeLogFormatter defaultFormatter) {
        setBFormatter(defaultFormatter);
    }

    /**
     * Set a custom formatter for both (Console and File)
     * @param formatter The formatter
     */
    public void setBFormatter(SomeLogFormatter formatter) {
        setConsoleFormatter(formatter);
        setFileFormatter(formatter);
    }

    /**
     * Set a custom formatter only for the Console
     * @param formatter The formatter
     */
    public void setConsoleFormatter(SomeLogFormatter formatter) {
        this.consoleFormatter = formatter;
    }
    /**
     * Set a custom formatter only for the Files
     * @param formatter The formatter
     */
    public void setFileFormatter(SomeLogFormatter formatter) {
        this.fileFormatter = formatter;
    }

    // TODO: 06.01.2022 Fix the colors in the File
    /**
     * Log a message with the given {@link LogLevel}
     *
     * @param logLevel {@link LogLevel}
     * @param message The message to be output
     */
    public void log(LogLevel logLevel, String message) {
        if (toConsole) {
            if (!debugging) {
                if (logLevel != LogLevel.DEBUG)
                    System.out.println(consoleFormatter.format(name, logLevel, message + ConsoleColors.RESET));
            } else
                System.out.println(consoleFormatter.format(name, logLevel, message + ConsoleColors.RESET));
        }

        if (!toFile) return;
        try {
            assert fileManager != null;
            fileManager.write(fileFormatter.format(name, logLevel, message));
        } catch (IOException ignored) { }
    }

    /**
     * Log a message with the given {@link SomeLogFormatter Formatter}
     *
     * @param logLevel {@link LogLevel The LogLevel of the Log}
     * @param message The message to be logged
     * @param formatter {@link SomeLogFormatter The Formatter}
     */
    public void logWF(LogLevel logLevel, String message, SomeLogFormatter formatter) {
        if (toConsole) {
            if (!debugging) {
                if (logLevel != LogLevel.DEBUG)
                    System.out.println(consoleFormatter.format(name, logLevel, message + ConsoleColors.RESET));
            } else
                System.out.println(consoleFormatter.format(name, logLevel, message + ConsoleColors.RESET));
        }

        if (toFile) return;
        try {
            assert fileManager != null;
            fileManager.write(formatter.format(name, logLevel, message));
        } catch (IOException ignored) { }
    }

    private String errorColor = ConsoleColors.RED;
    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.ERROR'
     * @param message The message that is to be output
     */
    public void error(String message) {
        log(LogLevel.ERROR, errorColor + message);
    }

    private String warnColor = ConsoleColors.RED_BRIGHT;
    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.WARNING'
     * @param message The message that is to be output
     */
    public void warn(String message) {
        log(LogLevel.WARNING, warnColor + message);
    }

    private String infoColor = ConsoleColors.CYAN;
    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.INFO'
     * @param message The message that is to be output
     */
    public void info(String message) {
        log(LogLevel.INFO, infoColor + message);
    }

    private String debugColor = ConsoleColors.GREEN;
    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.DEBUG'
     * ( Logged only when {@link SomeLogger#isDebugging()} is on )
     * @param message The message that is to be output
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, debugColor + message);
    }

    /**
     * This is like a prefix for the messages, so you can use {@link ConsoleColors}
     * or just a {@link String}
     *
     * @param logLevel The {@link LogLevel} wich you want to change the color(prefix)
     * @param color The color(prefix) to set
     */
    public void changeColor(LogLevel logLevel, String color) {
        if (logLevel == LogLevel.ERROR)
            errorColor = color;
        if (logLevel == LogLevel.WARNING)
            warnColor = color;
        if (logLevel == LogLevel.INFO)
            infoColor = color;
        if (logLevel == LogLevel.DEBUG)
            debugColor = color;
    }

    /**
     * Every LogLevel with the "name"
     */
    public enum LogLevel {
        ERROR("EROR"), WARNING("WARN"), INFO("INFO"), DEBUG("DBUG");

        private final String name;
        LogLevel(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
}
