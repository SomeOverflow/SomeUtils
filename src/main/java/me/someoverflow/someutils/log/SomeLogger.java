package me.someoverflow.someutils.log;

import me.someoverflow.someutils.file.SomeFile;

import java.io.IOException;

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

    /*
     * public SomeLogger(String name) {
     *         this.name             = name;
     *         this.defaultFormatter = new SomeLogFormatter.Default();
     *
     *         this.toConsole        = true;
     *
     *         this.toFile           = false;
     *         this.fileManager      = null;
     *     }
     *     public SomeLogger(String name, SomeLogFormatter formatter) {
     *         this.name             = name;
     *
     *         this.toConsole        = true;
     *
     *         this.toFile           = false;
     *         this.fileManager      = null;
     *         setBFormatter(formatter);
     *     }
     *     public SomeLogger(String name, boolean toConsole, String filePath, String fileName) {
     *         this.name             = name;
     *         this.defaultFormatter = new SomeLogFormatter.Default();
     *
     *         this.toConsole        = toConsole;
     *
     *         this.toFile           = true;
     *         this.fileManager      = new SomeFile(filePath, fileName);
     *     }
     */

    /**
     * Create a Logger with the given name
     * @param name The Name
     */
    public SomeLogger(String name) {
        this(name, false, true, new SomeLogFormatter.Default(), new SomeLogFormatter.Default(), false, null, null);
    }

    public SomeLogger(String name, boolean debugging) {
        this(name, debugging, true, new SomeLogFormatter.Default(), new SomeLogFormatter.Default(), false, null, null);
    }

    public SomeLogger(String name, SomeLogFormatter consoleF) {
        this(name, false, true, consoleF, consoleF, false, null, null);
    }

    public SomeLogger(String name, boolean toConsole, String filePath, String fileName) {
        this(name, false, toConsole, new SomeLogFormatter.Default(), new SomeLogFormatter.Default(), true, filePath, fileName);
    }

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

        if (toFile)
            this.fileManager    = new SomeFile(filePath, fileName);
        else
            this.fileManager    = null;
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

    /**
     * Log a message with the given {@link LogLevel}
     *
     * @param logLevel {@link LogLevel}
     * @param message The message to be output
     */
    public void log(LogLevel logLevel, String message) {
        message = message + ConsoleColors.RESET;
        if (toConsole && !debugging) System.out.println(consoleFormatter.format(name, logLevel, message));

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
        message = message + ConsoleColors.RESET;
        if (toConsole && !debugging) System.out.println(consoleFormatter.format(name, logLevel, message));
        if (toFile) {
            try {
                assert fileManager != null;
                fileManager.write(fileFormatter.format(name, logLevel, message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.ERROR'
     * @param message The message that is to be output
     */
    public void error(String message) {
        log(LogLevel.ERROR, ConsoleColors.RED + message);
    }
    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.WARNING'
     * @param message The message that is to be output
     */
    public void warn(String message) {
        log(LogLevel.WARNING, ConsoleColors.RED_BRIGHT + message);
    }
    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.INFO'
     * @param message The message that is to be output
     */
    public void info(String message) {
        log(LogLevel.INFO, ConsoleColors.CYAN + message);
    }
    /**
     * Doing this:
     * {@link SomeLogger#log(LogLevel, String)}
     * with 'LogLevel.DEBUG'
     * ( Logged only when {@link SomeLogger#isDebugging()} is on )
     * @param message The message that is to be output
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, ConsoleColors.GREEN + message);
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
