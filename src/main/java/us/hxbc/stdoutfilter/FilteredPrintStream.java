package us.hxbc.stdoutfilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;


public class FilteredPrintStream extends ByteArrayOutputStream {
    @VisibleForTesting
    static final Pattern EXCEPTION_PATTERN = Pattern.compile(".*Exception(: .*)?");
    private static final String[] EXCEPTIONS_TO_SKIP = new String[] {
            "com.avaje.ebean.enhance.agent.IgnoreClassHelper.isIgnoreClass(",
            "com.avaje.ebean.enhance.asm.ClassReader.readClass(",
            "com.avaje.ebean.enhance.asm.ClassReader.<init>(",
            "com.avaje.ebean.enhance.asm.ClassReader.readUTF8(",
            "com.avaje.ebean.enhance.asm.ClassReader.accept("
    };

    private final PrintStream out;
    private byte[] exceptionNameLine;
    private State state = State.REGULAR;

    enum State {
        REGULAR,
        EXCEPTION_NAME,
        SKIP_STACKTRACE,
    }

    FilteredPrintStream(PrintStream out) {
        this.out = out;
    }

    @Override
    public void flush() throws IOException {
        if (count == 0) {
            return;
        }

        if (count == 1 && buf[0] == '\n') {
            if (state == State.REGULAR) {
                out.write(buf, 0, count);
            }
            reset();
            return;
        }

        if (state == State.SKIP_STACKTRACE && isStackTrace()) {
            exceptionNameLine = null;
            reset();
            return;
        }

        if (isExceptionName()) {
            maybeFlushExceptionName();
            exceptionNameLine = copy();
            state = State.EXCEPTION_NAME;
        } else {
            if (state == State.EXCEPTION_NAME) {
                String loc = getStackTrace();
                if (loc != null && skipException(loc)) {
                    exceptionNameLine = null;
                    state = State.SKIP_STACKTRACE;
                } else {
                    flushException();
                }
            } else {
                out.write(buf, 0, count);
                state = State.REGULAR;
            }
        }

//        if (state != State.REGULAR)
//            STDOUT.println("[" + new String(buf, 0, count) + "]: " + state);

        reset();
    }

    private void maybeFlushExceptionName() throws IOException {
        if (exceptionNameLine != null) {
            out.write(exceptionNameLine);
            out.println();
            exceptionNameLine = null;
        }
    }

    private void flushException() throws IOException {
        maybeFlushExceptionName();
        out.write(buf, 0, count);
        state = State.REGULAR;
    }

    private byte[] copy() {
        byte[] tmp = new byte[super.count];
        System.arraycopy(super.buf, 0, tmp, 0, super.count);
        return tmp;
    }

    boolean skipException(String line) {
        for (String s : EXCEPTIONS_TO_SKIP) {
            if (line.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    boolean isExceptionName() {
        return EXCEPTION_PATTERN.matcher(new ByteArrayCharSequence(super.buf, 0, super.count)).matches();
    }

    boolean isStackTrace() {
        return count > 4 && buf[0] == '\t' && buf[1] == 'a' && buf[2] == 't' && buf[3] == ' ';
    }

    String getStackTrace() {
        if (isStackTrace()) {
            return new String(buf, 4, count - 4);
        }
        return null;
    }

}
