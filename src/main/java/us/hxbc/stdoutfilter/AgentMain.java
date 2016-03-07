package us.hxbc.stdoutfilter;

import java.io.PrintStream;
import java.lang.instrument.Instrumentation;

public class AgentMain {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.setOut(new PrintStream(new FilteredPrintStream(System.out), true));
        System.setErr(new PrintStream(new FilteredPrintStream(System.err), true));
    }
}
