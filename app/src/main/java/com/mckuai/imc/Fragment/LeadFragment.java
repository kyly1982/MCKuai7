package com.mckuai.imc.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.imc.R;
import com.umeng.socialize.utils.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeadFragment extends Fragment {
    private static final String ARG_PARAM1 = "step";
    private int step = 0;
    private View view;
    private AppCompatImageView stepImage;


    public LeadFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static LeadFragment newInstance(int step) {
        LeadFragment fragment = new LeadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lead, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == stepImage) {
            initView();
        }
        showData();
    }

    private void initView() {
        stepImage = (AppCompatImageView) view.findViewById(R.id.image);
    }

    private void showData() {
        switch (step) {
            case 0:
                stepImage.setImageResource(R.mipmap.bg_leader_step1);
                break;
            case 1:
                stepImage.setImageResource(R.mipmap.bg_leader_step2);
                break;
            case 2:
                stepImage.setImageResource(R.mipmap.bg_leader_step3);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.e("" + step + "显示");
        }
    }
}
