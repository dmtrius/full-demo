package com.example.demo.apps.payback.ml;

import lombok.Data;

@Data
public class Pointee {
    private int row;
    private int col;
    private double predictionScore;

    public Pointee(int row, int col) {
        this.row = row;
        this.col = col;
        this.predictionScore = 1.0; // Default initial score
    }
}
