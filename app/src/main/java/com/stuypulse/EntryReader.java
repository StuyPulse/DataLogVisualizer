package com.stuypulse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.util.datalog.DataLogReader;
import edu.wpi.first.util.datalog.DataLogRecord;
import edu.wpi.first.util.datalog.DataLogRecord.StartRecordData;

public class EntryReader {

	private final DataLogReader reader;

	private Map<Integer, String> names;
	private Map<String, List<DataEntry>> data;
	private Map<String, String> types;

	public EntryReader(String filename) throws IOException {
		reader = new DataLogReader(filename);

		names = new HashMap<Integer, String>();
		data = new HashMap<String, List<DataEntry>>();
		types = new HashMap<String, String>();
		
		reader.forEach(x -> {
			if (x.isStart()) {
				StartRecordData start = x.getStartData();

				names.put(start.entry, start.name);
			} else if (!x.isControl()) {
				addRecord(x);
			}
		});
	}

	private void addRecord(DataLogRecord r) {
		String name = names.get(r.getEntry());

		DataEntry entry = new DataEntry(types.get(name), r);
		
		try {
			data.get(name).add(entry);
		} catch (NullPointerException e) {
			List<DataEntry> l = new ArrayList<DataEntry>();
			
			l.add(entry);

			data.put(name, l);
		}
	}

	public List<DataEntry> getData(String name) {
		return data.get(name);
	}

	public String getType(String name) {
		return types.get(name);
	}

}
