package com.example.digitaldetoxapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class CircularTimerView extends View {

    private Paint circlePaint;
    private Paint progressPaint;
    private Paint textPaint;

    private int totalTime = 60; // 총 시간 (초 단위)
    private int elapsedTime = 0; // 경과 시간
    private Handler handler = new Handler();
    private Runnable timerRunnable; // 타이머를 멈추기 위한 Runnable
    private boolean isTimerRunning = false; // 타이머 실행 상태 확인

    private OnTimerFinishedListener onTimerFinishedListener;

    public CircularTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 원형 테두리 페인트
        circlePaint = new Paint();
        circlePaint.setColor(Color.GRAY);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(20);
        circlePaint.setAntiAlias(true);

        // 진행률 테두리 페인트
        progressPaint = new Paint();
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);
        progressPaint.setAntiAlias(true);

        // 텍스트 페인트
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 40; // 원 크기

        // 원형 테두리 그리기
        canvas.drawCircle(width / 2, height / 2, radius, circlePaint);

        // 진행률 그리기 (경과된 시간 비율에 맞춰 각도 계산)
        float sweepAngle = 360 * elapsedTime / totalTime;
        canvas.drawArc(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius,
                -90, sweepAngle, false, progressPaint);

        // 중앙에 경과 시간 표시
        int minutes = (elapsedTime % 3600) / 60;
        int seconds = elapsedTime % 60;
        String time = String.format("%02d:%02d", minutes, seconds);
        canvas.drawText(time, width / 2, height / 2 + 20, textPaint);
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        this.elapsedTime = 0;
        invalidate();
        startTimer();
    }

    private void startTimer() {
        if (isTimerRunning) return; // 이미 타이머가 실행 중이면 더 이상 시작하지 않음

        handler.removeCallbacksAndMessages(null); // 기존 타이머 제거

        // 타이머를 갱신하는 Runnable
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (elapsedTime < totalTime) {
                    elapsedTime++;
                    invalidate(); // 다시 그리기
                    handler.postDelayed(this, 1000); // 1초마다 갱신
                } else {
                    // 타이머 종료 리스너 호출
                    if (onTimerFinishedListener != null) {
                        onTimerFinishedListener.onTimerFinished();
                    }
                    isTimerRunning = false; // 타이머가 종료된 상태로 변경
                }
            }
        };

        handler.postDelayed(timerRunnable, 1000); // 1초마다 실행
        isTimerRunning = true; // 타이머 실행 중으로 상태 변경
    }

    // 타이머 종료 리스너 인터페이스
    public interface OnTimerFinishedListener {
        void onTimerFinished();
    }

    // 타이머 종료 리스너 설정 메서드
    public void setOnTimerFinishedListener(OnTimerFinishedListener listener) {
        this.onTimerFinishedListener = listener;
    }

    // 타이머 중지 메서드 (타이머를 멈추지만 시간은 유지)
    public void stopTimer() {
        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable); // 타이머 Runnable 취소
        }
        isTimerRunning = false; // 타이머 상태를 멈춤으로 설정
        invalidate(); // UI 업데이트 (진행률이 멈추므로 화면 갱신)
    }
    // 타이머 초기화 메서드
    public void resetTimer() {
        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable); // 타이머 Runnable 취소
        }
        elapsedTime = 0; // 경과 시간 초기화
        invalidate(); // UI 업데이트
    }

    // 타이머 상태가 실행 중인지 확인하는 메서드
    public boolean isTimerRunning() {
        return isTimerRunning;
    }
}
