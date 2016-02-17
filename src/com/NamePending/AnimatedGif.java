package com.NamePending;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class AnimatedGif extends Canvas {
	private final ImageLoader loader = new ImageLoader();
	private int img = 0;
	private volatile boolean animating = false;
	private Thread animateThread;

	public AnimatedGif(Composite parent, int style) {
		super(parent, style);
	}

	public void load (InputStream resource) throws IOException {
		loader.load(resource);
	}

	public void animate() {
		if (animateThread == null) {
			animateThread = createThread();
			animateThread.setDaemon(true);
		}

		if (animateThread.isAlive())
			return;

		animateThread.start();
	}

	public void stop() {
		animating = false;
		if (animateThread != null)
			try {
				animateThread.join();
				animateThread = null;
			} catch (InterruptedException e) {
				// do nothing
			}
	}

	private Thread createThread() {
		return new Thread() {
			long currentTime = System.currentTimeMillis();
			final Display display = getParent().getDisplay();
			public void run() {
				animating = true;
				while(animating) {
					img = (img == loader.data.length-1) ? 0 : img + 1;
					int delayTime = Math.max(50, 10*loader.data[img].delayTime);
					long now = System.currentTimeMillis();
					long ms = Math.max(currentTime + delayTime - now, 5);
					currentTime += delayTime;
					try {
						Thread.sleep(ms);
					} catch(Exception e) {
						return;
					}

					if (!display.isDisposed())
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								ImageData nextFrameData = loader.data[img];
								Image frameImage = new Image(display, nextFrameData);
								new GC(AnimatedGif.this).drawImage(frameImage, nextFrameData.x, nextFrameData.y);
								frameImage.dispose();
								//canvas.redraw();
							}
						});
				}

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						new GC(AnimatedGif.this).fillRectangle(
								0,
								0,
								getBounds().width,
								getBounds().height);
					}
				});
			}
		};
	}
}