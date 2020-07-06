package com.trangdv.shipperfood.ui.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.utils.DialogUtils;

import io.reactivex.disposables.CompositeDisposable;

public class RestaurantDetailDialog extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog dialog;
    private TextView tvName, tvPhoneNumber, tvAddress;
    private String lat, lng, phone;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_restaurant_detail, null);
        dialog.setContentView(view);
        initView();
        setup(args);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;

    }

    private void setup(Bundle args) {
        tvName.setText(args.getString("name", ""));
        tvAddress.setText(args.getString("address", ""));
        tvPhoneNumber.setText(args.getString("phone", ""));
        phone = args.getString("phone", "");
        lat = args.getString("lat", "");
        lng = args.getString("lng", "");
    }

    private void initView() {
        tvName = dialog.findViewById(R.id.tv_restaurant_name);
        tvAddress = dialog.findViewById(R.id.tv_restaurant_address);
        tvPhoneNumber = dialog.findViewById(R.id.tv_restaurant_phone);
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet();
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+lat+","+lng));
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getContext().startActivity(mapIntent);
                }
            }
        });
        tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet();
                getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone))));
            }
        });
    }

    private void closeBottomSheet() {
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void doclick(View v) {
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
