package com.example.digitaldetoxapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class StartActivity extends AppCompatActivity {

    private ArrayList<String> selectedChallenges = new ArrayList<>(); // 선택된 챌린지 저장용 리스트
    private Button selectTimeButton;
    private TextView selectedTimeTextView;
    private Button startChallengeButton;
    private int selectedHour = 0;
    private int selectedMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // 챌린지 리스트에 표시할 데이터
        String[] challenges = {"Game", "Youtube", "메시지", "전화", "카메라", "갤러리", "카카오톡"};

        // ListView 설정
        ListView challengeList = findViewById(R.id.challengeList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, challenges);
        challengeList.setAdapter(adapter);
        challengeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // 시간 선택 버튼 설정
        selectTimeButton = findViewById(R.id.selectTimeButton);
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 시간 가져오기
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // 시간 선택 다이얼로그 표시
                TimePickerDialog timePickerDialog = new TimePickerDialog(StartActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        selectedTimeTextView.setText("선택된 시간: " + time);
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        // 챌린지 시작 버튼 설정
        startChallengeButton = findViewById(R.id.startChallengeButton);
        startChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택된 챌린지를 ArrayList에 저장
                selectedChallenges.clear();
                for (int i = 0; i < challengeList.getCount(); i++) {
                    if (challengeList.isItemChecked(i)) {
                        selectedChallenges.add(challenges[i]);
                    }
                }

                if (selectedChallenges.isEmpty()) {
                    Toast.makeText(StartActivity.this, "챌린지를 선택하세요!", Toast.LENGTH_SHORT).show();
                } else if (selectedHour == 0 && selectedMinute == 0) {
                    Toast.makeText(StartActivity.this, "시간을 설정하세요!", Toast.LENGTH_SHORT).show();
                } else {
                    // 선택된 시간을 초로 변환
                    int totalSeconds = (selectedHour * 3600) + (selectedMinute * 60);
                    // ChallengeDetailActivity로 이동
                    Intent intent = new Intent(StartActivity.this, ChallengeDetailActivity.class);
                    intent.putStringArrayListExtra("selectedChallenges", selectedChallenges);
                    intent.putExtra("selectedTimeInSeconds", totalSeconds);
                    startActivity(intent);
                }
            }
        });
    }
}
