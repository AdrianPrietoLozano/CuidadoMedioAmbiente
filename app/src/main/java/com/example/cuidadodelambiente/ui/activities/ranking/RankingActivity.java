package com.example.cuidadodelambiente.ui.activities.ranking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.UserRank;
import com.google.android.material.appbar.MaterialToolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class RankingActivity extends AppCompatActivity implements Contract.View {

    private RecyclerView recyclerRanking;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;
    private Button botonVolverIntentar;
    private SwipeRefreshLayout contenidoPrincipal;
    private RelativeLayout layoutError;
    private RelativeLayout layoutCarga;
    private Contract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        presenter = new RankingPresenter(this);

        // Cambia el color del status bar a verde
        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Ranking");
        //toolbarTitle.setTextColor(Color.WHITE);

        contenidoPrincipal = findViewById(R.id.contenidoPrincipal);
        layoutCarga = findViewById(R.id.pantallaCarga);
        layoutError = findViewById(R.id.layoutSinConexion);
        botonVolverIntentar = findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.fetchRanking();
            }
        });

        contenidoPrincipal.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.fetchRanking();
            }
        });

        recyclerRanking = findViewById(R.id.recyclerRanking);
        recyclerRanking.setHasFixedSize(true);
        lManager = new LinearLayoutManager(getApplicationContext());
        recyclerRanking.setLayoutManager(lManager);

        presenter.fetchRanking();

    }

    @Override
    public void showLoading() {
        hideError();
        contenidoPrincipal.setVisibility(View.GONE);
        layoutCarga.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        layoutCarga.setVisibility(View.GONE);
        contenidoPrincipal.setRefreshing(false);
    }

    @Override
    public void showError(String error) {
        hideLoading();
        contenidoPrincipal.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        layoutError.setVisibility(View.GONE);
    }

    @Override
    public void showRanking(List<UserRank> ranking) {
        recyclerRanking.setAdapter(new RankingAdapter(getApplicationContext(), ranking));
        contenidoPrincipal.setVisibility(View.VISIBLE);
    }
}
