package com.mckuai.imc.Base;


import android.app.Fragment;
import android.content.Context;


public class BaseFragment extends Fragment {
    protected int mTitleResId;
    protected OnFragmentEventListener mOnFragmentEventListener;

    public interface OnFragmentEventListener {
        void onShow(int titleResId);
        void onAttach(int titleResId);
        void onActon(Object object);
    }

    public void setFragmentEventListener(OnFragmentEventListener l) {
        this.mOnFragmentEventListener = l;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && null != mOnFragmentEventListener) {
            mOnFragmentEventListener.onShow(mTitleResId);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (null != mOnFragmentEventListener) {
            mOnFragmentEventListener.onAttach(mTitleResId);
        }
    }

    public String getTitleResId() {
        return mTitleResId + "";
    }

    public String getTitle() {

        if (0 != mTitleResId) {
            return getString(mTitleResId);
        } else {
            return "未知";
        }
    }
}
