package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import common.Log;

public class StpTable {

	public static final int INITIAL_SIZE = 1024;
	public SortedArrayList<Stp> records; 

	public StpTable(String name) throws IOException {
		records = new SortedArrayList<Stp>(INITIAL_SIZE);
		load(name);
	}

	public void load(String filename) throws IOException {
		int i = 0;
		BufferedReader dis = new BufferedReader(new FileReader(filename));
		String line;
		try {
			while ((line = dis.readLine()) != null) {
				if (line != null) {
					Stp s = addLine(line);
					//Log.println("Loaded: " + s);
					i++;
				}
			}
			dis.close();
			Log.println("Loaded "+i+" records from STP file: " + filename);
		} catch (IOException e) {
			e.printStackTrace(Log.getWriter());
			Log.println(e.getMessage());
		} catch (NumberFormatException n) {
			n.printStackTrace(Log.getWriter());
			Log.println(n.getMessage());
		} finally {
			dis.close();
		}

	}

	private Stp addLine(String line) {
		if (line.length() == 0) return null;
		String date = null;
		int id = 0;
		int resets = 0;
		long uptime = 0;
		int type = 0;
		int sequenceNumber;
		int length;
		String source;
		String receiver;
		String frequency;
		String rx_location;
		String receiver_rf;
		String demodulator;
		String measuredTCA;
		String measuredTCAfrequency;
		String date_time;

		StringTokenizer st = null;
		try {
			st = new StringTokenizer(line, ",");
			date = st.nextToken();
			date = date + st.nextToken();
			id = Integer.valueOf(st.nextToken()).intValue();
			resets = Integer.valueOf(st.nextToken()).intValue();
			uptime = Long.valueOf(st.nextToken()).longValue();
			type = Integer.valueOf(st.nextToken()).intValue();
			sequenceNumber = Integer.valueOf(st.nextToken()).intValue();
			length = Integer.valueOf(st.nextToken()).intValue();
			source = st.nextToken();
			receiver = st.nextToken();
			frequency = st.nextToken();
			rx_location = st.nextToken();
			receiver_rf = st.nextToken();
			demodulator = st.nextToken();
			measuredTCA = st.nextToken();
			measuredTCAfrequency = st.nextToken();
			date_time = st.nextToken();

			Stp stp = null;
			stp = new Stp(date, id, resets, uptime, type, sequenceNumber, length, source, receiver,frequency,rx_location,receiver_rf,demodulator,measuredTCA,measuredTCAfrequency,date_time);
			if (records != null && stp != null) {
				records.add(stp);
				return stp;
			}

		} catch (NoSuchElementException e) {
			Log.errorDialog("ERROR: Corrupted record", e.getMessage() + 
					" Could not load record for SAT: " + id + " Reset:" + resets + " Up:" + uptime + " Type:" + type +
					"\nThis record will be ignored, but adding more records may not fix the problem.");
			// we are done and can finish
			return null;
		} catch (ArrayIndexOutOfBoundsException e) {
			// Something nasty happened when we were loading, so skip this record and log an error
			Log.errorDialog("ERROR: Too many fields: Index out of bounds", e.getMessage() + 
					" Could not load line for SAT: " + id + " Reset:" + resets + " Up:" + uptime + " Type:" + type);
			return null;
		} catch (NumberFormatException n) {
			Log.println("ERROR: Invalid number:  " + n.getMessage() + " Could not load frame " + id + " " + resets + " " + uptime + " " + type);
			Log.errorDialog("LOAD ERROR - DEBUG MESSAGE", "ERROR: Invalid number:  " + n.getMessage() + " Could not load frame " + id + " " + resets + " " + uptime + " " + type);
			return null;
		}
		return null;
	}
}
