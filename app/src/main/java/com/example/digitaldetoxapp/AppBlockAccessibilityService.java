package com.example.digitaldetoxapp;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class AppBlockAccessibilityService extends AccessibilityService {

    // 카메라 앱 패키지명
    private static final String CAMERA_PACKAGE = "com.sec.android.app.camera";
    // 갤러리 앱 패키지명
    private static final String PHOTOS_PACKAGE = "com.sec.android.gallery3d";
    // 유튜브 앱 패키지명
    private static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
    // 전화 앱 패키지명
    private static final String DIALER_PACKAGE = "com.samsung.android.dialer";
    // 메시지 앱 패키지명
    private static final String MESSAGE_PACKAGE = "com.samsung.android.messaging";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            Log.d("AccessibilityService", "Current app package: " + packageName);

            // SharedPreferences에서 사용자가 선택한 챌린지 가져오기
            SharedPreferences sharedPreferences = getSharedPreferences("ChallengePrefs", Context.MODE_PRIVATE);
            Set<String> selectedChallenges = sharedPreferences.getStringSet("selectedChallenges", new HashSet<>());

            // 선택된 챌린지가 없다면 앱 차단을 해제합니다.
            if (selectedChallenges.isEmpty()) {
                Log.d("AppBlockService", "No selected challenges. No apps will be blocked.");
                return;
            }

            // 선택된 챌린지 목록에 따라 앱을 차단
            if (selectedChallenges.contains("카메라") && packageName.equals(CAMERA_PACKAGE)) {
                blockApp("카메라");
            } else if (selectedChallenges.contains("갤러리") && packageName.equals(PHOTOS_PACKAGE)) {
                blockApp("갤러리");
            } else if (selectedChallenges.contains("Youtube") && packageName.equals(YOUTUBE_PACKAGE)) {
                blockApp("Youtube");
            } else if (selectedChallenges.contains("전화") && packageName.equals(DIALER_PACKAGE)) {
                blockApp("전화");
            } else if (selectedChallenges.contains("메시지") && packageName.equals(MESSAGE_PACKAGE)) {
                blockApp("메시지");
            }
        }
    }

    private void blockApp(String appName) {
        Log.d("AppBlockService", appName + " 앱이 감지되었습니다!");
        Toast.makeText(this, appName + " 앱이 차단되었습니다!", Toast.LENGTH_SHORT).show();

        // 홈 화면으로 이동
        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    @Override
    public void onInterrupt() {
        // 서비스가 중단될 때 처리할 내용
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // 서비스 연결 시 추가적인 설정이 필요할 경우

        //서비스 시작이 됐는지 로그 찍기
        Log.d("AppBlockService", "AppBlockService 연결완료");
    }

    // 차단된 앱 초기화 메서드 수정
    public void resetBlockedApps(Context context) {
        if (context == null) {
            Log.e("AppBlockService", "Context is null. Cannot reset blocked apps.");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("ChallengePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("selectedChallenges", new HashSet<>()); // 앱 차단 목록을 비웁니다
        editor.apply();
        Log.d("AppBlockService", "차단된 앱 목록이 초기화되었습니다.");
    }
}
