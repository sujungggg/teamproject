
package com.example.digitaldetoxapp;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class UsageStatsPermissionHelper {

    private Context context;

    public UsageStatsPermissionHelper(Context context) {
        this.context = context;
    }

    // 권한이 활성화되어 있는지 확인하는 메서드
    public boolean isUsageStatsPermissionGranted() {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // 권한 설정 페이지로 이동시키는 메서드
    public void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivity(intent);
    }
}
