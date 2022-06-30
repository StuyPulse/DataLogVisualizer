package com.stuypulse;

import edu.wpi.first.util.datalog.DataLogRecord;

public class DataEntry {
	public enum DataType {
		STRING,
		DOUBLE,
		INTEGER,
		BOOLEAN;

		public static DataType fromString(String type) {
			switch (type) {
				case "string":  return STRING;
				case "int64":   return INTEGER;
				case "boolean": return BOOLEAN;
				default:        return DOUBLE;
			}
		}
	};

	public final double milliseconds;
	
	public final String str;
	public final double d;
	public final long i;
	public final boolean b;

	public final DataType type;

	public DataEntry(String typeString, DataLogRecord record) {
		milliseconds = ((double)record.getTimestamp()) / 1000.0;
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
		if (type == DataType.BOOLEAN) {
			b = record.getBoolean();
		} else {
			b = false;
		}
	}

	public double getSeconds() {
		return milliseconds / 1000.0;
	}
}
