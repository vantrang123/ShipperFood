package com.trangdv.shipperfood.ui.orderdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.adapter.OrderItemAdapter;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.OrderDetail;
import com.trangdv.shipperfood.presenter.orderdetail.IOrderItemPresenter;
import com.trangdv.shipperfood.presenter.orderdetail.OrderItemPresenter;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.retrofit.RetrofitClient;
import com.trangdv.shipperfood.utils.DialogUtils;
import com.trangdv.shipperfood.view.IOrderItemView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class OrderItemFragment extends Fragment implements OrderItemAdapter.ItemListener, IOrderItemView {
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;
    DialogUtils dialogUtils;
    IOrderItemPresenter iOrderItemPresenter;

    private RecyclerView rvOrderItem;
    private RecyclerView.LayoutManager layoutManager;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderDetail> orderDetailList = new ArrayList<>();
    private boolean loaded = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (loaded) {
            outState.putBoolean("loaded", true);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            loaded = savedInstanceState.getBoolean("loaded", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail_item, container, false);
        findViewById(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        layoutManager = new LinearLayoutManager(getContext());
        rvOrderItem.setLayoutManager(layoutManager);
        if (!loaded) {
            dialogUtils.showProgress(getContext());
            iOrderItemPresenter.getAllOrderItem();
        } else {
            showDataLoaded();
        }

    }

    private void init() {
        anNgonAPI = RetrofitClient.getInstance(Common.API_ANNGON_ENDPOINT).create(IAnNgonAPI.class);
        compositeDisposable = new CompositeDisposable();
        iOrderItemPresenter = new OrderItemPresenter(this, anNgonAPI, compositeDisposable);
        dialogUtils = new DialogUtils();
    }

    private void showDataLoaded() {
        orderItemAdapter = new OrderItemAdapter(getContext(), orderDetailList, this);
        rvOrderItem.setAdapter(orderItemAdapter);
    }

    private void findViewById(View view) {
        rvOrderItem = view.findViewById(R.id.rv_order_item);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void dispatchToFoodDetail(int position) {

    }

    @Override
    public void onAllOrderSuccess(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        orderItemAdapter = new OrderItemAdapter(getActivity(), orderDetailList, this);
        rvOrderItem.setAdapter(orderItemAdapter);
        dialogUtils.dismissProgress();
    }

    @Override
    public void onAllOrderError(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dialogUtils.dismissProgress();
    }
}
