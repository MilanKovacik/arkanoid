package milan.arkanoid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import milan.arkanoid.R;


public class MainActivity extends ActionBarActivity {

	private Button button;
	private TextView title;
	private List<Block> blocks;
	private MyView drawView;
	AtomicBoolean run = new AtomicBoolean();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		button = (Button) findViewById(R.id.button1);
		title = (TextView) findViewById(R.id.textView1);
		blocks = new ArrayList<Block>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				button.setVisibility(View.INVISIBLE);
				title.setVisibility(View.INVISIBLE);
				generateGame();
			}
	    });
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void generateGame(){
		drawView = new MyView(this);
        setContentView(drawView);
	}
	
	private void endGame(){
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Your score is: " + drawView.getScore());

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.inicializedGame();
				dialog.dismiss();
			}
		});
		
		Button exitButton = (Button) dialog.findViewById(R.id.dialogButtonExit);
		// if button is clicked, close the custom dialog
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		dialog.show();
	}
	
	public class MyView extends View {
		private int width, height, lastX, x, y, moveX, moveY;
		private RectF panel;
		private int move = 0;
		private Thread thread;
		private int score;
			
        @SuppressWarnings("deprecation")
		public MyView( Context context) {
             super(context);
             
             Display display = getWindowManager().getDefaultDisplay();
             width = display.getWidth();
             height = display.getHeight();
          
             inicializedGame();
        }
        
        public void start(){
        	thread.start();
        }
	
		public void inicializedGame() {
			blocks.clear();
			setScore(0);
			x = width/3;
		    y = height/2;
		    moveX = -1;
		    moveY = -1;
		    run.set(true);
		    panel = new RectF(0, height-95, 100, height-80 );
		    int top = 2;
		    int left = 2;
		    int hard = 4;
		    int widthBlock = (width - 14) / 6;
		    for(int i = 0; i < 4; i++){
		          	for(int j = 0; j < 6; j++){
		          		blocks.add(new Block(left, top, left + widthBlock, top + 15, hard));
		            	left += widthBlock + 2; 
		            }	
		            hard--;
		            top += 18;
		            left = 2;
		    }	      
		    thread = new Thread( new Runnable() {     
				public void run() {     
					try {
						while(run.get()){
			   				Thread.sleep(2); 
			   				postInvalidate();
			   				runOnUiThread(new Runnable() { 
			   					public void run() {
			   						moveBall();
			                        movePanel();
			      					}
			      				});
			   				}
			  			} catch (InterruptedException e) {
			   				e.printStackTrace();
			   			}
			   		}            
			   	});
		     start();
		}
				
		public void moveBall() {
			checkCollision();
			x += moveX;
			y += moveY;				
			}

		private void checkCollision() {
			if(x-3 <= 0){
				moveX = 1;
			}else if(x+3 >= width){
				moveX = -1;
			}else if(y-3 <= 0){
				moveY = 1;
			}else if(y+3 >= height-80){
				end();
			}
			checkCollisionBlocks();
			checkCollisionPanel();
		}
			
		private void checkCollisionBlocks() {
			int size = blocks.size();
			if(size == 0){
				end();
			}
			for(int i = 0; i < size; i++){
				if((int)blocks.get(i).bottom <= y-2 && (int)blocks.get(i).bottom >= y-4 && (int)blocks.get(i).left <= x && (int)blocks.get(i).right >= x ){
					if(blocks.get(i).decHard() == 0){
						blocks.remove(i--);
						size--;
						setScore(getScore() + 1);;
					}
					moveY *=-1;
				}
				if((int)blocks.get(i).bottom >= y && (int)blocks.get(i).top <= y && (int)blocks.get(i).left <= x+3 && (int)blocks.get(i).right >= x-3 ){
					if(blocks.get(i).decHard() == 0){
						blocks.remove(i--);
						size--;
						setScore(getScore() + 1);
					}
					moveX *=-1;
			}
			if(i<0){
				i = 0;
			}
		}
		}

		public void end() {
			if(run.get()){
		       	endGame();
		       }
		       run.set(false);					
		   }

		private void checkCollisionPanel() {
			if(y+3 == (int)panel.top){
				if(x >= panel.left && x <= panel.right){
					if(x < panel.centerX()){
						moveX = -1;
					}else{
						moveX = 1;
					}
					moveY = -1;
				}
			}
		}

		public void movePanel() {
			int midPanel = (int)panel.left+50;
            if(!(lastX >= midPanel-5 && lastX <= midPanel+5)) {
              	 panel.set(panel.left+move , panel.top, panel.right + move, panel.bottom);
            }
		}
               
        @SuppressLint("ClickableViewAccessibility")
		@Override
        public boolean onTouchEvent(MotionEvent event) {
        	lastX = (int)event.getX();
        	int midPanel = (int)panel.left+50;
        	switch (event.getAction()) {
        	case MotionEvent.ACTION_UP:
	    		move = 0;
	    		break;
        	case MotionEvent.ACTION_MOVE:
        		if(event.getX() > midPanel){
            		move = +2;
            	}else{
            		move = -2;
            	}
        		break;
	        case MotionEvent.ACTION_DOWN:
	    		if(event.getX() > midPanel){
	        		move = +2;
	        	}else{
	        		move = -2;
	        	}
	    		break;
	    	default:
	    		break;
        	}
        	
			return true;
			}

        @SuppressLint("DrawAllocation")
		@Override
        public void onDraw(Canvas canvas) {
           super.onDraw(canvas);
           Paint paint = new Paint();
           paint.setStyle(Paint.Style.FILL);
           paint.setColor(Color.WHITE);
           canvas.drawPaint(paint);
           paint.setColor(Color.rgb(10, 150, 10));
           canvas.drawRect(panel, paint);
           for(Block b: blocks){
        	   canvas.drawRect(b, b.getPaint());
           }
           paint.setColor(Color.rgb(150, 10, 10));
           canvas.drawCircle(x, y, 6, paint);
       }

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}
    }
}
