package com.example.project;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    public static final String SELECTED_NAME = "SELECTED_NAME";
    public static final String SELECTED_URL = "SELECTED_URL";
    public static final String SELECTED_DESCRIPTION = "SELECTED_DESCRIPTION";
    public static final String SELECTED_IMAGE_RESOURCE = "SELECTED_IMAGE_RESOURCE";
    public static final String SELECTED_MAP_URL = "SELECTED_MAP_URL";

    private String selectedName = "";
    private String selectedUrl = "";
    private String selectedDescription = "Не выбрано";
    private int selectedImageResource = R.drawable.galery;
    private String selectedMapUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedName = extras.getString(SELECTED_NAME, "");
            selectedUrl = extras.getString(SELECTED_URL, "");
            selectedDescription = extras.getString(SELECTED_DESCRIPTION, "Не выбрано");
            selectedImageResource = extras.getInt(SELECTED_IMAGE_RESOURCE, R.drawable.galery);
            selectedMapUrl = extras.getString(SELECTED_MAP_URL, "");
        }

        // ⭐ ВОТ ЗДЕСЬ ДОЛЖНО БЫТЬ СОЗДАНИЕ ФРАГМЕНТА
        if (savedInstanceState == null) {
            DetailFragment fragment = DetailFragment.newInstance(
                    selectedName, selectedUrl, selectedDescription,
                    selectedImageResource, selectedMapUrl
            );
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailFragment, fragment)
                    .commit();
        }
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        DetailFragment fragment = (DetailFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.detailFragment);
//        if (fragment == null) {
//            // Создаём фрагмент только если его ещё нет
//            DetailFragment newFragment = DetailFragment.newInstance(
//                    selectedName,
//                    selectedUrl,
//                    selectedDescription,
//                    selectedImageResource,
//                    selectedMapUrl
//            );
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.detailFragment, newFragment)
//                    .commit();
//        }
//    }
}