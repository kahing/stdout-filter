package us.hxbc.stdoutfilter;

class ByteArrayCharSequence implements CharSequence {
    private final byte[] buf;
    private final int start;
    private final int end;

    ByteArrayCharSequence(byte[] buf, int start, int end) {
        this.buf = buf;
        this.start = start;
        this.end = end;
    }

    public int length() {
        return end - start;
    }

    public char charAt(int index) {
        return (char) buf[start + index];
    }

    public CharSequence subSequence(int start, int end) {
        return new ByteArrayCharSequence(buf, this.start + start, this.start + end);
    }

    public String toString() {
        return new String(buf, start, end);
    }
}
