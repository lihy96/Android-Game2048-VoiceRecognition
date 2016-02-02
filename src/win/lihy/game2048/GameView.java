package win.lihy.game2048;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import win.lihy.game2048.Card;
import win.lihy.game2048.Config;

import win.lihy.game2048.MainActivity;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class GameView extends LinearLayout {

	
	private static final int edge = 10;
	private Card[][] cardMap = new Card[Config.LINES][Config.LINES];
	private List<Point> emptyPoints = new ArrayList<Point>();
	private Context m_context;
	private MyAudio myAudio;
	public static GameView m_gameView;
	
	public static GameView getGameView(){
		return m_gameView;
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAudio(context);
		initGameView();
		m_gameView = this;
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAudio(context);
		initGameView();
		m_gameView = this;
	}

	public GameView(Context context) {
		super(context);
		initAudio(context);
		initGameView();
		m_gameView = this;
	}

	private void initAudio(Context context) {
		m_context = context;
		myAudio = new MyAudio(m_context);
	}
	
	private void initGameView() {
		

		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(0xffbbada0);// 设置背景颜色的方法
		
		setOnTouchListener(new View.OnTouchListener() {
			private float startX, startY, offsetX, offsetY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();
					startY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX() - startX;
					offsetY = event.getY() - startY;
					if (Math.abs(offsetX) > Math.abs(offsetY)) {
						if (offsetX < -5) {
							swipeLeft();
						} else if (offsetX > 5) {
							swipeRight();
						}
					} else {
						if (offsetY < -5) {
							swipeUp();
						} else if (offsetY > 5) {
							swipeDown();
						}
					}

					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		Config.CARD_WIDTH = (Math.min(w, h) - edge) / Config.LINES;
		addCards(Config.CARD_WIDTH, Config.CARD_WIDTH);
		startGame();
	}

	private void addCards(int cardWidth, int cardHeight) {

		Card c;

		LinearLayout line;
		LinearLayout.LayoutParams lineLp;
		
		for (int y = 0; y < Config.LINES; y++) {
			line = new LinearLayout(getContext());
			lineLp = new LinearLayout.LayoutParams(-1, cardHeight);
			addView(line, lineLp);
			
			for (int x = 0; x < Config.LINES; x++) {
				c = new Card(getContext());
				line.addView(c, cardWidth, cardHeight);

				cardMap[x][y] = c;
			}
		}
	}

	// 找到当前有多少张卡片
	private int findCardNumber() {
		int cnt = 0;
		for (int x = 0; x < Config.LINES; x++) {
			for (int y = 0; y < Config.LINES; y++) {
				if(cardMap[x][y].getNum() > 0){
					cnt ++;
				}
			}
		}
		return cnt;
	}

	private void addRandomNum() {
		emptyPoints.clear();
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardMap[x][y].getNum() <= 0) {// 如果某个位置上面还没有卡片
					emptyPoints.add(new Point(x, y));
				}
			}
		}

		if(emptyPoints.size() > 0){
			Point p = emptyPoints
					.remove((int) (Math.random() * emptyPoints.size()));
			cardMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
			MainActivity.getMainActivity().getMyAnimation().createScaleTo1(cardMap[p.x][p.y]);
		}

	}

	public void startGame() {
		
		MainActivity atyActivity = MainActivity.getMainActivity();
		atyActivity.clearScore();//显示初始分数0
		atyActivity.showHighScore();//显示此时最高分

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				cardMap[x][y].setNum(0);
			}
		}

		addRandomNum();
		addRandomNum();

	}

	// 检查是否已经游戏结束
	private void checkComplete() {

		boolean complete = true;

		ALL: for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardMap[x][y].getNum() == 0
						|| (x > 0 && cardMap[x][y].equals(cardMap[x - 1][y]))
						|| (x < Config.LINES - 1 && cardMap[x][y]
								.equals(cardMap[x + 1][y]))
						|| (y > 0 && cardMap[x][y].equals(cardMap[x][y - 1]))
						|| (y < Config.LINES - 1 && cardMap[x][y]
								.equals(cardMap[x][y + 1]))) {

					complete = false;
					break ALL;
				}
			}
		}

		if (complete) {
			new AlertDialog.Builder(getContext())
					.setTitle("2048")
					.setMessage("游戏结束")
					.setPositiveButton("重新开始",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startGame();
								}
							}).show();
		}

	}

	public void swipeLeft() {

		boolean merge = false;
		int oldCardNumber = findCardNumber();
		
		
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				for (int x1 = x + 1; x1 < Config.LINES; x1++) {
					if (cardMap[x1][y].getNum() > 0) {
						if (cardMap[x][y].getNum() <= 0) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x1][y],cardMap[x][y], x1, x, y, y);
							
							cardMap[x][y].setNum(cardMap[x1][y].getNum());
							cardMap[x1][y].setNum(0);
							x--;

							merge = true;

						} else if (cardMap[x1][y].equals(cardMap[x][y])) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);
							
							cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
							cardMap[x1][y].setNum(0);

							MainActivity.getMainActivity().addScore(
									cardMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}

		}
		//判断是否有数字的合并，从而确定应该播放1（merge），2（move）音乐
		if(oldCardNumber == findCardNumber()){
			myAudio.play(2);
		}else {
			myAudio.play(1);
		}
		
		if (merge) {
			addRandomNum();
			checkComplete();
		}

	}

	public void swipeRight() {
		boolean merge = false;
		int oldCardNumber = findCardNumber();
		
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = Config.LINES - 1; x >= 0; x--) {
				for (int x1 = x - 1; x1 >= 0; x1--) {
					if (cardMap[x1][y].getNum() > 0) {
						if (cardMap[x][y].getNum() <= 0) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);
							
							cardMap[x][y].setNum(cardMap[x1][y].getNum());
							cardMap[x1][y].setNum(0);
							x++;
							merge = true;
						} else if (cardMap[x1][y].equals(cardMap[x][y])) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);
							
							cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
							cardMap[x1][y].setNum(0);

							MainActivity.getMainActivity().addScore(
									cardMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}

		}
		
		//判断是否有数字的合并，从而确定应该播放1（merge），2（move）音乐
		if(oldCardNumber == findCardNumber()){
			myAudio.play(2);
		}else {
			myAudio.play(1);
		}
		
		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}

	public void swipeUp() {
		boolean merge = false;
		int oldCardNumber = findCardNumber();

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = 0; y < Config.LINES; y++) {
				for (int y1 = y + 1; y1 < Config.LINES; y1++) {
					if (cardMap[x][y1].getNum() > 0) {
						if (cardMap[x][y].getNum() <= 0) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
							
							cardMap[x][y].setNum(cardMap[x][y1].getNum());
							cardMap[x][y1].setNum(0);
							y--;
							merge = true;
						} else if (cardMap[x][y1].equals(cardMap[x][y])) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
							
							cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
							cardMap[x][y1].setNum(0);

							MainActivity.getMainActivity().addScore(
									cardMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}

		}
		
		
		//判断是否有数字的合并，从而确定应该播放1（merge），2（move）音乐
		if(oldCardNumber == findCardNumber()){
			myAudio.play(2);
		}else {
			myAudio.play(1);
		}
		
		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}

	public void swipeDown() {
		boolean merge = false;
		int oldCardNumber = findCardNumber();

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = Config.LINES - 1; y >= 0; y--) {
				for (int y1 = y - 1; y1 >= 0; y1--) {
					if (cardMap[x][y1].getNum() > 0) {
						if (cardMap[x][y].getNum() <= 0) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
							
							cardMap[x][y].setNum(cardMap[x][y1].getNum());
							cardMap[x][y1].setNum(0);
							y++;
							merge = true;
						} else if (cardMap[x][y1].equals(cardMap[x][y])) {
							
							MainActivity.getMainActivity().getMyAnimation().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
							
							cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
							cardMap[x][y1].setNum(0);

							MainActivity.getMainActivity().addScore(
									cardMap[x][y].getNum());
							merge = true;
						}
						break;
					}
				}
			}

		}
		
		
		//判断是否有数字的合并，从而确定应该播放1（merge），2（move）音乐
		if(oldCardNumber == findCardNumber()){
			myAudio.play(2);
		}else {
			myAudio.play(1);
		}
		
		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	
}
