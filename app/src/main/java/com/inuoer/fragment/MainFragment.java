package com.inuoer.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.CartData;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;
import com.inuoer.util.MainAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment{
	private String jsonString;
	private ListView lv;
	private MainAdapter MyAdapter;
	private ArrayList<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	private ProgressDialog mProgressDialog;
	private static final String URL_STRING = "urlString";
	//接受来自MainActivity的urlString
	private String urlString;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				MyAdapter.notifyDataSetChanged();
				mProgressDialog.dismiss();
			} else if (msg.what == 0x124) {
				Toast.makeText(getActivity(), "请查看网络连接", Toast.LENGTH_LONG)
						.show();
			}

		}
	};


	public MainFragment() {

	}

	/**
	 * 创建MainFragment时传入不同的url参数来加载不同的页面
	 * @param urlString
	 * @return
	 */
	public static MainFragment newInstance(String urlString){
		MainFragment mainFragment = new MainFragment();
		Bundle bundle = new Bundle();
		bundle.putString(URL_STRING, urlString);
		mainFragment.setArguments(bundle);
		return mainFragment;
	}
	/**
	 * 加载网络数据
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null){
			urlString = getArguments().getString(URL_STRING);
		}
		initProgressDialog();
		initData();
		MyAdapter = new MainAdapter(getActivity(), listItem);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		lv = new ListView(getActivity());
		lv.setAdapter(MyAdapter);
		return lv;
	}

	private void initProgressDialog() {
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage("正在加载中...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	/**
	 * 加载网络数据
	 */
	public void initData() {
		new Thread(new Runnable() {
			public void run() {
				try {
					jsonString = HttpUtil
							.getGetJsonContent(urlString);

					JSONArray jsonArr = JSON.parseArray(jsonString);
					for (int i = 0; i < jsonArr.size(); i++) {
						// 获取每一个JsonObject对象
						JSONObject myjObject = jsonArr.getJSONObject(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", i);
						map.put("image", Config.API_UPLOADS+myjObject.getString("image"));
						map.put("name", myjObject.getString("name"));
						map.put("price", myjObject.getString("price"));
						map.put("num", CartData.findCart(i));
						listItem.add(map);
					}
					handler.sendEmptyMessage(0x123);
				} catch (Exception e) {
					handler.sendEmptyMessage(0x124);
				}

			}
		}).start();
	}

	public ArrayList<Map<String, Object>> getListItem() {
		return listItem;
	}
}
