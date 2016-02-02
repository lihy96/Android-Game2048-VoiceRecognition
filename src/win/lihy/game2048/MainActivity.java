package win.lihy.game2048;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import android.R.string;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private TextView tvScore, tvHighScore,tvLink;
	private GameView gameView;
	private Button btnRestart;
	private static MainActivity mainActivity = null;
	private int score = 0;// 计分器
	private MyAnimation myAnimation = null;
	private LinearLayout linearLayout;
	private SpeechRecognizer mIat;
	private ToggleButton toggleBtnRecognize;
	private boolean canRecognize = false;

	public MainActivity() {
		mainActivity = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SpeechUtility.createUtility(this, "appid=" + "56aea292");
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);

		linearLayout = (LinearLayout) findViewById(R.id.container);
		linearLayout.setBackgroundColor(0xffbbada0);

		tvScore = (TextView) findViewById(R.id.tvScore);
		myAnimation = (MyAnimation) findViewById(R.id.myAnimation);
		tvHighScore = (TextView) findViewById(R.id.tvHighScore);
		gameView = (GameView) findViewById(R.id.gameView);
		tvLink = (TextView) findViewById(R.id.linkWeb);
		tvLink.setMovementMethod(LinkMovementMethod.getInstance());
        Spanned text = Html.fromHtml("<a href=\"http://www.lihy.win/2048/\">玩法说明</a>");
        tvLink.setText(text);

		
		btnRestart = (Button) findViewById(R.id.btnRestart);

		btnRestart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gameView.startGame();

			}
		});

		toggleBtnRecognize = (ToggleButton) findViewById(R.id.toggleBtnRecognize);
		toggleBtnRecognize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startVoiceRecognize();
				if (toggleBtnRecognize.isChecked()) {
					canRecognize = true;
				} else {
					canRecognize = false;
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static MainActivity getMainActivity() {
		return mainActivity;
	}

	public void clearScore() {
		score = 0;
		showScore();
	}

	public void showScore() {
		tvScore.setText(score + "");
	}

	public void showHighScore() {
		tvHighScore.setText(WelcomePage.getWelcomePage().getBestScore() + "");
	}

	public void addScore(int s) {
		score += s;
		showScore();
		int highScore = Math.max(WelcomePage.getWelcomePage().getBestScore(),
				score);
		WelcomePage.getWelcomePage().saveBestScore(highScore);
		showHighScore();
	}

	public MyAnimation getMyAnimation() {
		return myAnimation;
	}

	// 语音识别部分
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d("shitou", "SpeechRecognizer init() code = " + code);
		}
	};

	public void setParam() {
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		// mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
	}



	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			Log.d("shitou", "kaishi");
		}

		@Override
		public void onError(SpeechError error) {
			Log.d("shitou", error + "");
		}

		@Override
		public void onEndOfSpeech() {
			Log.d("shitou", "jieshu");
			startVoiceRecognize();

		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d("shitou", results.getResultString());
			if (canRecognize == true) {

				Log.d("shitou", results.getResultString());
				JSONObject root;
				try {
					root = new JSONObject(results.getResultString());
					String res = root.getJSONArray("ws").getJSONObject(0)
							.getJSONArray("cw").getJSONObject(0).getString("w");
					Log.d("shitou", res);
					char ch = res.substring(0, 1).toCharArray()[0];
					// int tmp = which(res);
					// Log.d("shitou", "(" + ch + " " + tmp + " res: " + res);
					if (canRecognize) {
						if (ch == 'U' || ch == 'u') {
							gameView.swipeUp();
						} else if (ch == 'D' || ch == 'd') {
							gameView.swipeDown();
						} else if (ch == 'L' || ch == 'l') {
							gameView.swipeLeft();
						} else if (ch == 'R' || ch == 'r') {
							gameView.swipeRight();
						} else {
							startVoiceRecognize();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub

		}

	};

	void startVoiceRecognize() {

		setParam();
		int ret = mIat.startListening(recognizerListener);
		Log.d("shitou", "startListening ret:" + ret);

	}

	@Override
	protected void onDestroy() {
		canRecognize = false;
		super.onDestroy();
	}

}
