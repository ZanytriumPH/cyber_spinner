package com.example.cyberspinner;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    private ImageView menuIcon; // 新增菜单图标引用
    private boolean isSpinning = false;
    private Random random = new Random();
    private float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 获取控件引用
        resultText = findViewById(R.id.resultText);
        wheelImage = findViewById(R.id.wheelImage);
        Button generateButton = findViewById(R.id.generateButton);
        menuIcon = findViewById(R.id.menuIcon); // 初始化菜单图标

        // 设置菜单点击事件
        menuIcon.setOnClickListener(v -> showPopupMenu(v));

        // 设置按钮点击事件
        generateButton.setOnClickListener(v -> {
            if (!isSpinning) {
                spinWheel();
            }
        });

        // 处理系统栏Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 显示弹出菜单
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        // 菜单选项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            // 处理选项2
            if (itemId == R.id.menu_item1) {
                // 处理选项1
                return true;
            } else return itemId == R.id.menu_item2;
        });

        popupMenu.show();
    }

    // 转盘旋转方法（保持不变）
    private void spinWheel() {
        isSpinning = true;
        resultText.setText("");

        int circles = random.nextInt(3) + 3;
        int fullCirclesDegrees = circles * 360;
        int randomOffset = random.nextInt(360);
        int totalDegrees = fullCirclesDegrees + randomOffset;

        float newDegree = currentDegree + totalDegrees;

        Animation rotation = new RotateAnimation(
                currentDegree,
                newDegree,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );

        rotation.setDuration(750);
        rotation.setFillAfter(true);

        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                currentDegree = newDegree;
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