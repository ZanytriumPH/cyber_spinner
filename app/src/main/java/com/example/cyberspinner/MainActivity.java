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
    private boolean isSpinning = false;
    private Random random = new Random();
    private float currentDegree = 0f;
    // 皮肤资源列表，与SkinSelectorActivity保持一致
    private List<Integer> skinList;
    private SharedPreferences sharedPreferences;
    // 在类中添加成员变量
    private int rotationTime = 750;


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
        Button generateButton = findViewById(R.id.generateButton);
        menuIcon = findViewById(R.id.menuIcon);

        // 加载保存的皮肤
        loadSelectedSkin();

        // 加载保存的旋转时间
        loadRotationTime();

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

    // 添加加载旋转时间的方法
    private void loadRotationTime() {
        rotationTime = sharedPreferences.getInt("rotation_time", 750);
    }


    // 初始化皮肤列表，与SkinSelectorActivity中的列表保持一致
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

    // 加载选中的皮肤
    private void loadSelectedSkin() {
        int selectedSkin = sharedPreferences.getInt("selected_skin", 0);
        // 检查索引是否有效
        if (selectedSkin >= 0 && selectedSkin < skinList.size()) {
            wheelImage.setImageResource(skinList.get(selectedSkin));
        }
    }

    // 当从皮肤选择页面返回时，重新加载皮肤
    @Override
    protected void onResume() {
        super.onResume();
        loadSelectedSkin();
        loadRotationTime(); // 添加这行
    }

    // 显示弹出菜单
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        // 菜单选项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_item1) {
                // 点击"换转盘皮肤"，跳转到皮肤选择页面
                Intent intent = new Intent(MainActivity.this, SkinSelectorActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_item2) {
                // 点击"调旋转时间"，跳转到时间设置页面
                Intent intent = new Intent(MainActivity.this, TimeSettingActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    // 转盘旋转方法
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

        // 使用保存的旋转时间
        rotation.setDuration(rotationTime);

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