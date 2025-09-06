package com.example.cyberspinner;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView resultText;
    private ImageView wheelImage;
    private ImageView menuIcon;
    private Button generateButton;
    private boolean isSpinning = false;
    private Random random = new Random();
    private float currentDegree = 0f;
    private float targetDegree;  // 新增：保存目标角度
    private List<Integer> skinList;
    private SharedPreferences sharedPreferences;
    private int rotationTime = 750;
    private RotateAnimation rotation;  // 保存动画引用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 初始化皮肤列表
        initSkinList();
        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences("WheelSkin", MODE_PRIVATE);

        // 获取控件引用
        resultText = findViewById(R.id.resultText);
        wheelImage = findViewById(R.id.wheelImage);
        generateButton = findViewById(R.id.generateButton);
        menuIcon = findViewById(R.id.menuIcon);

        // 加载保存的皮肤和旋转时间
        loadSelectedSkin();
        loadRotationTime();

        // 设置菜单点击事件
        menuIcon.setOnClickListener(v -> showPopupMenu(v));

        // 设置开始旋转按钮点击事件
        generateButton.setOnClickListener(v -> {
            if (!isSpinning) {
                spinWheel();
            } else {
                // 如果正在旋转，点击则跳过等待
                skipRotation();
            }
        });


        // 处理系统栏Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadRotationTime() {
        rotationTime = sharedPreferences.getInt("rotation_time", 750);
    }

    private void initSkinList() {
        skinList = new ArrayList<>();
        skinList.add(R.drawable.wheel);
        skinList.add(R.drawable.wheel2);
        skinList.add(R.drawable.wheel3);
        skinList.add(R.drawable.wheel4);
        skinList.add(R.drawable.wheel5);
        skinList.add(R.drawable.wheel6);
        skinList.add(R.drawable.wheel7);
        skinList.add(R.drawable.wheel8);
    }

    private void loadSelectedSkin() {
        int selectedSkin = sharedPreferences.getInt("selected_skin", 0);
        if (selectedSkin >= 0 && selectedSkin < skinList.size()) {
            wheelImage.setImageResource(skinList.get(selectedSkin));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSelectedSkin();
        loadRotationTime();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_item1) {
                Intent intent = new Intent(MainActivity.this, SkinSelectorActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_item2) {
                Intent intent = new Intent(MainActivity.this, TimeSettingActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    // 在spinWheel()方法中，确保基于当前角度计算新的目标角度
    private void spinWheel() {
        isSpinning = true;
        resultText.setText("");
        generateButton.setText("跳过旋转");

        // 基于当前角度（可能是上次旋转结束或跳过的角度）计算新的旋转角度
        // 随机旋转3-5圈（每圈360度），再加上0-360度的随机偏移
        int circles = random.nextInt(3) + 3; // 3-5圈
        int fullCirclesDegrees = circles * 360;
        int randomOffset = random.nextInt(360);
        int totalDegrees = fullCirclesDegrees + randomOffset;

        // 新目标角度 = 当前角度 + 总旋转角度（确保连续旋转）
        targetDegree = currentDegree + totalDegrees;

        rotation = new RotateAnimation(
                currentDegree, // 从当前角度开始
                targetDegree,  // 到新目标角度结束
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotation.setDuration(rotationTime);
        rotation.setFillAfter(true);

        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                currentDegree = targetDegree; // 更新当前角度为最终角度
                int result = ((int) (currentDegree % 360) / 30) % 12 + 1;
                resultText.setText(String.valueOf(result));
                isSpinning = false;
                generateButton.setText("开始旋转");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        wheelImage.startAnimation(rotation);
    }

    // 修改MainActivity中的skipRotation()方法
    private void skipRotation() {
        if (rotation != null && isSpinning) {
            // 取消当前动画
            rotation.cancel();

            // 获取当前旋转角度（通过ImageView的旋转状态）
            float currentRotation = wheelImage.getRotation();

            // 创建一个1毫秒的快速动画，从当前角度过渡到目标角度
            RotateAnimation fastRotation = new RotateAnimation(
                    currentRotation,
                    targetDegree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            fastRotation.setDuration(1); // 1毫秒内完成
            fastRotation.setFillAfter(true);

            // 设置动画结束监听，更新状态
            fastRotation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentDegree = targetDegree; // 更新当前角度
                    int result = ((int) (currentDegree % 360) / 30) % 12 + 1;
                    resultText.setText(String.valueOf(result));
                    isSpinning = false;
                    generateButton.setText("开始旋转");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            // 启动快速动画
            wheelImage.startAnimation(fastRotation);
            // 更新动画引用
            rotation = fastRotation;
        }
    }
}