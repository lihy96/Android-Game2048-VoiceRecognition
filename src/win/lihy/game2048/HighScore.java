package win.lihy.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class HighScore {
	private Context context;
	AlertDialog.Builder builder;

	public HighScore(Context context) {
		this.context = context;
	}

	public void show() {
		// 显示基本的AlertDialog
		builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.android_icon);
		builder.setTitle("最高分");
		builder.setMessage("您的最高分是： "+ ((WelcomePage) context).getBestScore());
		builder.setPositiveButton("清零",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((WelcomePage) context).saveBestScore(0);
					}
				});
		builder.setNeutralButton("返回",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//不用进行任何操作
					}
				});
		builder.show();
	}
}
