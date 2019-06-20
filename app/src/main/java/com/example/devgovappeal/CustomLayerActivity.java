package com.example.devgovappeal;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.TileId;
import com.yandex.mapkit.Version;
import com.yandex.mapkit.layers.Layer;
import com.yandex.mapkit.layers.LayerOptions;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.tiles.UrlProvider;
import com.yandex.mapkit.resource_url_provider.DefaultUrlProvider;
import com.yandex.mapkit.geometry.geo.Projection;
import com.yandex.mapkit.geometry.geo.Projections;
import com.yandex.mapkit.mapview.MapView;

public class CustomLayerActivity extends Activity {

    private final String MAPKIT_API_KEY = "your_api_key";

    private UrlProvider urlProvider;
    private DefaultUrlProvider resourceUrlProvider;
    private Projection projection;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.custom_layer);
        super.onCreate(savedInstanceState);

        urlProvider = new UrlProvider() {
            @Override
            public String formatUrl(@NonNull TileId tileId, @NonNull Version version) {
//                return "https://maps-ios-pods-public.s3.yandex.net/mapkit_logo.pngs";

                return null;
            }
        };
        resourceUrlProvider = new DefaultUrlProvider();
        projection = Projections.createWgs84Mercator();

        mapView = findViewById(R.id.mapview);
        mapView.getMap().setMapType(MapType.NONE);
        Layer l = mapView.getMap().addLayer(
                "mapkit_logo",
                "image/png",
                new LayerOptions(),
                urlProvider,
                resourceUrlProvider,
                projection);
        l.invalidate("0.0.0");
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }



}
