package com.stuypulse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.util.datalog.DataLogReader;
import edu.wpi.first.util.datalog.DataLogRecord;
import edu.wpi.first.util.datalog.DataLogRecord.StartRecordData;

public class LoggedData {

	public static LoggedData fromFile(String filename) {
		try {
			return fromReader(new DataLogReader(filename));
		} catch (IOException e) {
			System.err.println("Failed to open file " + filename + "!");

			return new LoggedData();
		}
	}

	public static LoggedData fromReader(DataLogReader reader) {
		LoggedData out = new LoggedData();

		reader.forEach(record -> {
			out.addRecord(record);
		});

		return out;
	}

	private Map<Integer, String> names;
	private Map<String, List<DataEntry>> data;
	private Map<String, String> types;

	public LoggedData() {
		names = new HashMap<Integer, String>();
		data = new HashMap<String, List<DataEntry>>();
		types = new HashMap<String, String>();
	}

	private void addRecord(DataLogRecord r) {
		if (r.isStart()) {
			StartRecordData start = r.getStartData();

			names.put(start.entry, start.name);
			types.put(start.name, start.type);
		} else if (!r.isControl()) {
			String name = names.get(r.getEntry());
			DataEntry entry = DataEntry.fromRecord(types.get(name), r);
			
			if (data.containsKey(name)) {
				data.get(name).add(entry);
			} else {
				List<DataEntry> l = new ArrayList<DataEntry>();
				
				l.add(entry);
	
				data.put(name, l);
			}
		}
	}

	public List<DataEntry> getData(String name) {
		return data.get(name);
	}

	public String getType(String name) {
		return types.get(name);
	}

}
