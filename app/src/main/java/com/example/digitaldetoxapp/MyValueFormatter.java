package com.example.digitaldetoxapp;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        // 소수점 없이 정수로 표시
        return String.format("%d", (int) value); // 소수점 없이 정수로 변환
    }
}
