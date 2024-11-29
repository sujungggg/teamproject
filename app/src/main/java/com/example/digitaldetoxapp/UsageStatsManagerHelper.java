package com.example.digitaldetoxapp;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.util.Calendar;
import java.util.List;

public class UsageStatsManagerHelper {

    public static long getAppUsageTime(Context context, String packageName) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        if (usageStatsManager == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis(); // 현재 시간
        calendar.add(Calendar.DAY_OF_YEAR, -1); // 하루 전부터
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        long totalTime = 0;
        if (stats != null) {
            for (UsageStats usageStats : stats) {
                if (usageStats.getPackageName().equals(packageName)) {
                    totalTime += usageStats.getTotalTimeInForeground(); // 포그라운드에서의 사용 시간
                }
            }
        }
        return totalTime; // 밀리초 단위로 반환
    }
}