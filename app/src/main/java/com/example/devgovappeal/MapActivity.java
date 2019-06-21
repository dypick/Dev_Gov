package com.example.devgovappeal;

import android.os.Bundle;
import android.app.Activity;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import com.yandex.mapkit.mapview.MapView;

public class MapActivity extends Activity {
    private final Point TARGET_LOCATION = new Point(52.971381, 36.063083); // Долгота и широта, соответствующие зданию администрации области

    private MapView mapView; // экземпляр, необходимый для привязки к элементу MapView на layout приложения

    @Override
    // действие, выполняемое после генерации layout на экране девайса.
    // Хавает ключ, выполняет привязку к элементу mapview на экране
    // Плавно перемещается на заданные коор-ты
    protected void onCreate(Bundle savedInstanceState) {
        // строка с Ключом Разработчка Yandex. ключ доступен по адресу : https://developer.tech.yandex.ru/ website
        String MAPKIT_API_KEY = "44655ba5-c7a0-4cf4-82d4-2116fd9485e0";
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map_layout);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);

        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}
