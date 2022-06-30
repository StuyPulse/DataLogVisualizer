package com.stuypulse;

import edu.wpi.first.util.datalog.DataLogRecord;

public class DataEntry {
	public enum DataType {
		STRING,
		DOUBLE,
		INTEGER,
		BOOLEAN,
		RAW,
		ARR_STRING,
		ARR_DOUBLE,
		ARR_INTEGER,
		ARR_BOOLEAN;

		public static DataType fromString(String type) {
			switch (type) {
				case "string":    return STRING;
				case "int64":     return INTEGER;
				case "boolean":   return BOOLEAN;
				case "double":    return DOUBLE;
				case "string[]":  return ARR_STRING;
				case "int64[]":   return ARR_INTEGER;
				case "boolean[]": return ARR_BOOLEAN;
				case "double[]":  return ARR_DOUBLE;
				default:          return RAW;
			}
		}
	};

	public double seconds;
	
	public DataType type;

	public byte[] raw;
	public String string;
	public double d;
	public long i;
	public boolean bool;

	public String[] stringArray;
	public double[] doubleArray;
	public long[] intArray;
	public boolean[] boolArray;

	public static DataEntry fromRecord(String type, DataLogRecord record) {
		DataType t = DataType.fromString(type);
		double seconds = ((double)record.getTimestamp()) / 1000000.0;

		switch (t) {
			case STRING:      return new DataEntry(seconds, record.getString());
			case DOUBLE:      return new DataEntry(seconds, record.getDouble());
			case INTEGER:     return new DataEntry(seconds, record.getInteger());
			case BOOLEAN:     return new DataEntry(seconds, record.getBoolean());
			case RAW:         return new DataEntry(seconds, record.getRaw());
			case ARR_STRING:  return new DataEntry(seconds, record.getStringArray());
			case ARR_DOUBLE:  return new DataEntry(seconds, record.getDoubleArray());
			case ARR_INTEGER: return new DataEntry(seconds, record.getIntegerArray());
			case ARR_BOOLEAN: return new DataEntry(seconds, record.getBooleanArray());
			default: throw new Error("Invalid string type");
		}
	}


	public DataEntry(double seconds) {
		this.seconds = seconds;

		type = null;

		string = "";
		d = 0.0;
		i = 0;
		bool = false;

		raw = null;
		stringArray = null;
		doubleArray = null;
		intArray = null;
		boolArray = null;
	}

	public DataEntry(double seconds, String val) {
		this(seconds);

		type = DataType.STRING;
		string = val;
	}

	public DataEntry(double seconds, double val) {
		this(seconds);

		type = DataType.DOUBLE;
		d = val;
	}

	public DataEntry(double seconds, long val) {
		this(seconds);

		type = DataType.INTEGER;
		i = val;
	}

	public DataEntry(double seconds, boolean val) {
		this(seconds);

		type = DataType.BOOLEAN;
		bool = val;
	}

	public DataEntry(double seconds, byte[] val) {
		this(seconds);

		type = DataType.RAW;
		raw = val;
	}

	public DataEntry(double seconds, String[] val) {
		this(seconds);

		type = DataType.ARR_STRING;
		stringArray = val;
	}

	public DataEntry(double seconds, double[] val) {
		this(seconds);

		type = DataType.ARR_DOUBLE;
		doubleArray = val;
	}

	public DataEntry(double seconds, long[] val) {
		this(seconds);

		type = DataType.ARR_INTEGER;
		intArray = val;
	}

	public DataEntry(double seconds, boolean[] val) {
		this(seconds);

		type = DataType.ARR_BOOLEAN;
		boolArray = val;
	}


	public boolean isType(DataType type) {
		return this.type == type;
	}

	public boolean isString() {
		return isType(DataType.STRING);
	}

	public boolean isDouble() {
		return isType(DataType.DOUBLE);
	}

	public boolean isInteger() {
		return isType(DataType.INTEGER);
	}

	public boolean isBoolean() {
		return isType(DataType.BOOLEAN);
	}

	public boolean isRaw() {
		return isType(DataType.RAW);
	}

	public boolean isStringArray() {
		return isType(DataType.ARR_STRING);
	}

	public boolean isDoubleArray() {
		return isType(DataType.ARR_DOUBLE);
	}

	public boolean isIntegerArray() {
		return isType(DataType.ARR_INTEGER);
	}

	public boolean isBooleanArray() {
		return isType(DataType.ARR_BOOLEAN);
	}

	public boolean isNumeric() {
		return isType(DataType.DOUBLE) || isType(DataType.INTEGER) || isType(DataType.BOOLEAN);
	}


	public double asDouble() {
		if (isDouble()) return d;
		if (isInteger()) return i;
		if (isBoolean()) return bool ? 1 : 0;

		return 0.0;
	}

}
