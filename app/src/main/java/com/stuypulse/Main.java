package com.stuypulse;

import java.util.List;

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
        double MAX_X = 400.0;

        double MIN_Y = 0.0;
        double MAX_Y = 3500.0;

        Settings SETTINGS =
                new Settings()
                        .setSize(WIDTH, HEIGHT)
                        .setAxes(TITLE, X_AXIS, Y_AXIS)
                        .setXRange(MIN_X, MAX_X)
                        .setYRange(MIN_Y, MAX_Y);
        
        public static Interpolator interpolate(List<DataEntry> data) {
            Vector2D[] points = new Vector2D[data.size()];

            for (int i = 0; i < data.size(); i++) {
                DataEntry entry = data.get(i);

                points[i] = new Vector2D(entry.seconds, entry.asDouble());
            }

            return new NearestInterpolator(Bias.kLeft, points);
        }

        public static Series make(String label, List<DataEntry> data) {
            if (data.get(0).isNumeric()) {
                return new FuncSeries(
                    new Config(label, CAPACITY),
                    new Domain(MIN_X, MAX_X),
                    interpolate(data));
            } else {
                return new TimeSeries(
                    new Config(label, CAPACITY),
                    new TimeSpan(MIN_X, MAX_X),
                    () -> Double.NaN);
            }
        }

        public static Series make(String label, List<DataEntry> xData, List<DataEntry> yData) {
            if (xData.get(0).isNumeric() && yData.get(0).isNumeric() && xData.size() == yData.size()) {
                Vector2D[] points = new Vector2D[xData.size()];

                for (int i = 0; i < yData.size(); i++) {
                    points[i] = new Vector2D(xData.get(i).asDouble(), yData.get(i).asDouble());
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
