package com.trangdv.shipperfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.adapter.RestaurantAdapter;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Restaurant;
import com.trangdv.shipperfood.presenter.restaurant.IRestaurantPresenter;
import com.trangdv.shipperfood.presenter.restaurant.RestautantPresenter;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.retrofit.RetrofitClient;
import com.trangdv.shipperfood.utils.DialogUtils;
import com.trangdv.shipperfood.utils.GpsUtils;
import com.trangdv.shipperfood.view.IRestaurantView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class RestaurantFragment extends Fragment implements RestaurantAdapter.ItemListener, IRestaurantView {

    private RecyclerView rvRestaurant;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swrRestaurant;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurantList;
    DialogUtils dialogUtils;
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;
    IRestaurantPresenter iRestaurantPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvRestaurant = view.findViewById(R.id.recycler_orders);
        swrRestaurant = view.findViewById(R.id.swr_restaurant);
        layoutManager = new LinearLayoutManager(getContext());
        rvRestaurant.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        dialogUtils = new DialogUtils();
        anNgonAPI = RetrofitClient.getInstance(Common.API_ANNGON_ENDPOINT).create(IAnNgonAPI.class);
        compositeDisposable = new CompositeDisposable();
        iRestaurantPresenter = new RestautantPresenter(this, anNgonAPI, compositeDisposable);
        restaurantList = new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(getContext(), restaurantList, this);
        rvRestaurant.setAdapter(restaurantAdapter);

        swrRestaurant.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swrRestaurant.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                openGPS();
                swrRestaurant.setRefreshing(false);
            }
        });
        openGPS();
    }

    public void requestNearbyRestaurant(double latitude, double longitude) {
        dialogUtils.showProgress(getContext());
        restaurantList.clear();
        iRestaurantPresenter.getRestaurant(latitude, longitude, 10);
    }

    private void openGPS() {
        if (!((MainActivity)getActivity()).isGPS) {
            new GpsUtils(getContext()).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {
                    // turn on GPS
                    ((MainActivity)getActivity()).isGPS = isGPSEnable;
                    ((MainActivity)getActivity()).isContinue = false;
                    ((MainActivity)getActivity()).getLocation();
                }
            });
        } else {
            ((MainActivity)getActivity()).isContinue = false;
            ((MainActivity)getActivity()).getLocation();
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void dispatchToOrderNeedShip(int position) {
        Common.curentRestaurantId = restaurantList.get(position).getId();
        startActivity(new Intent(getContext(), OrderNeedShipActivity.class));
    }

    @Override
    public void onRestaurantSuccess(List<Restaurant> restaurants) {
        dialogUtils.dismissProgress();
        restaurantList.addAll(restaurants);
        restaurantAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRestaurantError(String message) {
        dialogUtils.dismissProgress();
    }

}
