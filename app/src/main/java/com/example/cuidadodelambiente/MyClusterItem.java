package com.example.cuidadodelambiente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements ClusterItem {
    private LatLng position;
    private Integer id;

    public MyClusterItem(LatLng position, Integer id) {
        this.position = position;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

}
