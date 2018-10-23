package data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Stp implements Comparable<Stp> {
	public static final String NONE = "NONE";
	public static final DateFormat stpDateFormat = new SimpleDateFormat(
			"E, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
	
	public int foxId = 0;
	public int resets = 0;
	public long uptime = 0;
	public int type = 0;
	public String receiver = NONE; // unique name (usually callsign) chosen by
									// the user. May vary over life of program
									// usage, so stored
	private String frequency = NONE; // frequency when this frame received
	private String source; // The frame source subsystem
	private int length; // The frame length in bytes
	public String rx_location = NONE; // the lat, long and altitude
	public String receiver_rf = NONE; // human description of the receiver
	public String demodulator; // will contain Config.VERSION
	private Date stpDate;
	public long sequenceNumber = 0;

	private String measuredTCA = NONE; // time of TCA
	private String measuredTCAfrequency = NONE; // frequency if this frame was
												// just after TCA
	
	public double lat;
	public double lon;
	
	Stp(String date, int id, int resets, long uptime, int type, int sequenceNumber, int length, String source, String receiver,String frequency,String rx_location,
			String receiver_rf,String demodulator,String measuredTCA,String measuredTCAfrequency,String date_time) {
		try {
			stpDate = stpDateFormat.parse(date);
		} catch (ParseException e) {
			stpDate = null; // we have no date, so likely ignore in any date dependant query
		}
		foxId = id;
		this.resets = resets;
		this.uptime = uptime;
		this.type = type;
		this.sequenceNumber = sequenceNumber;
		this.length = length;
		this.source = source;
		this.receiver = receiver;
		this.frequency = frequency;
		this.rx_location = rx_location;
		this.receiver_rf = receiver_rf;
		this.demodulator = demodulator;
		this.measuredTCA = measuredTCA;
		this.measuredTCAfrequency = measuredTCAfrequency;
		
		String latLon[] = rx_location.split(" ");
		if (latLon.length == 5) {
		lat = Double.parseDouble(latLon[1]);
		if (latLon[0].equals("S"))
			lat = lat * -1;
		lon = Double.parseDouble(latLon[3]);
		if (latLon[2].equals("W"))
			lon = lon * -1;
		}
	}

	public String toString() {
		String s = receiver + " " + foxId + " " + rx_location;
		return s;
	}
	
	@Override
	public int compareTo(Stp p) {
		if (resets == p.resets && uptime == p.uptime && type == p.type && receiver == p.receiver) 
			return 0;
		else if (resets < p.resets)
			return -1;
		else if (resets > p.resets)
			return +1;
		else if (resets == p.resets && uptime == p.uptime) {
			if (type < p.type)
				return -1;
			if (type > p.type)
				return +1;
		} else if (resets == p.resets && uptime == p.uptime && type == p.type ) {
			return (receiver.compareTo(p.receiver));
		} else if (resets == p.resets) {	
			if (uptime < p.uptime)
				return -1;
			if (uptime > p.uptime)
				return +1;
		} 
		return +1;
	}
}
