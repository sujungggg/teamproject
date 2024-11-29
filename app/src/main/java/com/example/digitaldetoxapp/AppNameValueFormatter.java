package com.example.digitaldetoxapp;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class AppNameValueFormatter extends ValueFormatter {
    private final List<String> appNames;

    public AppNameValueFormatter(List<String> appNames) {
        this.appNames = appNames;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value - 1; // 인덱스 조정 (BarEntry 인덱스가 1부터 시작하므로)
        if (index >= 0 && index < appNames.size()) {
            return appNames.get(index);
        }
        return ""; // 범위 밖이면 빈 문자열 반환
    }
}
