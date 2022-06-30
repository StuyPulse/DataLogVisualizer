package com.stuypulse;

import java.io.IOException;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DataLogReader;
import edu.wpi.first.util.datalog.DataLogRecord.MetadataRecordData;
import edu.wpi.first.util.datalog.DataLogRecord.StartRecordData;

public class Test {
    public static void main(String[] args) throws IOException {
        DataLogReader reader = new DataLogReader("FRC_20220421_181140_GALILEO_Q28.wpilog");

        reader.forEach(record -> {
            if (record.isStart()) {
                System.out.println("Reading START data");
                
                StartRecordData start = record.getStartData();
                System.out.println("entry - " + start.entry);
                System.out.println("metadata - " + start.metadata);
                System.out.println("name - " + start.name);
                System.out.println("type - " + start.type);

                System.out.println();
            } else if (record.isFinish()) {
                System.out.println("Reading FINISH data");
                
                int finish = record.getFinishEntry();
                System.out.println("finish record entry ID - " + finish);

                System.out.println();
            } else if (record.isControl()) {
                System.out.println("Reading CONTROL data");

                StartRecordData control = record.getStartData();
                System.out.println("entry - " + control.entry);
                System.out.println("metadata - " + control.metadata);
                System.out.println("name - " + control.name);
                System.out.println("type - " + control.type);

                System.out.println();
            } else if (record.isSetMetadata()) {
                System.out.println("Reading META data");

                MetadataRecordData meta = record.getSetMetadataData();
                System.out.println("entry - " + meta.entry);
                System.out.println("metadata - " + meta.metadata);

                System.out.println();
            } else {
                System.out.println("Reading data");

                System.out.println("entry - " + record.getEntry());
                if (record.getEntry() == 10) {
                    System.out.println(record.getDouble());
                }

                System.out.println();
            }
        });
    }
}
