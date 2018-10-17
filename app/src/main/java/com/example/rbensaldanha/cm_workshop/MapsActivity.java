package com.example.rbensaldanha.cm_workshop;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    ArrayList<LatLng> locals;
    LatLng local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetViewPanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        locals = new ArrayList<>();
        //Close to Washington Monument
        locals.add(new LatLng(38.887735, -77.032689));
        //Close to Colosseum
        locals.add(new LatLng(41.889129, 12.495238));
        //Close to Taj Mahal
        locals.add(new LatLng(27.170496,78.040306));
        //Close to the Great Sphinx
        locals.add(new LatLng(29.975117,31.137096));

        Random rand = new Random();

        int  n = rand.nextInt(locals.size());
        local = locals.get(n);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(local);
    }
}
