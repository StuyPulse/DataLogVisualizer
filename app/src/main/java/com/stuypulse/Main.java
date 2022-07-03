package com.stuypulse;

import java.util.ArrayList;
import java.util.List;

import com.stuypulse.DataEntry.DataType;
import com.stuypulse.stuylib.math.Vector2D;
import com.stuypulse.stuylib.math.interpolation.Interpolator;
import com.stuypulse.stuylib.math.interpolation.NearestInterpolator;
import com.stuypulse.stuylib.math.interpolation.NearestInterpolator.Bias;
import com.stuypulse.stuylib.util.plot.FuncSeries;
import com.stuypulse.stuylib.util.plot.Plot;
import com.stuypulse.stuylib.util.plot.Series;
import com.stuypulse.stuylib.util.plot.Settings;
import com.stuypulse.stuylib.util.plot.TimeSeries;
import com.stuypulse.stuylib.util.plot.FuncSeries.Domain;
import com.stuypulse.stuylib.util.plot.Series.Config;
import com.stuypulse.stuylib.util.plot.TimeSeries.TimeSpan;

public class Main {
    public interface Constants {
        int CAPACITY = 200;

        String TITLE = "StuyLib Plotting Library";
        String X_AXIS = "match time";
        String Y_AXIS = "y-axis";

        int WIDTH = 800;
        int HEIGHT = 600;

        double MIN_X = 0.0;
        double MAX_X = 16.5;

        double MIN_Y = 0.0;
        double MAX_Y = 8.2;

        Settings SETTINGS =
                new Settings()
                        .setAxes(TITLE, X_AXIS, Y_AXIS)
                        .setXRange(MIN_X, MAX_X)
                        .setYRange(MIN_Y, MAX_Y);

        DataType[] NUMERIC = { DataType.DOUBLE, DataType.INTEGER, DataType.BOOLEAN };

        public static double asDouble(DataEntry entry) {
            if (entry.isDouble()) {
                return entry.getDouble();
            }
    
            if (entry.isInteger()) {
                return entry.getInteger();
            }
    
            if (entry.isBoolean()) {
                return entry.getBoolean() ? 1 : 0;
            }
    
            return 0.0;
        }

        public static Series make(String label, List<DataEntry> data) {
            if (data.get(0).isType(NUMERIC)) {
                List<Vector2D> points = new ArrayList<Vector2D>();
    
                double prevEntry = Double.NaN;

                for (int i = 0; i < data.size(); i++) {
                    DataEntry entry = data.get(i);
                    double val = asDouble(entry);
    
                    if (val != prevEntry) {
                        points.add(new Vector2D(entry.seconds, val));  
                        prevEntry = val;
                    }
                }
    
                Interpolator interp = new NearestInterpolator(Bias.kLeft, points.toArray(new Vector2D[0]));

                return new FuncSeries(
                    new Config(label, CAPACITY),
                    new Domain(MIN_X, MAX_X),
                    interp);
            } else {
                return new TimeSeries(
                    new Config(label, CAPACITY),
                    new TimeSpan(MIN_X, MAX_X),
                    () -> Double.NaN);
            }
        }

        public static Series make(String label, List<DataEntry> xData, List<DataEntry> yData) {
            if (xData.get(0).isType(NUMERIC) && yData.get(0).isType(NUMERIC) && xData.size() == yData.size()) {
                Vector2D[] points = new Vector2D[xData.size()];

                for (int i = 0; i < yData.size(); i++) {
                    points[i] = new Vector2D(asDouble(xData.get(i)), asDouble(yData.get(i)));
                }

                return new DataSeries(label, points);
            } else {
                return new TimeSeries(
                    new Config(label, CAPACITY),
                    new TimeSpan(MIN_X, MAX_X),
                    () -> Double.NaN);
            }
        }
    }

    private static void printData(LoggedData data) {
        data.getNames().forEach(name -> {
            System.out.println(name);
            // data.getData(name).forEach(record -> {
            //     System.out.println(record.seconds + ",\"" + name + "\"," + record);
            // });
        });
    }

    public static void main(String[] args) throws InterruptedException {
        LoggedData data = LoggedData.fromFile("C:\\Users\\bengo\\Documents\\Programming\\DataLogVisualizer\\FRC_20220701_010412.wpilog");
        
        Plot plot = new Plot();

        plot.addPlot(Constants.SETTINGS.setTitle("Robot Pos"))
            .addSeries(
                Constants.make(
                    "Robot Position",
                    data.getData("NT:/SmartDashboard/Debug/Drivetrain/Odometer X Position (m)"),
                    data.getData("NT:/SmartDashboard/Debug/Drivetrain/Odometer Y Position (m)")))

            .addPlot(Constants.SETTINGS
                .setTitle("Encoder 1")
                .setYRange(-5, 30))
            .addSeries(
                Constants.make(
                    "Distance",
                    data.getData("NT:/LiveWindow/Ungrouped/Encoder[1]/Distance")))
            .addSeries(
                Constants.make(
                    "Speed",
                    data.getData("NT:/LiveWindow/Ungrouped/Encoder[1]/Speed")))
            .build(Constants.TITLE, Constants.WIDTH, Constants.HEIGHT);

        for (;;) {
            plot.update();
            Thread.sleep(20);
        }
    }
}
