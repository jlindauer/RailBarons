package edu.cs307.railbarons;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	private Board _board;
	private SurfaceHolder _surfaceHolder;
	private boolean _running;
	public static int currentFrame;
	public static int framesLost;
	
	public GameThread(Board board, SurfaceHolder sHolder) {
		_board = board;
		_surfaceHolder = sHolder;
		_running = false;
		GameThread.currentFrame = 0;
		GameThread.framesLost = 0;
	}
	
	public void setRunning(boolean running) {
		_running = running;
	}
	
	@SuppressLint("WrongCall")
	public void run() {
		Canvas c = null;
		while(_running) {
			GameThread.currentFrame += 1;
			c = null;
            try {
                c = _surfaceHolder.lockCanvas();
                synchronized (_surfaceHolder) {
                    _board.onDraw(c);
                }
                Thread.sleep(15);
            } catch(Exception e) {
            	GameThread.framesLost += 1;
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
		}
	}
}
