package mhir.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;

public class LoggerStub implements Logger {

    private final String name;
    private final Level level = Level.TRACE;
    private final List<LogEntry> entries;

    public LoggerStub(String name) {
        super();
        this.name = name;
        this.entries = new ArrayList<>();
    }

    public List<LogEntry> getEntries(Level level) {
        List<LogEntry> matchingEntries = new ArrayList<>();
        for (LogEntry entry : this.entries) {
            // Example:         level == Level.INFO (20)
            //          entry.level() == Level.WARN (30)
            if (level.toInt() <= entry.level().toInt()) {
                matchingEntries.add(entry);
            }
        }
        return matchingEntries;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.level.toInt() <= Level.TRACE.toInt();
    }

    @Override
    public void trace(String msg) {
        this.trace((Marker) null, msg, new Object[]{});
    }

    @Override
    public void trace(String format, Object arg) {
        this.trace((Marker) null, format, new Object[]{arg});
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        this.trace((Marker) null, format, new Object[]{arg1, arg2});
    }

    @Override
    public void trace(String format, Object... arguments) {
        this.trace((Marker) null, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        this.trace((Marker) null, msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return this.isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
        this.trace(marker, msg, new Object[]{});
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        this.trace(marker, format, new Object[]{arg});
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        this.trace(marker, format, new Object[]{arg1, arg2});
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        LogEntry msg = new LogEntry(Level.TRACE, String.format(format, argArray), marker, null);
        this.entries.add(msg);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        LogEntry lm = new LogEntry(Level.TRACE, msg, marker, t);
        this.entries.add(lm);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.level.toInt() <= Level.DEBUG.toInt();
    }

    @Override
    public void debug(String msg) {
        this.debug((Marker) null, msg, new Object[]{});
    }

    @Override
    public void debug(String format, Object arg) {
        this.debug((Marker) null, format, new Object[]{arg});
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        this.debug((Marker) null, format, new Object[]{arg1, arg2});
    }

    @Override
    public void debug(String format, Object... arguments) {
        this.debug((Marker) null, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        this.debug((Marker) null, msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return this.isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        this.debug(marker, msg, new Object[]{});
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        this.debug(marker, format, new Object[]{arg});
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        this.debug(marker, format, new Object[]{arg1, arg2});
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        LogEntry entry = new LogEntry(Level.DEBUG, String.format(format, arguments), marker, null);
        this.entries.add(entry);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        LogEntry entry = new LogEntry(Level.DEBUG, msg, marker, t);
        this.entries.add(entry);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.level.toInt() <= Level.INFO.toInt();
    }

    @Override
    public void info(String msg) {
        this.info((Marker) null, msg, new Object[]{});
    }

    @Override
    public void info(String format, Object arg) {
        this.info((Marker) null, format, new Object[]{arg});
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        this.info((Marker) null, format, new Object[]{arg1, arg2});
    }

    @Override
    public void info(String format, Object... arguments) {
        this.info((Marker) null, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        this.info((Marker) null, msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return this.isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        this.info(marker, msg, new Object[]{});
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        this.info(marker, format, new Object[]{arg});
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        this.info(marker, format, new Object[]{arg1, arg2});
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        LogEntry entry = new LogEntry(Level.INFO, String.format(format, arguments), marker, null);
        this.entries.add(entry);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        LogEntry entry = new LogEntry(Level.INFO, msg, marker, t);
        this.entries.add(entry);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.level.toInt() <= Level.WARN.toInt();
    }

    @Override
    public void warn(String msg) {
        this.warn((Marker) null, msg, new Object[]{});
    }

    @Override
    public void warn(String format, Object arg) {
        this.warn((Marker) null, format, new Object[]{arg});
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        this.warn((Marker) null, format, new Object[]{arg1, arg2});
    }

    @Override
    public void warn(String format, Object... arguments) {
        this.warn((Marker) null, format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        this.warn((Marker) null, msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return this.isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        this.warn(marker, msg, new Object[]{});
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        this.warn(marker, format, new Object[]{arg});
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        this.warn(marker, format, new Object[]{arg1, arg2});
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        LogEntry entry = new LogEntry(Level.WARN, String.format(format, arguments), marker, null);
        this.entries.add(entry);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        LogEntry entry = new LogEntry(Level.WARN, msg, marker, t);
        this.entries.add(entry);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.level.toInt() <= Level.ERROR.toInt();
    }

    @Override
    public void error(String msg) {
        this.error((Marker) null, msg, new Object[]{});
    }

    @Override
    public void error(String format, Object arg) {
        this.error((Marker) null, format, new Object[]{arg});
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        this.error((Marker) null, format, new Object[]{arg1, arg2});
    }

    @Override
    public void error(String format, Object... arguments) {
        this.error((Marker) null, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        this.error((Marker) null, msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return this.isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        this.error(marker, msg, new Object[]{});
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        this.error(marker, format, new Object[]{arg});
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        this.error(marker, format, new Object[]{arg1, arg2});
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        LogEntry entry = new LogEntry(Level.ERROR, String.format(format, arguments), marker, null);
        this.entries.add(entry);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        LogEntry entry = new LogEntry(Level.ERROR, msg, marker, t);
        this.entries.add(entry);
    }
}
