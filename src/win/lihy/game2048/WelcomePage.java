package win.lihy.game2048;

import java.util.ArrayList;
import java.util.List;

import win.lihy.game2048.MainActivity;


import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomePage extends Activity {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private Button btnStart,btnHighScore,btnAbout,btnQuit;
	private MyAudio myAudio;
	private Share share;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myAudio = new MyAudio(this);
		share = new Share(this);
		
		setContentView(R.layout.welcome_page);
		
		initViews();
		
		handler.sendEmptyMessageDelayed(0, Config.WELCOME_PIC_LENGTH);
		handler_audio.sendEmptyMessageDelayed(0, 500);
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.one, null));
		views.add(inflater.inflate(R.layout.two, null));

		vpAdapter = new ViewPagerAdapter(views, this);
		vp = (ViewPager) findViewById(R.id.viewPager);
		vp.setAdapter(vpAdapter);

		btnStart = (Button) views.get(1).findViewById(R.id.btnStart);
		btnHighScore = (Button) views.get(1).findViewById(R.id.btnHighScore);
		btnAbout = (Button) views.get(1).findViewById(R.id.btnAbout);
		btnQuit = (Button) views.get(1).findViewById(R.id.btnQuit);
		
		
		btnStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(WelcomePage.this, MainActivity.class));
				myAudio.stop(3);
			}
		});
		
		btnHighScore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				HighScore highScoreDialogHighScore = new HighScore(getWelcomePage());
				highScoreDialogHighScore.show();
			}
		});
		
		btnAbout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				share.showShare();
			}
		});
		
		btnQuit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		
	}

	private boolean isrunning = true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 让ViewPager滑到下一页
			int now = vp.getCurrentItem();
			if(now == 0){
				vp.setCurrentItem(now + 1);
			}else {
				isrunning = false;
			}
			// 延时，循环调用handler
			if (isrunning) {
				handler.sendEmptyMessageDelayed(0, Config.WELCOME_PIC_LENGTH);
			}
		};
	};
	
	private Handler handler_audio = new Handler() {
		public void handleMessage(android.os.Message msg) {
					myAudio.play(3);
					handler_audio.sendEmptyMessageDelayed(0, Config.BACKGROUND_MUSIC_LENGTH_MS);		
		};
	};

	protected void onDestroy() {
		myAudio.stop(3);
		super.onDestroy();
	}
	
	//*************************************************************
	//方便子对象访问本页面
	private static WelcomePage welcomePage = null;
	public static WelcomePage getWelcomePage() {
		return welcomePage;
	}
	public WelcomePage() {
		welcomePage = this;
	}
	
	//****************************************************************
	//最高分
	public static final String SP_KEY_BEST_SCORE = "bestScore";
	public void saveBestScore(int s){
		Editor e = getPreferences(MODE_PRIVATE).edit();
		e.putInt(SP_KEY_BEST_SCORE, s);
		e.commit();
	}
	public int getBestScore(){
		return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
	}
}
