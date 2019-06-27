package com.example.devgovappeal;

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RawTile;
import com.yandex.mapkit.TileId;
import com.yandex.mapkit.Version;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.geo.Projection;
import com.yandex.mapkit.geometry.geo.Projections;
import com.yandex.mapkit.layers.Layer;
import com.yandex.mapkit.layers.LayerOptions;
import com.yandex.mapkit.map.CameraPosition;

import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.resource_url_provider.ResourceUrlProvider;
import com.yandex.mapkit.tiles.TileProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class MapActivity extends Activity {
    private final Point TARGET_LOCATION = new Point(52.971381, 36.063083); // Долгота и широта, соответствующие зданию администрации области

    private MapView mapView; // экземпляр, необходимый для привязки к элементу MapView на layout приложения
    private final Logger logger = Logger.getLogger("devgovappeal.geojson");


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
        createGeoJsonLayer();
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

    private void createGeoJsonLayer() {

        final ResourceUrlProvider urlProvider = new ResourceUrlProvider() {
            @NonNull
            @Override
            public String formatUrl(@NonNull String s) {
                return String.format("https://raw.githubusercontent.com/dypick/Dev_Gov/7da4207176f9df38e60f1121552c93c1eb93aefe/app/src/main/%s", s);
            }
        };

        TileProvider tileProvider;
        try {
            tileProvider = createTileProvider();
        }
        catch (IOException ex) {
            logger.severe("Tile provider not created: cancel creation of geo json layer");
            return;
        }

        final Projection projection = Projections.createWgs84Mercator();

        Layer layer = mapView.getMap().addLayer(
                "geo_json_layer",
                "application/geo-json",
                new LayerOptions(),
                tileProvider,
                urlProvider,
                projection);

        layer.invalidate("0.0.0");
    }

    private TileProvider createTileProvider() throws IOException
    {
        final StringBuilder builder = new StringBuilder();
        final int resourceIdentifier =
                getResources().getIdentifier("geo_json_example","raw", getPackageName());
        InputStream is = getResources().openRawResource(resourceIdentifier);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception ex) {
            reader.close();
            logger.severe("Cannot read GeoJSON file");
            throw ex;
        }

        final String rawJson = builder.toString();
        return new TileProvider() {
            @NonNull
            @Override
            public RawTile load(@NonNull TileId tileId, @NonNull Version version, @NonNull String etag) {
                return new RawTile(version, etag, RawTile.State.OK, rawJson.getBytes());
            }
        };
    }
}


