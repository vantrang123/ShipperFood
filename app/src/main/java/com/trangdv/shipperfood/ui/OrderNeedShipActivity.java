package com.trangdv.shipperfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.adapter.NeedShippAdapter;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Order;
import com.trangdv.shipperfood.model.Restaurant;
import com.trangdv.shipperfood.model.ShippingOrder;
import com.trangdv.shipperfood.presenter.shippingorder.IOrderNeedShipPresenter;
import com.trangdv.shipperfood.presenter.shippingorder.OrderNeedShipPresenter;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.retrofit.RetrofitClient;
import com.trangdv.shipperfood.ui.dialog.RestaurantDetailDialog;
import com.trangdv.shipperfood.ui.orderdetail.OrderDetailActivity;
import com.trangdv.shipperfood.utils.DialogUtils;
import com.trangdv.shipperfood.view.IOrderNeedShipView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OrderNeedShipActivity extends AppCompatActivity implements IOrderNeedShipView, NeedShippAdapter.ItemListener {
    DialogUtils dialogUtils;
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;
    IOrderNeedShipPresenter iOrderNeedShipPresenter;

    private RecyclerView rvNeedShip;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swrRestaurant;
    private NeedShippAdapter needShippAdapter;
    private List<Order> orderList = new ArrayList<>();
    private List<ShippingOrder> shippingOrderList;
    private ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needship);
        findViewById();

        init();
    }

    private void findViewById() {
        rvNeedShip = findViewById(R.id.recycler_needship);
        swrRestaurant = findViewById(R.id.swr_needship);
        ivBack = findViewById(R.id.iv_back);
    }

    private void init() {
        dialogUtils = new DialogUtils();
        anNgonAPI = RetrofitClient.getInstance(Common.API_ANNGON_ENDPOINT).create(IAnNgonAPI.class);
        compositeDisposable = new CompositeDisposable();
        iOrderNeedShipPresenter = new OrderNeedShipPresenter(this, anNgonAPI, compositeDisposable);
        layoutManager = new LinearLayoutManager(this);
        rvNeedShip.setLayoutManager(layoutManager);
        needShippAdapter = new NeedShippAdapter(OrderNeedShipActivity.this, orderList, this);
        rvNeedShip.setAdapter(needShippAdapter);

        swrRestaurant.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_orange_dark);
        swrRestaurant.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderList.clear();
                dialogUtils.showProgress(OrderNeedShipActivity.this);
                iOrderNeedShipPresenter.getOrderNeedShip(Common.curentRestaurantId);
                swrRestaurant.setRefreshing(false);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iOrderNeedShipPresenter.getOrderNeedShip(Common.curentRestaurantId);

    }


    @Override
    public void onOrderNeedShipSuccess(List<Order> result) {
        iOrderNeedShipPresenter.getShippingOrder(Common.curentRestaurantId, Common.currentShipper.getId());
        orderList.addAll(result);
        needShippAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShippingOrderError(String message) {
        dialogUtils.dismissProgress();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShippingOrderSuccess(List<ShippingOrder> result) {
        dialogUtils.dismissProgress();
        shippingOrderList = result;
    }

    @Override
    public void dispatchToOrderDetail(int position) {
        dialogUtils.showProgress(this);
        Intent intent = new Intent(this, OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        if (shippingOrderList.size() == orderList.size() && shippingOrderList.get(position).getOrderId() == orderList.get(position).getOrderId())
            bundle.putInt("Status" ,shippingOrderList.get(position).getShippingStatus());
        else
            bundle.putInt("Status" ,0);
        intent.putExtras(bundle);
        Common.currentOrder = orderList.get(position);
        startActivity(intent);
    }

    @Override
    public void openRestaurantDetail() {
        compositeDisposable.add(
                anNgonAPI.getRestaurant(Common.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(restaurantModel -> {
                                    if (restaurantModel.isSuccess()) {
                                        Restaurant restaurant = restaurantModel.getResult().get(0);
                                        Bundle args = new Bundle();
                                        args.putString("name", restaurant.getName());
                                        args.putString("address", restaurant.getAddress());
                                        args.putString("phone", restaurant.getPhone());
                                        args.putString("lat", restaurant.getLat().toString());
                                        args.putString("lng", restaurant.getLng().toString());
                                        RestaurantDetailDialog dialog = new RestaurantDetailDialog();
                                        dialog.setArguments(args);
                                        dialog.show(getSupportFragmentManager(), "restaurant detail dialog");
                                    }
                                    dialogUtils.dismissProgress();
                                },
                                throwable -> {
                                    dialogUtils.dismissProgress();
                                }
                        ));
    }

    @Override
    protected void onStop() {
        dialogUtils.dismissProgress();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.animateFinish(this);
    }
}
