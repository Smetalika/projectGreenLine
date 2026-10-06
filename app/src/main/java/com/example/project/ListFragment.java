package com.example.project;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ListFragment extends Fragment {


    public interface OnLandmarkSelectedListener {
        void onLandmarkSelected(String url, String description);
    }


    private OnLandmarkSelectedListener listener;
    private List<Landmark> landmarks;
    private LandmarkAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLandmarkSelectedListener) {
            listener = (OnLandmarkSelectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnLandmarkSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация WebView
        WebView webView = view.findViewById(R.id.mapWebView);
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true); // Включить JavaScript, если нужно для карты
            webView.setWebViewClient(new WebViewClient()); // Чтобы ссылка открывалась внутри WebView
            webView.loadUrl("file:///android_asset/map.html");
        }


        // Получаем данные из Activity
        if (getActivity() instanceof LandmarkListActivity) {
            landmarks = ((LandmarkListActivity) getActivity()).getLandmarks();
        } else {
            landmarks = new ArrayList<>();
        }

        recyclerView = view.findViewById(R.id.landList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LandmarkAdapter.OnLandmarkClickListener clickListener = (landmark, position) -> {
            if (listener != null) {
                listener.onLandmarkSelected(landmark.getUrl(), landmark.getDescription());
            }
        };

        adapter = new LandmarkAdapter(getContext(), landmarks, clickListener);
        recyclerView.setAdapter(adapter);

        // ⭐ НОВАЯ КНОПКА "МОИ ПЛАНЫ" ⭐
        Button btnPlans = view.findViewById(R.id.btnPlans);
        if (btnPlans != null) {
            btnPlans.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), PlansActivity.class);
                startActivity(intent);
            });
        }



//        Button btnWindow3 = view.findViewById(R.id.btnWindow3);
//        if (btnWindow3 != null) {
//            btnWindow3.setOnClickListener(v -> {
//                Toast.makeText(getContext(), "Окно 3", Toast.LENGTH_SHORT).show();
//            });
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // При возврате на экран обновляем размер шрифта в адаптере
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}