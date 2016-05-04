package com.mckuai.imc.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mckuai.imc.Activity.CartoonActivity;
import com.mckuai.imc.Activity.MainActivity;
import com.mckuai.imc.Activity.UserCenterActivity;
import com.mckuai.imc.Base.BaseFragment;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.Ad;
import com.mckuai.imc.Bean.Cartoon;
import com.mckuai.imc.Bean.MCCartoonPKNotificationMessage;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.DownloadService;
import com.mckuai.imc.R;
import com.mckuai.imc.Util.MCNetEngine;
import com.mckuai.imc.Widget.CompetitionLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;


public class MainFragment_Competition extends BaseFragment implements CompetitionLayout.OnActionListener
        ,MCNetEngine.OnLoadCartoonListResponseListener
        ,MCNetEngine.OnRewardCartoonResponseListener
        ,View.OnClickListener
        ,MCNetEngine.OnGetAdsResponse{
    private View view;
    private CompetitionLayout competitionLayout;
    private ArrayList<Cartoon> cartoons;
    private RelativeLayout adRoot;
    private ImageView adView;
    private AppCompatButton adCloseBtn;

    private Page page;
    private Cartoon voteCartoon;
    private Cartoon failCartoon;

    private MCKuai application;

    private long lastAdShowTime = 0;
    private int lastAdInterval = 0;
    private int adShowCount = 0;
    private ArrayList<Ad> ads;
    private ImageLoader loader;


    public MainFragment_Competition() {
        application = MCKuai.instence;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_main_fragment_competition, container, false);
            initView();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null == page) {
            loadData();
        }
        if (null == ads){
            application.netEngine.getAds(getActivity(),this);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showData();
        }
        if (lastAdShowTime > 0){
            long time = System.currentTimeMillis();
            lastAdShowTime = time - (time - lastAdShowTime);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            onVote(voteCartoon, failCartoon);
        }
    }

    private void initView() {
        competitionLayout = (CompetitionLayout) view.findViewById(R.id.competition);
        adRoot = (RelativeLayout) view.findViewById(R.id.adRoot);
        adView = (ImageView) view.findViewById(R.id.ad);
        adCloseBtn = (AppCompatButton) view.findViewById(R.id.adCloseButton);

        competitionLayout.setActionListener(this);
        adView.setOnClickListener(this);
        adCloseBtn.setOnClickListener(this);
    }


    private void loadData() {
        if (null == page) {
            page = new Page();
        }
        application.netEngine.loadCartoonList(getActivity(), page, this);
    }

    private void showData() {
        if (null == page) {
            loadData();
        } else {
            if (null != cartoons) {
                competitionLayout.setData(cartoons);
            }
        }
    }

    private void showAd() {
        if (check()){
            if (null != ads && null != ads.get(adShowCount) && null != ads.get(adShowCount).getBitmap()){
                MobclickAgent.onEvent(getActivity(),"IAD_ShowAd");
                adView.setImageBitmap(ads.get(adShowCount ).getBitmap());
                adView.setTag(ads.get(adShowCount));

                lastAdShowTime = System.currentTimeMillis();
                adRoot.setVisibility(View.VISIBLE);
                adShowCount++;
                lastAdInterval = 0;
                if (adShowCount < ads.size()) {
                    preLoadAdBitmap();
                }
            }
        }
    }

    private boolean check() {
//        Log.e("check",null == ads ? "广告为空":"adshowcount="+adShowCount +", lastTime="+lastAdShowTime +", lastAdInterval="+lastAdInterval);
        if (null != ads && ads.size() > adShowCount && adShowCount < 2) {
            lastAdInterval++;
            if (lastAdInterval > 2) {//最多允许每三次
                long time = System.currentTimeMillis();
                if (time - lastAdShowTime > 30000) {
                    double d = Math.random() * 10;
                    d += Math.pow(lastAdInterval / 4, 3);
//                    Log.e("check","ad="+d);
                    return d > 10;
                }
            }
        }
        return false;
    }

    private void preLoadAdBitmap(){
        if (null != ads && ads.size() > adShowCount && null != ads.get(adShowCount)){
            if (null == loader){
                loader = ImageLoader.getInstance();
            }
            loader.loadImage(ads.get(adShowCount).getImageUrl(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ads.get(adShowCount).setBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    private void sendPKNotification(Cartoon win,Cartoon lose){
        if (null != RongIM.getInstance() && null != RongIM.getInstance().getRongIMClient()) {
            //TextMessage message = TextMessage.obtain("恭喜你，你的漫画在与 \"" + lose.getOwner().getNickEx() + "\" 的漫画的PK中取得了胜利。");
            MCCartoonPKNotificationMessage msg = new MCCartoonPKNotificationMessage(win,lose);

            RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.SYSTEM, win.getOwner().getName(), msg, application.user.getNike() + "你的漫画在PK中取得了胜利！", "", new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                }

                @Override
                public void onSuccess(Integer integer) {

                }
            }, new RongIMClient.ResultCallback<Message>() {
                @Override
                public void onSuccess(Message message) {

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ad:
                MobclickAgent.onEvent(getActivity(),"IAD_Click");
                Ad ad = (Ad) v.getTag();
                if (null != ad) {
                    Intent intent = new Intent(getActivity(), DownloadService.class);
                    intent.putExtra("URL",ad.getDownUrl());
                    intent.putExtra("NAME",ad.getDownName());
                    getActivity().startService(intent);
                }
                adRoot.setVisibility(View.GONE);
                break;
            case R.id.adCloseButton:
                MobclickAgent.onEvent(getActivity(),"IAD_Close");
                adRoot.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void EOF() {
        loadData();
    }

    @Override
    public void onShowDetile(Cartoon cartoon) {
        Intent intent = new Intent(getActivity(), CartoonActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.cartoondetail_tag_cartoon), cartoon);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }



    @Override
    public void onVote(Cartoon win, Cartoon lose) {
        MobclickAgent.onEvent(getActivity(), "competitionCartoon");
//        Log.e("onVote","win="+win.getId()+",fail="+fail.getId());
        if (null != win && null != lose) {
            if (application.isLogin()) {
                MCKuai.instence.netEngine.rewardCartoon(getActivity(), application.user.getId(), win, this);
                sendPKNotification(win,lose);
                showAd();
            } else {
                voteCartoon = win;
                failCartoon = lose;
                ((MainActivity) getActivity()).callLogin(2);
            }
        }
    }

    @Override
    public void onShowUser(User user) {
        Intent intent = new Intent(getActivity(), UserCenterActivity.class);
        intent.putExtra(getString(R.string.usercenter_tag_userid), user.getId().intValue());
        getActivity().startActivity(intent);
    }

    @Override
    public void onLoadCartoonListFailure(String msg) {
        showMessage(msg, "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    @Override
    public void onLoadCartoonListSuccess(ArrayList<Cartoon> cartoons) {
        page.setPage(page.getPage() + 1);
        competitionLayout.setData(cartoons);
    }

    @Override
    public void onRewardaCartoonFailure(String msg) {
        if (msg.equals("已赞")) {
            MobclickAgent.onEvent(getActivity(), "competitionCartoon_FR");
//            Log.e("onReward","Failure_FR");
            competitionLayout.showData();
            return;
        }
        showMessage(msg, null, null);
        MobclickAgent.onEvent(getActivity(), "competitionCartoon_F");
//        Log.e("onReward","Failure");
        competitionLayout.showData();
    }

    @Override
    public void onRewardCartoonSuccess() {
        MobclickAgent.onEvent(getActivity(), "competitionCartoon_S");
//        Log.e("onReward","Success");
        competitionLayout.showData();
    }

    @Override
    public void onGetAdFailure(String msg) {

    }

    @Override
    public void onGetAdSuccess(ArrayList<Ad> ads) {
        this.ads = ads;
        preLoadAdBitmap();
    }
}
