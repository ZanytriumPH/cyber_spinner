package com.example.cyberspinner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class SkinSelectorActivity extends AppCompatActivity {

    private GridView skinGrid;
    private List<Integer> skinList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_selector);

        sharedPreferences = getSharedPreferences("WheelSkin", MODE_PRIVATE);
        skinGrid = findViewById(R.id.skin_grid);
        initSkinList();

        SkinAdapter adapter = new SkinAdapter(skinList);
        skinGrid.setAdapter(adapter);

        skinGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 保存选中的皮肤索引
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("selected_skin", position);
                editor.apply();

                // 返回主页面
                finish();
            }
        });
    }

    private void initSkinList() {
        skinList = new ArrayList<>();
        // 添加8个皮肤资源
        skinList.add(R.drawable.wheel);
        skinList.add(R.drawable.wheel2);
        skinList.add(R.drawable.wheel3);
        skinList.add(R.drawable.wheel4);
        skinList.add(R.drawable.wheel5);
        skinList.add(R.drawable.wheel6);
        skinList.add(R.drawable.wheel7);
        skinList.add(R.drawable.wheel8);
    }

    private class SkinAdapter extends ArrayAdapter<Integer> {

        public SkinAdapter(List<Integer> skins) {
            super(SkinSelectorActivity.this, R.layout.skin_item, skins);
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = (ImageView) getLayoutInflater().inflate(R.layout.skin_item, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(getItem(position));
            return imageView;
        }
    }
}