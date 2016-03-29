package com.mckuai.imc.Widget;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mckuai.imc.Bean.LeaderItem;
import com.mckuai.imc.R;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/3/29.
 */
public class LeaderDialog  extends DialogFragment {
    private View view;
    private Point screenSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_leader,container,false);
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getSize(screenSize);
        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showData(ArrayList<LeaderItem> items){

    }

}
