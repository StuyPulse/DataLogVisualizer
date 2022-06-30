package com.stuypulse;

import edu.wpi.first.util.datalog.DataLogRecord;

public class DataEntry {
	public enum DataType {
		STRING,
		DOUBLE,
		INTEGER;

		public static DataType fromString(String type) {
			switch (type) {
				case "string": return STRING;
				case "int64":  return INTEGER;
				default:       return DOUBLE;
			}
		}
	};

	public final long timestamp;
	
	public final String str;
	public final double d;
	public final long i;

	public final DataType type;

	public DataEntry(String typeString, DataLogRecord record) {
		timestamp = record.getTimestamp();
		type = DataType.fromString(typeString);

		if (type == DataType.STRING) {
			str = record.getString();
		} else {
			str = "";
		}
		
		if (type == DataType.DOUBLE) {
			d = record.getDouble();
		} else {
			d = 0.0;
		}
		
		if (type == DataType.INTEGER) {
			i = record.getInteger();
		} else {
			i = 0;
		}
	}

	public double getSeconds() {
		return ((double)timestamp) / 1000000.0;
	}
	public double getMilliseconds() {
		return ((double)timestamp) / 1000.0;
	}
}
