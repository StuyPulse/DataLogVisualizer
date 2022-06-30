package com.stuypulse;

import java.util.List;

import com.stuypulse.DataEntry.DataType;
import com.stuypulse.stuylib.math.Vector2D;
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
        double MAX_X = 400.0;

        double MIN_Y = 0.0;
        double MAX_Y = 3500.0;

        Settings SETTINGS =
                new Settings()
                        .setSize(WIDTH, HEIGHT)
                        .setAxes(TITLE, X_AXIS, Y_AXIS)
                        .setXRange(MIN_X, MAX_X)
                        .setYRange(MIN_Y, MAX_Y);

        public static Series make(String key, List<DataEntry> data) {
            DataType t = data.get(0).type;

            double start = Double.POSITIVE_INFINITY;
            double end = Double.NEGATIVE_INFINITY;

            if (t == DataType.BOOLEAN || t == DataType.INTEGER || t == DataType.DOUBLE) {
                Vector2D[] points = new Vector2D[data.size()];

                for (int i = 0; i < data.size(); i++) {
                    DataEntry entry = data.get(i);
                    
                    if (entry.isBoolean()) {
                        points[i] = new Vector2D(entry.seconds, entry.bool ? 1 : 0);
                    } else if (entry.isInteger()) {
                        points[i] = new Vector2D(entry.seconds, entry.i);
                    } else {
                        points[i] = new Vector2D(entry.seconds, entry.d);
                    }

                    if (start > entry.seconds) {
                        start = entry.seconds;
                    }

                    if (end < entry.seconds) {
                        end = entry.seconds;
                    }
                }

                NearestInterpolator interp = new NearestInterpolator(Bias.kLeft, points);

                return new FuncSeries(
                    new Config(key, CAPACITY),
                    new Domain(start, end),
                    interp);
            } else {
                return new TimeSeries(
                    new Config(key, CAPACITY),
                    new TimeSpan(MIN_X, MAX_X),
                    () -> Double.NaN);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LoggedData reader = LoggedData.fromFile("C:\\Users\\bengo\\Documents\\Programming\\DataLogVisualizer\\FRC_20220421_181140_GALILEO_Q28.wpilog");
        Plot plot = new Plot(Constants.SETTINGS);

        String key = "NT:/SmartDashboard/Shooter/Target RPM";

        plot.addSeries(Constants.make(key, reader.getData(key)));

        while (plot.isRunning()) {
            plot.update();
            Thread.sleep(20);
        }
    }
}
