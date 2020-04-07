package com.trangdv.shipperfood.ui.orderdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.FCMSendData;
import com.trangdv.shipperfood.remote.IFCMService;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.retrofit.RetrofitClient;
import com.trangdv.shipperfood.retrofit.RetrofitFCMClient;
import com.trangdv.shipperfood.ui.dialog.ConfirmUpdateOrderStatusDialog;
import com.trangdv.shipperfood.utils.DialogUtils;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OrderStatusFragment extends Fragment implements View.OnClickListener {
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;
    DialogUtils dialogUtils;
    IFCMService ifcmService;

    private ImageView ivRegistation, ivAccepted, ivShipping, ivShipped, ivCancelled;
//    private TextView tvUpdateStatus;
    private int statusId = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_status, container, false);
        findViewById(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        initStatus();
    }

    private void init() {
        anNgonAPI = RetrofitClient.getInstance(Common.API_ANNGON_ENDPOINT).create(IAnNgonAPI.class);
        compositeDisposable = new CompositeDisposable();
        dialogUtils = new DialogUtils();
        ifcmService = RetrofitFCMClient.getInstance(Common.fcmUrl).create(IFCMService.class);
    }

    private void findViewById(View view) {
        ivRegistation = view.findViewById(R.id.iv_registation_order);
        ivRegistation.setOnClickListener(this);
        ivAccepted = view.findViewById(R.id.iv_accepted);
        ivAccepted.setOnClickListener(this);
        ivShipping = view.findViewById(R.id.iv_shipping);
        ivShipping.setOnClickListener(this);
        ivCancelled = view.findViewById(R.id.iv_shipped);
        ivCancelled.setOnClickListener(this);
        ivShipped = view.findViewById(R.id.iv_shipped);
        ivShipped.setOnClickListener(this);
//        tvUpdateStatus = view.findViewById(R.id.tv_update_status);
//        tvUpdateStatus.setOnClickListener(this);
    }

    public void initStatus() {
        int id = ((OrderDetailActivity)getActivity()).status;
        updateCorlorStatus(id);
    }
    public void updateOrderStatus() {
        dialogUtils.showProgress(getContext());
        /*compositeDisposable.add(anNgonAPI.updateOrderStatus(Common.API_KEY,
                Common.currentOrder.getOrderId(),
                statusId
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateOrderModel -> {
                            getToken();
                        }
                        , throwable -> {
                            dialogUtils.dismissProgress();
                        }
                ));*/
        if (statusId == 1) {
            compositeDisposable.add(anNgonAPI.setShippingOrder(Common.API_KEY,
                    Common.currentOrder.getOrderId(),
                    Common.currentOrder.getRestaurantId(),
                    Common.currentShipper.getId(),
                    1
            )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(shippingOrderModel -> {
                                if (shippingOrderModel.isSuccess()) {
                                    dialogUtils.dismissProgress();
                                    sendNotificatonToRestaurant(Common.currentOrder.getRestaurantId());
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Gửi yêu cầu thành công")
                                            .setContentText("Bạn sẻ nhận được thông báo khi có chấp nhận từ nhà hàng")
                                            .show();
                                    updateCorlorStatus(statusId);
                                } else {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            , throwable -> {
                                dialogUtils.dismissProgress();
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                    ));
        }
    }

    private void sendNotificatonToRestaurant(int i) {
        Map<String, String> dataSend = new HashMap<>();
        dataSend.put(Common.NOTIFI_TITLE, "Status Order");
        dataSend.put(Common.NOTIFI_CONTENT, "You have new accept ship" /*+ createOrderModel.getResult().get(0)*/);

        FCMSendData sendData = new FCMSendData(Common.createTopicSender(
                Common.getTopicChannel(
                        i
                )), dataSend);

        compositeDisposable.add(ifcmService.sendNotification(sendData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fcmResponse -> {
                    dialogUtils.dismissProgress();
                }, throwable -> {
                    dialogUtils.dismissProgress();
                })
        );
    }
    private void getToken() {
        compositeDisposable.add(anNgonAPI.getToken(Common.API_KEY,
                Common.currentOrder.getOrderFBID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokenModel -> {
                            if (tokenModel.isSuccess()) {
                                Map<String, String> messageSend = new HashMap<>();
                                messageSend.put(Common.NOTIFI_TITLE, "Your order has been updated");
                                messageSend.put(Common.NOTIFI_CONTENT, new StringBuilder("Your order ")
                                .append(Common.currentOrder.getOrderId())
                                .append(" has been update to")
                                .append(Common.convertCodeToStatus(statusId)).toString());

                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getResult().get(0).getToken(), messageSend);
                                compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            Toast.makeText(getContext(), "[UPDATE SUCCESS]", Toast.LENGTH_SHORT).show();
                                            updateCorlorStatus(statusId);
                                            Common.currentOrder.setOrderStatus(statusId);
                                            ((OrderDetailActivity)getActivity()).sendResult();
                                            dialogUtils.dismissProgress();
                                        }, throwable -> {
                                            Toast.makeText(getContext(), "[UPDATE FAILED]", Toast.LENGTH_SHORT).show();
                                            dialogUtils.dismissProgress();
                                        } )
                                );
                            }
                        }
                        , throwable -> {
                            dialogUtils.dismissProgress();
                        }
                ));
    }

    private void updateCorlorStatus(int id) {
        switch (id) {
            case -1:
                ivCancelled.setBackground(getResources().getDrawable(R.drawable.bg_iv_status));
                ivCancelled.setClickable(false);
            case 3:
                ivShipping.setBackground(getResources().getDrawable(R.drawable.bg_iv_status));
                ivShipping.setClickable(false);
            case 2:
                ivAccepted.setBackground(getResources().getDrawable(R.drawable.bg_iv_status));
                ivAccepted.setClickable(false);
            case 1:
                ivRegistation.setBackground(getResources().getDrawable(R.drawable.bg_iv_status));
                ivRegistation.setClickable(false);
                break;
            default:
                break;
        }
    }

    private void showConfirmDialog() {
        new ConfirmUpdateOrderStatusDialog().show(getFragmentManager(), "ConfirmUpdateStatus");
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_registation_order:
                statusId = 1;
//                ivOrderPlaced.setClickable(true);
                showConfirmDialog();
                break;
            case R.id.iv_accepted:
                statusId = 2;
//                ivShipping.setClickable(true);
                showConfirmDialog();
                break;
            case R.id.iv_shipping:
                statusId = 3;
//                ivShipped.setClickable(true);
                showConfirmDialog();
                break;
            case R.id.iv_shipped:
                statusId = -1;
//                ivCancelled.setClickable(true);
                showConfirmDialog();
                break;
            default:
                break;
        }
    }
}
