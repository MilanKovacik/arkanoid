package milan.arkanoid;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Block extends RectF{

	private Paint paint;
	private int hard;
	
	public Block(float left, float top, float right, float bottom, int hard){
		super(left, top, right, bottom);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
	    setHard(hard);
	}
	
	public int getHard() {
		return hard;
	}

	public void setHard(int hard) {
		this.hard = hard;
		setColor();
	}

	public int decHard(){
		this.hard--;
		setColor();
		return hard;
	}
	
	public Paint getPaint() {
		return paint;
	}

	public void setPaint(int color) {
		this.paint.setColor(color);
	}

	private void setColor() {
		switch (hard) {
		case 1: setPaint(Color.LTGRAY);
		break;
		case 2: setPaint(Color.GRAY);
		break;
		case 3: setPaint(Color.DKGRAY);
		break;
		case 4: setPaint(Color.BLACK);
		break;

		default:
			setPaint(Color.RED);
			break;			
		}
		
	}
}
