package com.example.cyberspinner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TimeSettingActivity extends AppCompatActivity {

    private EditText timeInput;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);

        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences("WheelSkin", MODE_PRIVATE);
        timeInput = findViewById(R.id.timeInput);
        Button confirmButton = findViewById(R.id.confirmButton);

        // 显示当前保存的时间（可选）
        int currentTime = sharedPreferences.getInt("rotation_time", 750);
        timeInput.setText(String.valueOf(currentTime));

        // 确认按钮点击事件
        confirmButton.setOnClickListener(v -> saveRotationTime());
    }

    private void saveRotationTime() {
        String input = timeInput.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "请输入时间", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int time = Integer.parseInt(input);

            // 保存到SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("rotation_time", time);
            editor.apply();

            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
            finish(); // 返回主页面
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }
}