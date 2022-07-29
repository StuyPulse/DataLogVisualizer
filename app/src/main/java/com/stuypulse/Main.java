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

        // 1 meter margin of error on each side
        double MIN_X = -1.0;
        double MAX_X = +17.5;
    
        double MIN_Y = -1.0;
        double MAX_Y = +9.2;

        public static Settings defaultSettings() {
            return new Settings()
                    .setAxes(TITLE, X_AXIS, Y_AXIS)
                    .setXRange(MIN_X, MAX_X)
                    .setYRange(MIN_Y, MAX_Y);
        }

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
            if (xData.get(0).isType(NUMERIC) && yData.get(0).isType(NUMERIC)) {
                // make the two arrays the same size (only neccessary because target and measurement doesn't have same #)
                if (xData.size() != yData.size()) {
                    while (xData.size() > yData.size()) xData.remove(xData.size() - 1);
                    while (yData.size() > xData.size()) yData.remove(yData.size() - 1);
                }

                Vector2D[] points = new Vector2D[xData.size()];

                for (int i = 0; i < yData.size(); i++) {
                    points[i] = new Vector2D(asDouble(xData.get(i)), asDouble(yData.get(i)));
                }

                return new DataSeries(label, points);
            } else {
                System.out.println(xData.size() + ", " + yData.size());
                return new TimeSeries(
                    new Config(label, CAPACITY),
                    new TimeSpan(MIN_X, MAX_X),
                    () -> Double.NaN);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LoggedData data = LoggedData.fromFile("C:\\Users\\Ben\\Documents\\Programming\\robotics\\DataLogVisualizer\\FRC_SWERVE_20220726_031445.wpilog");
        data.setPrefix("NT:/SmartDashboard/");

        String[] modules = {"Top Left", "Top Right", "Bottom Left", "Bottom Right"};

        Plot plot = new Plot();

        plot.addTab(Constants.defaultSettings().setAxes("Robot Pos", "x (m)", "y (m)"))
                .addSeries(
                    Constants.make(
                        "Robot Position",
                        data.getData("Swerve/Pose X"),
                        data.getData("Swerve/Pose Y")))
            
            .addTab(Constants.defaultSettings()
                .setAxes("Angle Error", "time", "angle error (deg)")
                .setXMax(Constants.MAX_X)
                .setYRange(-180, 180))

            .addTab(Constants.defaultSettings()
                .setAxes("Vel Error", "time", "vel error (m/s)")
                .setXMax(Constants.MAX_X)
                .setYRange(-5, 5));
            
        
        for (String module : modules) {
            plot.addSeries(
                    "Angle Error",
                    Constants.make(
                        module,
                        data.getData(module + "/Angle Error")))
                .addSeries(
                    "Vel Error",
                    Constants.make(
                        module,
                        data.getData(module + "/Velocity Error")));
        }

        plot.build(Constants.TITLE, Constants.WIDTH, Constants.HEIGHT);

        for (;;) {
            plot.update();
            Thread.sleep(20);
        }
    }
}
