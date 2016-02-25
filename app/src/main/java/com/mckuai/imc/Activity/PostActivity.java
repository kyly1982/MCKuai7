package com.mckuai.imc.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.UMImage;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PostActivity extends BaseActivity implements OnClickListener, TextWatcher
{

	private String TAG = "PostActivity";

	//private Button btn_showOwner;
	//private ImageView btn_return;
	private ImageButton btn_reply;
	private ImageButton btn_share;
	private ImageButton btn_collect;
	private ImageButton btn_pic;
	private ImageButton btn_reward;
	private RelativeLayout reply_layout;// 回复层
	private RelativeLayout post_layout;// 帖子显示层
	private TextView tv_title;// 标题栏
	private TextView tv_hint;
	private LinearLayout mpics;
	private static WebView webView;
	private static EditText edt_content;// 编辑框

	private Post post;
	private String url;
	private String[] type = { "admin", "all" };// admin:只显示楼主；all:显示所有
	private String key = type[1];

	// 回复时所要用的一些东西，由web部分通过java接口传值
	private int mUserId;// 当前用户的id
	private int ownerId;
	private int isNew;
	private int forumId;
	private String forumName;
	private int postId;
	private String postTitle;
	private int floorId;// 要回复的楼层的id
	private int floorOwnerId;
	private String floorOwnerName;

	// 回复中要用到的一些变量
	private ArrayList<Bitmap> picsList;
	private String picUrl;// 这是上传的图片的位置
	private boolean isShowPost = true;// 为真时显示帖子，否则为显示回复
	private boolean isReplyPost;// 是否回复帖子，为真为回复帖子，否则为回复楼层
	private boolean isPicUpload = false;
	private boolean isCollect = false;// 帖子是否被收藏
	private boolean isReward = false;// 帽子是否被打赏

	private AsyncHttpClient mClient;
	//private com.umeng.socialize.controller.UMSocialService mShareService;

	private static final int LOGIN = 0;
	private static final int GETPIC = 1;
	private static final int COLLECT_POST = 2;
	private static final int REWARD_POST = 3;
	private static final int REPLY_POST = 4;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		if (null == savedInstanceState)
		{
			savedInstanceState = new Bundle();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		initToolbar(R.id.toolbar, 0, new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mClient = new AsyncHttpClient();
		post = (Post) getIntent().getSerializableExtra(getString(R.string.tag_post));
		mHandler.sendMessageDelayed(mHandler.obtainMessage(5), 1000);
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("帖子详情");
		initView();
		if (isShowPost)
		{
			showPost();
		} else
		{
			showReply();
		}
		getPostMark();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mckuai.imc.activity.BaseActivity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("帖子详情");
	}


	/*
             * (non-Javadoc)
             *
             * @see com.mckuai.imc.activity.BaseActivity#finish()
             */
	@Override
	public void finish()
	{
		// TODO Auto-generated method stub
		if (null != webView)
		{
			ViewGroup parent = (ViewGroup) webView.getParent();
			if (null != parent)
			{
				parent.removeAllViews();
			}
			webView.removeAllViews();
			webView.destroy();
		}
		super.finish();
	}

	private void initView()
	{
		// findViewById(R.id.btn_return).setOnClickListener(this);
		webView = (WebView) findViewById(R.id.webview);
		//btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
		btn_collect = (ImageButton) findViewById(R.id.btn_collectPost);
		btn_reply = (ImageButton) findViewById(R.id.btn_replyPost);
		btn_reward = (ImageButton) findViewById(R.id.btn_rewardPost);
		//btn_return = (ImageView) findViewById(R.id.btn_left);
		btn_share = (ImageButton) findViewById(R.id.btn_sharePost);
		reply_layout = (RelativeLayout) findViewById(R.id.rl_reply);
		post_layout = (RelativeLayout) findViewById(R.id.rl_post);
		edt_content = (EditText) findViewById(R.id.edt_reply_Content);
		btn_pic = (ImageButton) findViewById(R.id.btn_addPic);
		mpics = (LinearLayout) findViewById(R.id.ll_piclayer);
		//btn_showOwner.setVisibility(View.VISIBLE);

		btn_reward.setOnClickListener(this);
		//btn_showOwner.setOnClickListener(this);
		btn_collect.setOnClickListener(this);
		btn_reply.setOnClickListener(this);
		//btn_return.setOnClickListener(this);
		btn_share.setOnClickListener(this);
		btn_pic.setOnClickListener(this);
		edt_content.addTextChangedListener(this);

		tv_title = (TextView) findViewById(R.id.actionbar_title);
		tv_hint = (TextView) findViewById(R.id.tv_lengthHint);
		reply_layout = (RelativeLayout) findViewById(R.id.rl_reply);

		tv_title.setTextColor(getResources().getColor(R.color.icons));

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setSupportMultipleWindows(true);
		webView.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view, int newProgress)
			{
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if (0 == newProgress)
				{
					//popupLoadingToast(getString(R.string.onloading_hint));
				}
				if (100 == newProgress)
				{

					if (null != webView)
					{
						mHandler.sendMessage(mHandler.obtainMessage(6));
						getParams();
					}
					runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							//cancleLodingToast(true);
						}
					});
				}
			}

		});
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				Log.e(TAG, "url= " + url);
				view.loadUrl(url);
				return true;
			}
		});
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		addInterface();
	}


	private void addInterface()
	{
		if (null != webView)
		{
			webView.addJavascriptInterface(new MyJavaScriptInterface(), "interface");
		}
	}

	private void showPost()
	{
		if (null != post)
		{
			if (!isShowPost)
			{
				reply_layout.setVisibility(View.GONE);
				post_layout.setVisibility(View.VISIBLE);
				isShowPost = true;
			}
			tv_title.setText("帖子详情");
			url = getString(R.string.interface_domainName) + getString(R.string.interface_showPost);
			url += "&id=" + post.getId();
			webView.loadUrl(url);
		} else
		{
			Toast.makeText(PostActivity.this, "没有获取取帖子信息", Toast.LENGTH_SHORT).show();
		}
	}

	private void showReply()
	{
		isShowPost = false;
		//btn_showOwner.setText("发 布");
		//btn_showOwner.setVisibility(View.VISIBLE);
		//showKeyboard(null);
		if (isReplyPost)
		{
			MobclickAgent.onEvent(this, "replyPost");
			tv_title.setText("跟帖");
		} else
		{
			MobclickAgent.onEvent(this, "replyFoolr");
			tv_title.setText("回复");
		}
		post_layout.setVisibility(View.GONE);
		reply_layout.setVisibility(View.VISIBLE);
		//showKeyboard(null);
	}

	private void resumeShowPost()
	{
		//hideKeyboard(edt_content);
		isShowPost = true;
		//btn_showOwner.setText("只看楼主");
		tv_title.setText("帖子详情");
		post_layout.setVisibility(View.VISIBLE);
		reply_layout.setVisibility(View.GONE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (isShowPost)
			{
				if (null != webView && webView.canGoBack())
				{
					webView.goBack();
					return true;
				}
			} else
			{
				resumeShowPost();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		/*case R.id.btn_left:
			if (isShowPost)
			{
				this.finish();
			} else
			{
				isShowPost = true;
				resumeShowPost();
			}
			break;*/
		case R.id.btn_collectPost:
			// 关注帖子
			if (isCollect)
			{
				MobclickAgent.onEvent(this, "cancleCollectPost");
			} else
			{
				MobclickAgent.onEvent(this, "collectPost");
			}
			collectPost();
			break;
		case R.id.btn_sharePost:
			// 分享帖子
			MobclickAgent.onEvent(this, "sharePost");
			sharePost();
			break;
		case R.id.btn_replyPost:
			isReplyPost = true;
			showReply();
			break;
		/*case R.id.btn_showOwner:
			if (isShowPost)
			{
				if (key.equals(type[0]))
				{
					key = type[1];
					btn_showOwner.setText("只看楼主");
				} else
				{
					key = type[0];
					btn_showOwner.setText("完整帖子");
				}
				webView.loadUrl(url + "&admin=" + key);
			} else
			{
				replyPost();
			}
			break;*/
		case R.id.btn_addPic:
			MobclickAgent.onEvent(this, "addPic_Reply");
			Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, GETPIC);
			break;

		case R.id.btn_rewardPost:
			MobclickAgent.onEvent(this, "rewardPost");
			rewardPost();
			break;

		default:
			break;
		}
	}

	private void rewardPost()
	{
		if (mApplication.isLogin())
		{
			String url = getString(R.string.interface_domainName) + getString(R.string.interface_reward);
			RequestParams params = new RequestParams();
			params.put("userId", mApplication.user.getId());
			params.put("talkId", post.getId());
			mClient.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    // TODO Auto-generated method stub
                    super.onStart();
                   // popupLoadingToast("正在打赏楼主!");
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int,
                 * org.apache.http.Header[], org.json.JSONObject)
                 */
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // TODO Auto-generated method stub
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if (response.has("state") && response.getString("state").equalsIgnoreCase("ok")) {
                            //showNotification(1, "打赏成功！楼主获得了1个钻石", R.id.rl_post);
//							Toast.makeText(PostActivity.this, "打赏成功！\n楼主获得了1个钻石", Toast.LENGTH_SHORT).show();
                            //cancleLodingToast(true);
                            isReward = true;
                            setButtonFunction();
                            return;
                        } else {
                            //showNotification(1, "打赏失败！", R.id.rl_post);
                            //cancleLodingToast(false);
                            return;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    //showNotification(1, "打赏失败！", R.id.rl_post);
                    //cancleLodingToast(false);
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * com.loopj.android.http.JsonHttpResponseHandler#onFailure(int,
                 * org.apache.http.Header[], java.lang.String,
                 * java.lang.Throwable)
                 */
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    // TODO Auto-generated method stub
                    super.onFailure(statusCode, headers, responseString, throwable);
                    //showNotification(1, "操作失败！", R.id.rl_post);
                    //cancleLodingToast(false);
                }
            });
		} else
		{
			callLogin(REWARD_POST);
		}
	}

	private void collectPost()
	{
		if (mApplication.isLogin())
		{
			String url;
			if (isCollect)
			{
				url = getString(R.string.interface_domainName) + getString(R.string.interface_cancle_collectpost);
			} else
			{
				url = getString(R.string.interface_domainName) + getString(R.string.interface_collectpost);
			}
			RequestParams params = new RequestParams();
			params.put("userId", mApplication.user.getId());
			params.put("talkId", post.getId());
			mClient.get(url, params, new JsonHttpResponseHandler()
			{
				@Override
				public void onStart()
				{
					// TODO Auto-generated method stub
					super.onStart();
					if (isCollect)
					{
						//popupLoadingToast("正在取消收藏...");
					}
					else {
						//popupLoadingToast("正在添加到背包...");
					}
					
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response)
				{
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, headers, response);
					if (response.has("state"))
					{
						try
						{
							// Log.e(TAG, response.toString());
							if (response.getString("state").equalsIgnoreCase("ok"))
							{
								if (isCollect)
								{
									Toast.makeText(PostActivity.this, "取消关注成功!", Toast.LENGTH_SHORT).show();
								} else
								{
									Toast.makeText(PostActivity.this, "帖子关注成功，可在背包中看到!", Toast.LENGTH_SHORT).show();
								}
								//cancleLodingToast(true);
								// btn_collect.setEnabled(false);
								isCollect = !isCollect;
								setButtonFunction();
								return;
							} else
							{
								Toast.makeText(PostActivity.this, "操作失败!", Toast.LENGTH_LONG).show();
								//cancleLodingToast(false);
								return;
							}
						} catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							//cancleLodingToast(false);
							// showNotification("自动收获机器被熊孩子玩坏了!");
                            //showNotification(1, "操作失败！", R.id.rl_post);
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
				{
					// TODO Auto-generated method stub
					super.onFailure(statusCode, headers, responseString, throwable);
					//cancleLodingToast(false);
                    //showNotification(1, "收藏失败！", R.id.rl_post);
				}
			});
		} else
		{
			callLogin(COLLECT_POST);
		}
	}

/*	@overwite
	protected void callLogin(int requestCode)
	{
		Intent intent = new Intent(PostActivity.this, LoginActivity.class);
		intent.putExtra(getString(R.string.needLoginResult), true);// 只是调用，需要返回值
		startActivityForResult(intent, requestCode);
	}*/

	private void replyPost()
	{
		// 检查是否满足发布回复的条件
		if (checkReplyInfo())
		{
			if (0 == ownerId)
			{
				getParams();
				return;
			}
			ArrayList<String> picIds;
			if (null != picsList && picsList.size() > 0 && !isPicUpload)
			{
//				MobclickAgent.onEvent(this, "picCount_Reply", picsList.size());
				uploadPic();
				return;
			}
			String url;
			RequestParams params = new RequestParams();
			if (isReplyPost)
			{
				url = getString(R.string.interface_domainName) + getString(R.string.interface_replypost);
				params.put("operUserId", ownerId);
				params.put("isNew", isNew);
				params.put("forumId", forumId);
				params.put("forumName", forumName);
				params.put("talkId", postId);
				params.put("talkTitle", postTitle);
				if (null != picUrl && 0 < picUrl.length())
				{
					params.put("content", edt_content.getText().toString() + picUrl);
				} else
				{
					params.put("content", edt_content.getText().toString());
				}
			} else
			{
				url = getString(R.string.interface_domainName) + getString(R.string.interface_replyfloor);
				params.put("talkId", floorId);
				params.put("replyId_id", postId);
				params.put("replyContext", URLEncoder.encode(edt_content.getText().toString()));
			}
			params.put("isOver", "yes");
			params.put("device", "andriod");
			params.put("userId", mUserId);
			// Log.e(TAG, url + "&" + params.toString());
			mClient.post(url, params, new JsonHttpResponseHandler()
			{
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.loopj.android.http.AsyncHttpResponseHandler#onStart()
				 */
				@Override
				public void onStart()
				{
					// TODO Auto-generated method stub
					super.onStart();
					String msg;
					if (isReplyPost)
					{
						msg = "正在发布回帖，请稍候";
					} else
					{
						msg = "正在发布回复，请稍候";
					}
//					popupLoadingToast(msg);
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int,
				 * org.apache.http.Header[], org.json.JSONObject)
				 */
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response)
				{
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, headers, response);
					if (response.has("state"))
					{
						try
						{
							if (response.getString("state").equalsIgnoreCase("ok"))
							{
								if (isReplyPost)
								{
									MobclickAgent.onEvent(PostActivity.this, "replyPost_Success");
								} else
								{
									MobclickAgent.onEvent(PostActivity.this, "replyFoolr_Success");
								}
								// 恢复显示
								resumeShowPost();
								webView.loadUrl("javascript:addReplyHtml()");
								// 将变量恢复
								edt_content.setText("");
								if (picsList != null)
								{
									picsList.clear();
								}
								isPicUpload = false;
								picUrl = null;
//								cancleLodingToast(true);
								return;
							}
						} catch (Exception e)
						{
							// TODO: handle exception

						}
					}
					showMessage("发送失败，是否重试？", "重发", new OnClickListener() {
						@Override
						public void onClick(View v) {
							replyPost();
						}
					});
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
				{
					// TODO Auto-generated method stub
					super.onFailure(statusCode, headers, responseString, throwable);
					showMessage("发送失败，原因：" + throwable.getLocalizedMessage(), null, null);
				}
			});
		}
	}

	private void addPic(Intent data)
	{
		if (null != data)
		{
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			// 获取图片
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picturePath, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, 1080 * 700);
			opts.inJustDecodeBounds = false;
			final Bitmap bmp;
			try
			{
				bmp = BitmapFactory.decodeFile(picturePath, opts);
			} catch (OutOfMemoryError err)
			{
				showMessage("图片太大，请重新选择！", null, null);
				return;
			}
			if (null == picsList)
			{
				picsList = new ArrayList<Bitmap>(4);
			}
			picsList.add(bmp);

			final ImageView image = new ImageView(PostActivity.this);
			LayoutParams params = (LayoutParams) btn_pic.getLayoutParams();
			params.width = btn_pic.getWidth();
			params.height = btn_pic.getHeight();
			image.setScaleType(ScaleType.CENTER_CROP);
			image.setLayoutParams(params);
			image.setImageBitmap(bmp);
            image.setClickable(true);
            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (null != mpics && mpics.getChildCount() > 0){
                        mpics.removeView(image);
                        picsList.remove(bmp);
                        mpics.postInvalidate();
                        if (mpics.getChildCount() < 5){
                            btn_pic.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                    return false;
                }
            });
			int count = mpics.getChildCount();
			if (4 == count)
			{
				btn_pic.setVisibility(View.GONE);
			}
			mpics.addView(image, count - 1);
			mpics.postInvalidate();
		}
	}

	private void uploadPic()
	{
		String url = "http://www.mckuai.com/" + getString(R.string.interface_uploadimage);
		RequestParams params = new RequestParams();
		for (int i = 0; i < picsList.size(); i++)
		{
			String picName = "0" + i + ".jpg";
			params.put(i + "", Bitmap2IS(picsList.get(i)), picName, "image/jpeg");
		}
		mClient.post(url, params, new JsonHttpResponseHandler()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
			 */
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				super.onStart();
				showMessage("正在上传图片，请稍候...", null, null);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.loopj.android.http.JsonHttpResponseHandler#onSuccess(int,
			 * org.apache.http.Header[], org.json.JSONObject)
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				if (response.has("state"))
				{
					try
					{
						if (response.getString("state").equals("ok"))
						{
							picUrl = response.getString("msg");
							if (null != picUrl)
							{
								isPicUpload = true;
                                picsList.clear();
                                mpics.removeAllViews();
                                mpics.postInvalidate();
								replyPost();
								return;
							}
						}
					} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(PostActivity.this, "上传图片返回内容不正确！", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				Toast.makeText(PostActivity.this, "上传图片失败！", Toast.LENGTH_SHORT).show();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.loopj.android.http.JsonHttpResponseHandler#onFailure(int,
			 * org.apache.http.Header[], java.lang.String, java.lang.Throwable)
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
			{
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				Toast.makeText(PostActivity.this, "上传图片失败！原因：" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	// 将bitmap转成流，以便于上传
	private InputStream Bitmap2IS(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	// 根据登录与否以及内容的长度来确定是否满足发送回复的要求
	private boolean checkReplyInfo()
	{
		if (!mApplication.isLogin())
		{
			mHandler.sendMessageDelayed(mHandler.obtainMessage(3), 200);
			callLogin(REPLY_POST);
			return false;
		}
		mUserId = mApplication.user.getId();

		String context = edt_content.getText().toString();
		context = context.trim();
		if (0 == context.length())
		{
			Toast.makeText(PostActivity.this, "不能回复空内容哦", Toast.LENGTH_LONG).show();
			return false;
		} else if (isReplyPost)
		{
			if (2000 < context.length())
			{
				// showNotification("你输入的字太多啦，跟帖最多只能输入2000字");
				Toast.makeText(PostActivity.this, "你输入的字太多啦，跟帖最多只能输入2000字", Toast.LENGTH_LONG).show();
				return false;
			}
		} else
		{
			if (150 < context.length())
			{
				// showNotification("你输入的字太多啦，回复最多只能输入150字");
				Toast.makeText(PostActivity.this, "你输入的字太多啦，回复最多只能输入150字", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		/*UMSsoHandler ssoHandler = mShareService.getConfig().getSsoHandler(requestCode);
		if (null != ssoHandler)
		{
			Log.e(TAG, "get ssoHandler");
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			return;
		}*/
		switch (requestCode)
		{
		case LOGIN:
			if (Activity.RESULT_OK == resultCode)
			{
				replyPost();
			} else
			{
				// showNotification("未登录，不能发布帖子");
				Toast.makeText(PostActivity.this, "未登录，不能发布帖子", Toast.LENGTH_LONG).show();
			}
			break;

		case GETPIC:
			if (Activity.RESULT_OK == resultCode)
			{
				addPic(data);
			} else
			{
				// showNotification("未选择图片！");
				Toast.makeText(PostActivity.this, "未选择图片！", Toast.LENGTH_LONG).show();
			}
			break;
		case REPLY_POST:
			if (RESULT_OK == resultCode)
			{
				replyPost();
			}
			else {
				Toast.makeText(PostActivity.this, "登录后才能回复!", Toast.LENGTH_SHORT).show();
			}
			break;
		case REWARD_POST:
			if (RESULT_OK == resultCode)
			{
				rewardPost();
			}
			else {
				Toast.makeText(PostActivity.this, "登录后才能打赏!", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case COLLECT_POST:
			if (RESULT_OK == resultCode)
			{
				collectPost();
			}
			else {
				Toast.makeText(PostActivity.this, "登录后才能收藏帖子!", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	private void getParams()
	{
		String jsStr = "javascript:getParameters()";
		webView.loadUrl(jsStr);
	}

	private void onWebLoadingComplete()
	{
		if (!isShowPost && isReplyPost)
		{
			replyPost();
		}
	}

	/**
	 * 处理文本框内的内容变化时的提示
	 * 
	 * @param s
	 * @param start
	 * @param count
	 * @param after
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s)
	{
		// TODO Auto-generated method stub
		int length = edt_content.getText().length();
		boolean isshowneed = false;
		String hint = null;

		if (isReplyPost)
		{
			if (2000 - length < 100 && 2000 - length >= 0)
			{
				isshowneed = true;
				//hint = getString(R.string.spare) + (2000 - length) + getString(R.string.unit);
			} else if (2001 < length)
			{
				//hint = getString(R.string.tooMuch) + (length - 2000) + getString(R.string.unit);
			}
		} else
		{
			if (150 - length < 20 && 150 - length >= 0)
			{
				isshowneed = true;
				//hint = getString(R.string.spare) + (150 - length) + getString(R.string.unit);
			} else if (150 < length)
			{
				//hint = getString(R.string.tooMuch) + (length - 150) + getString(R.string.unit);
			}
		}
		if (isshowneed)
		{
			//tv_hint.setTextColor(getResources().getColor(R.color.font_green));
		} else
		{
			//tv_hint.setTextColor(getResources().getColor(R.color.font_red));
		}
		if (null != hint)
		{
			tv_hint.setText(hint);
			tv_hint.setVisibility(View.VISIBLE);
		} else
		{
			tv_hint.setVisibility(View.GONE);
		}

	}

	/**
	 * java接口,与web页面交互信息
	 * 
	 * @author kyly
	 * 
	 */
	public final class MyJavaScriptInterface
	{

		@JavascriptInterface
		public void setparameters(int ownerId, int isNew, int forumId, String forumName, int postId, String postTitle)
		{
			// Toast.makeText(PostActivity.this, "取值",
			// Toast.LENGTH_SHORT).show();
			PostActivity.this.ownerId = ownerId;
			PostActivity.this.isNew = isNew;
			PostActivity.this.forumId = forumId;
			PostActivity.this.forumName = forumName;
			PostActivity.this.postId = postId;
			PostActivity.this.postTitle = postTitle;
			PostActivity.this.isReplyPost = true;
			Message msg = mHandler.obtainMessage(2);
			mHandler.sendMessage(msg);
		}

		/**
		 * setparametersReply:获取回复楼层时的相关数据，web端的js将调用些<br>
		 * web端的Js将调用些函数将相应的数据传递给应用
		 * 
		 * @param floorid
		 *            获取到的楼层id
		 * @param floorOwnerId
		 *            获取到的楼拥有者id
		 * @param username
		 *            获取到的楼层拥有者用户名
		 */

		@JavascriptInterface
		public void setparametersReply(int floorid, int floorOwnerId, String username)
		{
			PostActivity.this.floorId = floorid;
			PostActivity.this.floorOwnerId = floorOwnerId;
			PostActivity.this.floorOwnerName = username;
			PostActivity.this.isReplyPost = false;
			// 由于是js调用的，只能通过消息来间接调用应用内的函数
			Message msg = mHandler.obtainMessage(1);
			mHandler.sendMessage(msg);
		}

		@JavascriptInterface
		public void setUserIdEX(int userId)
		{
			if (0 != userId)
			{

				// Toast.makeText(PostActivity.this, "userId=" + userId,
				// Toast.LENGTH_LONG).show();
				Message msg = mHandler.obtainMessage(7);
				msg.arg1 = userId;
				mHandler.sendMessage(msg);
			}
		}
	}

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:
				// Log.e(TAG, "floorId=" + floorId + ",floorOwnerId=" +
				// floorOwnerId + ",floorOwnerName=" + floorOwnerName);
				showReply();
				break;
			case 2:
				// Log.e(TAG, "ownerId=" + ownerId + ",isNew=" + isNew +
				// ",froumId=" + forumId + ",forumName=" + forumName
				// + ",postId=" + postId + ",postId=" + postTitle);
				if (!isShowPost)
				{
					replyPost();
				}
				break;
			case 3:
				//callLogin();
				break;
			case 5:
				break;

			case 6:
				addInterface();
				break;
				case 7:
					Intent intent = new Intent(PostActivity.this, UserCenterActivity.class);
					intent.putExtra(getString(R.string.usercenter_tag_userid),msg.arg1);
					startActivity(intent);
					break;

			default:
				break;
			}
			if (1 == msg.what)
			{
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						isReplyPost = false;// 是网页上调用回复楼层
						showReply();
					}
				});
			}
		}
	};

	// 加载大图时,计算缩放比例,以免出现OOM
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
	{
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8)
		{
			roundedSize = 1;
			while (roundedSize < initialSize)
			{
				roundedSize <<= 1;
			}
		} else
		{
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
	{
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));

		if (upperBound < lowerBound)
		{
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1))
		{
			return 1;
		} else if (minSideLength == -1)
		{
			return lowerBound;
		} else
		{
			return upperBound;
		}
	}

	protected void sharePost()
	{
		if (null == post)
		{
			showMessage("未获取到帖子信息，请重试！", null, null);
			return;
		} else {
			String targetUrl = "http://www.mckuai.com/thread-" + post.getId() + ".html";
			UMImage image;
			if (null != post.getMobilePic() && 10 < post.getMobilePic().length()) {
				image = new UMImage(this, post.getMobilePic());
			} else {
				image = new UMImage(this, R.mipmap.ic_share_default);
			}
			share(post.getTalkTitle(), post.getContent(), targetUrl, image);
		}
	}

	private void getPostMark()
	{
		if (mApplication.isLogin())
		{
			RequestParams params = new RequestParams();
			params.put("userId", mApplication.user.getId() + "");
			params.put("talkId", post.getId() + "");
			String url = getString(R.string.interface_domainName) + getString(R.string.interface_getpostmark);
			mClient.get(url, params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					// TODO Auto-generated method stub
					super.onSuccess(statusCode, headers, response);
					if (null != response && response.has("state")) {
						try {
							if (response.getString("state").equalsIgnoreCase("ok")) {
								String result = response.getString("msg");
								if (-1 < result.indexOf("dashang")) {
									isReward = true;
								}
								if (-1 < result.indexOf("collect")) {
									isCollect = true;
								}
								setButtonFunction();
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			});
		}
	}

	private void setButtonFunction()
	{
		if (isReward)
		{
			btn_reward.setEnabled(false);
			btn_reward.setBackgroundResource(R.drawable.btn_post_reward);
		} else
		{
			btn_reward.setBackgroundResource(R.drawable.btn_post_reward);
		}

		if (isCollect)
		{
			btn_collect.setBackgroundResource(R.drawable.btn_post_collect);
		} else
		{
			btn_collect.setBackgroundResource(R.drawable.btn_post_collect);
		}
	}

}
