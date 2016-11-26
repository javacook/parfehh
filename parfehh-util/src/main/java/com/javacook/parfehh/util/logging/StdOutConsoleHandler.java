package com.javacook.parfehh.util.logging;

import java.util.logging.*;

/**
 * Created by vollmer on 09.10.16.
 */
public class StdOutConsoleHandler extends ConsoleHandler {

        @Override
        public void publish(LogRecord record) {
            if (getFormatter() == null) {
                setFormatter(new SimpleFormatter());
            }

            try {
                String message = getFormatter().format(record);
                if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                    System.err.write(message.getBytes());
                }
                else {
                    System.out.write(message.getBytes());
                }
            }
            catch (Exception exception) {
                reportError(null, exception, ErrorManager.FORMAT_FAILURE);
            }
        }

        @Override
        public void close() throws SecurityException {}

        @Override
        public void flush(){}

}