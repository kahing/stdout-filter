package us.hxbc.stdoutfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ByteArrayCharSequenceTest {
    byte[] bytes = "helloworld".getBytes();
    ByteArrayCharSequence sequence = new ByteArrayCharSequence(bytes, 0, bytes.length);
    
    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.Test
    public void testSubSequence() throws Exception {
        assertEquals("hello", sequence.subSequence(0, 5).toString());
    }

    @Test
    public void testRegex() throws Exception {
        String s = "java.lang.ArrayIndexOutOfBoundsException: 52264";
        byte[] buf = s.getBytes();
        ByteArrayCharSequence seq = new ByteArrayCharSequence(buf, 0, buf.length);
        assertTrue(FilteredPrintStream.EXCEPTION_PATTERN.matcher(seq).matches());
    }
}