package com.stuypulse;

import java.util.ArrayList;
import java.util.List;

import com.stuypulse.stuylib.math.Vector2D;
import com.stuypulse.stuylib.util.plot.Series;

public class DataSeries extends Series {

	private List<Double> xValues;
	private List<Double> yValues;

	public DataSeries(String label, Vector2D... points) {
		super(new Config(label, points.length), false);

		xValues = new ArrayList<Double>();
		yValues = new ArrayList<Double>();

		for (Vector2D point : points) {
			xValues.add(point.x);
			yValues.add(point.y);
		}
	}

	@Override
	public int size() {
		return yValues.size();
	}

	@Override
	protected List<Double> getSafeXValues() {
		return xValues;
	}

	@Override
	protected List<Double> getSafeYValues() {
		return yValues;
	}

	@Override
	protected void pop() {}

	@Override
	protected void poll() {}
	
}
