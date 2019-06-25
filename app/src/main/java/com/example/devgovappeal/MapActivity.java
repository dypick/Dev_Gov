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
    private final Point TARGET_LOCATION = new Point(52.971381, 36.063083);

    private Logger LOGGER = Logger.getLogger("devgovappeal");
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String MAPKIT_API_KEY = "44655ba5-c7a0-4cf4-82d4-2116fd9485e0";
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map_layout);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);
        createGeoJsonLayer();
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);


    }

    private void createGeoJsonLayer() {

        final ResourceUrlProvider urlProvider = new ResourceUrlProvider() {
            @NonNull
            @Override
            public String formatUrl(@NonNull String s) {
                return String.format("https://raw.githubusercontent.com/dypick/Dev_Gov/master/src/main/%s", s);

            }
        };


        TileProvider tileProvider;
        try {
            tileProvider = createTileProvider();
        } catch (IOException ex) {
            LOGGER.severe("Tile provider failed to create: cancel creation of geojson layer");
            return;

        }
        final Projection projection = Projections.createWgs84Mercator();

        Layer layer = mapView.getMap().addLayer(
                "geo_json_layer ",
                "application/geo-json",
                new LayerOptions(),
                tileProvider,
                urlProvider,
                projection);

        layer.invalidate("0.0.0");

    }

    private TileProvider createTileProvider() throws IOException {
        final StringBuilder builder = new StringBuilder();
        final int resourceIdentifier =
              MapActivity.this.getResources().getIdentifier("geojson", "raw", MapActivity.this.getPackageName());
        InputStream IS = getResources().openRawResource(resourceIdentifier);
        BufferedReader reader = new BufferedReader(new InputStreamReader(IS));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (Exception ex) {
            reader.close();
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
