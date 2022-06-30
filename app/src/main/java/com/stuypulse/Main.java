package com.stuypulse;

import com.stuypulse.stuylib.streams.filters.IFilter;

public class Main {
    public static void main(String[] args) {
        IFilter f = IFilter.create(x -> x);
    }
}
