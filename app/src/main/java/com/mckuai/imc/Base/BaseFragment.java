package com.mckuai.imc.Base;


import android.app.Fragment;
import android.content.Context;
import android.view.View;


public class BaseFragment extends Fragment {
    protected int mTitleResId;
    protected OnFragmentEventListener mOnFragmentEventListener;

    public interface OnFragmentEventListener {
        void onFragmentShow(int titleResId);
        void onFragmentAttach(int titleResId);
        void onFragmentAction(Object object);
    }

    public void setFragmentEventListener(OnFragmentEventListener l) {
        this.mOnFragmentEventListener = l;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && null != mOnFragmentEventListener) {
            mOnFragmentEventListener.onFragmentShow(mTitleResId);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (null != mOnFragmentEventListener) {
            mOnFragmentEventListener.onFragmentAttach(mTitleResId);
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

    private void showMessage(String msg, String action, View.OnClickListener listener) {
        if (null != getActivity()) {
            ((BaseActivity) getActivity()).showMessage(msg, action, listener);
        }
    }


    public boolean onBackPressed(){
        return false;
    }
}
