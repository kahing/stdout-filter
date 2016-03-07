package us.hxbc.stdoutfilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Main {
	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		PrintStream out = new PrintStream(new FilteredPrintStream(System.out), true);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				out.println(line);
			}
		} catch (IOException e) {
			// ignore
		}
	}
}
