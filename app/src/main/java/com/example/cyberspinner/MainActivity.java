package com.example.cyberspinner;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private TextView resultText;
    private ImageView wheelImage;
    private boolean isSpinning = false;
    private Random random = new Random();
    private float currentDegree = 0f; // 新增：记录当前转盘的总旋转角度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 获取控件引用
        resultText = findViewById(R.id.resultText);
        wheelImage = findViewById(R.id.wheelImage);
        Button generateButton = findViewById(R.id.generateButton);

        // 设置按钮点击事件
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSpinning) {
                    spinWheel();
                }
            }
        });

        // 处理系统栏Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void spinWheel() {
        isSpinning = true;
        resultText.setText("");

        // 生成3-5圈的随机圈数（3圈=1080度，5圈=1800度）
        int circles = random.nextInt(3) + 3;
        int fullCirclesDegrees = circles * 360;
        int randomOffset = random.nextInt(360);
        int totalDegrees = fullCirclesDegrees + randomOffset;

        // 计算新的结束角度（在当前角度基础上累加）
        float newDegree = currentDegree + totalDegrees;

        // 旋转动画设置：从当前角度旋转到新角度
        Animation rotation = new RotateAnimation(
                currentDegree, // 从当前角度开始
                newDegree,     // 旋转到新角度
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );

        rotation.setDuration(750); // 旋转时间
        rotation.setFillAfter(true);

        // 动画结束后更新当前角度并计算结果
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                currentDegree = newDegree; // 更新当前角度为新角度
                // 根据最终角度计算结果（每36度对应一个数字，1-10循环）
                int result = ((int)(currentDegree % 360) / 30) % 12 + 1;
                resultText.setText(String.valueOf(result));
                isSpinning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        wheelImage.startAnimation(rotation);
    }
}