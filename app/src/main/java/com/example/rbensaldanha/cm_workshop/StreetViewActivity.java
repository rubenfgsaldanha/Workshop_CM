package com.example.rbensaldanha.cm_workshop;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Random;

public class StreetViewActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private ArrayList<LatLng> locals;
    private LatLng local;
    private Button btn_ready, btn_reset, btn_retry;
    private StreetViewPanorama streetViewPanorama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_map);

        btn_ready = (Button) findViewById(R.id.btn_ready);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_retry = (Button) findViewById(R.id.btn_retry);

        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetViewPanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        init();
    }

    private void init(){
        //4 places from different continents
        locals = new ArrayList<>();
        //Close to Washington Monument
        locals.add(new LatLng(38.887735, -77.032689));
        //Close to Colosseum
        locals.add(new LatLng(41.889129, 12.495238));
        //Close to Tokyo Tower
        locals.add(new LatLng(35.659988, 139.744451));
        //Close to the Great Sphinx
        locals.add(new LatLng(29.975117,31.137096));

        setLocation();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                streetViewPanorama.setPosition(local);
            }
        });

        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapActivity = new Intent(StreetViewActivity.this, MapsActivity.class);
                //sends coordinates to next activity
                mapActivity.putExtra("latitude",local.latitude);
                mapActivity.putExtra("longitude",local.longitude);
                startActivity(mapActivity);
            }
        });

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
                streetViewPanorama.setPosition(local);
            }
        });
    }

    private void setLocation(){
        //chooses a random place from those 4
        Random rand = new Random();
        int  n = rand.nextInt(locals.size());
        local = locals.get(n);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        this.streetViewPanorama = streetViewPanorama;
        streetViewPanorama.setPosition(local);
    }
}
