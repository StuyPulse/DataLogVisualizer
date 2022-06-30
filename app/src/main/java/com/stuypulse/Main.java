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
        double MAX_X = 150.0;

        double MIN_Y = 0.0;
        double MAX_Y = 3000.0;

        Settings SETTINGS =
                new Settings()
                        .setSize(WIDTH, HEIGHT)
                        .setAxes(TITLE, X_AXIS, Y_AXIS)
                        .setXRange(MIN_X, MAX_X)
                        .setYRange(MIN_Y, MAX_Y);

        public static Series make(String entry, List<DataEntry> data) {
            DataType t = data.get(0).type;

            if (t == DataType.STRING || t == DataType.ARRAY) {
                return new TimeSeries(
                    new Config(entry, CAPACITY),
                    new TimeSpan(MIN_X, MAX_X),
                    () -> Double.NaN);
            } else {
                Vector2D[] points = new Vector2D[data.size()];

                for (int i = 0; i < data.size(); i++) {
                    double seconds = data.get(i).getSeconds();
                    
                    if (t == DataType.BOOLEAN) {
                        points[i] = new Vector2D(seconds, data.get(i).b ? 1 : 0);
                    } else if (t == DataType.INTEGER) {
                        points[i] = new Vector2D(seconds, data.get(i).i);
                    } else {
                        points[i] = new Vector2D(seconds, data.get(i).d);
                    }
                }

                NearestInterpolator interp = new NearestInterpolator(Bias.kLeft, points);

                return new FuncSeries(
                    new Config(entry, CAPACITY),
                    new Domain(MIN_X, MAX_X),
                    interp);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        EntryReader reader = new EntryReader("C:\\Users\\bengo\\Documents\\Programming\\DataLogVisualizer\\FRC_20220421_181140_GALILEO_Q28.wpilog");
        Plot plot = new Plot(Constants.SETTINGS);

        String entry = "NT:/SmartDashboard/Shooter/Target RPM";

        plot.addSeries(Constants.make(entry, reader.getData(entry)));

        while (plot.isRunning()) {
            plot.update();
            Thread.sleep(20);
        }
    }
}
