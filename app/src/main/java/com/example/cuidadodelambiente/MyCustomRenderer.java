package com.example.cuidadodelambiente;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyCustomRenderer extends DefaultClusterRenderer<MyClusterItem> {

    private ClusterManager<MyClusterItem> clusterManager;

    public MyCustomRenderer(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.clusterManager = clusterManager;
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyClusterItem> cluster) {
        if (cluster.getSize() <= 1) return false;

        boolean sonIguales = true;
        Iterator<MyClusterItem> iterator = cluster.getItems().iterator();

        MyClusterItem item = iterator.next();
        boolean continuar = true;
        while (iterator.hasNext() && continuar) {
            MyClusterItem otro = iterator.next();
            if (!item.getPosition().equals(otro.getPosition())) {
                sonIguales = false;
                continuar = false;
            }
        }

        return !sonIguales && cluster.getSize() > 5;
    }
}
