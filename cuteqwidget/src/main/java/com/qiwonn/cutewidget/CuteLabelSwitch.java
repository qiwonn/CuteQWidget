package com.qiwonn.cutewidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ValueAnimator;

import com.qiwonn.cutewidget.R;

/**
 *	可爱的开关控件
 *	author: 王琦 2021/12/20 星期三
 */

public class CuteLabelSwitch extends View
{
	protected int width;
    protected int height;

	private int padding;

    private int colorOn;
    private int colorOff;
    private int colorBorder;
    private int colorDisabled;

	private int colorThumb;
	private int colorTextOn;
	private int colorTextOff;

    private int textSize;

    private int outerRadii;
    private int thumbRadii;

    private Paint paint;

    private long startTime;

    private String labelOn;
    private String labelOff;

    private RectF thumbBounds;

    private RectF leftBgArc;
    private RectF rightBgArc;

    private RectF leftFgArc;
    private RectF rightFgArc;

    private Typeface typeface;

    private float thumbOnCenterX;
    private float thumbOffCenterX;

    protected boolean isOn;

    protected boolean enabled;

    protected OnToggledListener onToggledListener;

    public CuteLabelSwitch(Context context) {
        super(context);
		initView();
    }

    public CuteLabelSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
		initView();
        initProperties(attrs);
    }

    public CuteLabelSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
		initView();
        initProperties(attrs);
    }

	private void initView() {
        this.isOn = false;
        this.labelOn = "开";
        this.labelOff = "关";

        this.enabled = true;
        this.textSize = (int)(12f * getResources().getDisplayMetrics().scaledDensity);

        colorBorder = colorOn = getResources().getColor(R.color.colorA);

        paint = new Paint();
        paint.setAntiAlias(true);

        leftBgArc = new RectF();
        rightBgArc = new RectF();

        leftFgArc = new RectF();
        rightFgArc = new RectF();
        thumbBounds = new RectF();

        this.colorOff = Color.parseColor("#FFFFFF");
        this.colorDisabled = Color.parseColor("#D3D3D3");
    }

    private void initProperties(AttributeSet attrs) {
        TypedArray tarr = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CuteToggle,0,0);
        final int N = tarr.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = tarr.getIndex(i);
            if (attr == R.styleable.CuteToggle_on) {
                isOn = tarr.getBoolean(R.styleable.CuteToggle_on, false);
            } else if (attr == R.styleable.CuteToggle_colorOff) {
                colorOff = tarr.getColor(R.styleable.CuteToggle_colorOff, Color.parseColor("#FFFFFF"));
            } else if (attr == R.styleable.CuteToggle_colorBorder) {
                int accentColor = R.color.colorA;
                colorBorder = tarr.getColor(R.styleable.CuteToggle_colorBorder, accentColor);
            } else if (attr == R.styleable.CuteToggle_colorOn) {
                int accentColor = R.color.colorA;
                colorOn = tarr.getColor(R.styleable.CuteToggle_colorOn, accentColor);
            } else if (attr == R.styleable.CuteToggle_colorThumb) {
				colorThumb = tarr.getColor(R.styleable.CuteToggle_colorThumb, Color.parseColor("#FFFFFF"));
			} else if (attr == R.styleable.CuteToggle_colorTextOn) {
				colorTextOn = tarr.getColor(R.styleable.CuteToggle_colorTextOn, Color.parseColor("#000000"));
			} else if (attr == R.styleable.CuteToggle_colorTextOff) {
				colorTextOff = tarr.getColor(R.styleable.CuteToggle_colorTextOff, Color.parseColor("#FFFFFF"));
			} else if (attr == R.styleable.CuteToggle_colorDisabled) {
                colorDisabled = tarr.getColor(R.styleable.CuteToggle_colorOff, Color.parseColor("#D3D3D3"));
            } else if (attr == R.styleable.CuteToggle_textOff) {
                labelOff = tarr.getString(R.styleable.CuteToggle_textOff);
            } else if (attr == R.styleable.CuteToggle_textOn) {
                labelOn = tarr.getString(R.styleable.CuteToggle_textOn);
            } else if (attr == R.styleable.CuteToggle_android_textSize) {
                int defaultTextSize = (int)(12f * getResources().getDisplayMetrics().scaledDensity);
                textSize = tarr.getDimensionPixelSize(R.styleable.CuteToggle_android_textSize, defaultTextSize);
            } else if(attr == R.styleable.CuteToggle_android_enabled) {
                enabled = tarr.getBoolean(R.styleable.CuteToggle_android_enabled, false);
            }
        }
    }

    @Override public boolean isEnabled() {
        return enabled;
    }

    @Override public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setOnToggledListener(OnToggledListener onToggledListener) {
        this.onToggledListener = onToggledListener;
    }

	@Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setTextSize(textSize);

//      Drawing Switch background here
        {
            paint.setColor(colorOff);
			int pd1 = padding / 10;
            canvas.drawArc(leftFgArc, 90, 180, false, paint);
            canvas.drawArc(rightFgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, pd1, (width - outerRadii), height - pd1, paint);

            int alpha = (int) (((thumbBounds.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int onColor;

            if(isEnabled()) {
                onColor = Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn));
            } else {
                onColor = Color.argb(alpha, Color.red(colorDisabled), Color.green(colorDisabled), Color.blue(colorDisabled));
            }
            paint.setColor(onColor);

            canvas.drawArc(leftBgArc, 90, 180, false, paint);
            canvas.drawArc(rightBgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, 0, (width - outerRadii), height, paint);

            alpha = (int) (((thumbOnCenterX - thumbBounds.centerX()) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int offColor = Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff));
            paint.setColor(offColor);

            canvas.drawArc(leftFgArc, 90, 180, false, paint);
            canvas.drawArc(rightFgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, pd1, (width - outerRadii), height - pd1, paint);
        }

//      Drawing Switch Labels here
        String MAX_CHAR = "N";
        float textCenter = paint.measureText(MAX_CHAR) / 2;
        if(isOn) {
            int alpha = (int)((((width >>> 1) - thumbBounds.centerX()) / ((width >>> 1) - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int onColor = Color.argb(alpha, Color.red(colorTextOn), Color.green(colorTextOn), Color.blue(colorTextOn));
            paint.setColor(onColor);

            float centerX = (width - padding - ((padding + (padding >>> 1)) + (thumbRadii << 1))) >>> 1;
            canvas.drawText(labelOff, (padding + (padding >>> 1)) + (thumbRadii << 1) + centerX - (paint.measureText(labelOff) / 2), (height >>> 1) + textCenter, paint);

            alpha = (int)(((thumbBounds.centerX() - (width >>> 1)) / (thumbOnCenterX - (width >>> 1))) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int offColor = Color.argb(alpha, Color.red(colorTextOn), Color.green(colorTextOn), Color.blue(colorTextOn));
            paint.setColor(offColor);

            int maxSize = width - (padding << 1) - (thumbRadii << 1);

            centerX = (((padding >>> 1) + maxSize) - padding) >>> 1;
            canvas.drawText(labelOn, padding + centerX - (paint.measureText(labelOn) / 2), (height >>> 1) + textCenter, paint);
        } else {
            int alpha = (int)(((thumbBounds.centerX() - (width >>> 1)) / (thumbOnCenterX - (width >>> 1))) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int offColor = Color.argb(alpha, Color.red(colorTextOff), Color.green(colorTextOff), Color.blue(colorTextOff));
            paint.setColor(offColor);

            int maxSize = width - (padding << 1) - (thumbRadii << 1);
            float centerX = (((padding >>> 1) + maxSize) - padding) >>> 1;
            canvas.drawText(labelOn, padding + centerX - (paint.measureText(labelOn) / 2), (height >>> 1) + textCenter, paint);

            alpha = (int)((((width >>> 1) - thumbBounds.centerX()) / ((width >>> 1) - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (alpha > 255 ? 255 : alpha));
            int onColor;
            if(isEnabled()) {
                onColor = Color.argb(alpha, Color.red(colorTextOff), Color.green(colorTextOff), Color.blue(colorTextOff));
            } else {
                onColor = Color.argb(alpha, Color.red(colorDisabled), Color.green(colorDisabled), Color.blue(colorDisabled));
            }
            paint.setColor(onColor);

            centerX = (width - padding - ((padding + (padding >>> 1)) + (thumbRadii << 1))) >>> 1;
            canvas.drawText(labelOff, (padding + (padding >>> 1)) + (thumbRadii << 1) + centerX - (paint.measureText(labelOff) / 2), (height >>> 1) + textCenter, paint);
        }

//      Drawing Switch Thumb here
        {
            int alpha = 255;
            int offColor = Color.argb(alpha, Color.red(colorThumb), Color.green(colorThumb), Color.blue(colorThumb));
            paint.setColor(offColor);

            canvas.drawCircle(thumbBounds.centerX(), thumbBounds.centerY(), thumbRadii, paint);

            int onColor;
            if(isEnabled()) {
                onColor = Color.argb(alpha, Color.red(colorThumb), Color.green(colorThumb), Color.blue(colorThumb));
            } else {
                onColor = Color.argb(alpha, Color.red(colorDisabled), Color.green(colorDisabled), Color.blue(colorDisabled));
            }
            paint.setColor(onColor);
            canvas.drawCircle(thumbBounds.centerX(), thumbBounds.centerY(), thumbRadii, paint);
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth  = 106;//getResources().getDimensionPixelSize(R.dimen.labeled_default_width);
        int desiredHeight = 64;//getResources().getDimensionPixelSize(R.dimen.labeled_default_height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);

        outerRadii = Math.min(width, height) >>> 1;
        thumbRadii = (int) (Math.min(width, height) / (2.2f));
        padding = (height - (thumbRadii<<1)) >>> 1;

		int wp = width - padding;
		int hp = height - padding;

        thumbBounds.set(wp - (thumbRadii<<1), padding, wp, hp);
        thumbOnCenterX = thumbBounds.centerX();

        thumbBounds.set(padding, padding, padding + (thumbRadii<<1), hp);
        thumbOffCenterX = thumbBounds.centerX();

        if(isOn) {
            thumbBounds.set(wp - thumbRadii, padding, wp, hp);
        } else {
            thumbBounds.set(padding, padding, padding + (thumbRadii<<1), hp);
        }

        leftBgArc.set(0,0, outerRadii << 1, height);
        rightBgArc.set(width - (outerRadii << 1),0, width, height);

		int pd1 = padding / 10;
        leftFgArc.set(pd1,pd1, (outerRadii << 1)- pd1, height - pd1);
        rightFgArc.set(width - (outerRadii << 1) + pd1,pd1, width - pd1, height - pd1);
    }

    /**
     * Call this view's OnClickListener, if it is defined.  Performs all normal
     * actions associated with clicking: reporting accessibility event, playing
     * a sound, etc.
     *
     * @return True there was an assigned OnClickListener that was called, false
     *         otherwise is returned.
     */
    @Override public final boolean performClick() {
        super.performClick();
        if (isOn) {
            ValueAnimator switchColor = ValueAnimator.ofFloat(width - padding - (thumbRadii<<1), padding);
			switchColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
					@Override
					public void onAnimationUpdate(ValueAnimator p1)
					{
						// TODO: Implement this method
						float value = (float)p1.getAnimatedValue();
						thumbBounds.set(value, thumbBounds.top, value + (thumbRadii<<1), thumbBounds.bottom);
						invalidate();
					}
				});
            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
            switchColor.setDuration(250);
            switchColor.start();
        } else {
            ValueAnimator switchColor = ValueAnimator.ofFloat(padding, width - padding - (thumbRadii<<1));
			switchColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
					@Override
					public void onAnimationUpdate(ValueAnimator p1)
					{
						// TODO: Implement this method
						float value = (float)p1.getAnimatedValue();
						thumbBounds.set(value, thumbBounds.top, value + (thumbRadii<<1), thumbBounds.bottom);
						invalidate();
					}
				});
            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
            switchColor.setDuration(250);
            switchColor.start();
        }
        isOn =! isOn;
        if(onToggledListener != null) {
            onToggledListener.onSwitched(this, isOn);
        }
        return true;
    }

    /**
     * Method to handle touch screen motion events.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override public final boolean onTouchEvent(MotionEvent event) {
        if(isEnabled()) {
            float x = event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
						startTime = System.currentTimeMillis();
						return true;
					}

                case MotionEvent.ACTION_MOVE: {
						float xt = -thumbRadii + x;
						if (xt > padding && x + thumbRadii < width - padding) {
							thumbBounds.set(xt, thumbBounds.top, x + thumbRadii, thumbBounds.bottom);
							invalidate();
						}
						return true;
					}

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
						long endTime = System.currentTimeMillis();
						long span = endTime - startTime;
						if (span < 200) {
							performClick();
						} else {
							if (x >= width >>> 1) {
								int onMaxX = width - padding - (thumbRadii<<1);
								ValueAnimator switchColor = ValueAnimator.ofFloat((x > onMaxX ? onMaxX : x), onMaxX);
								switchColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
										@Override
										public void onAnimationUpdate(ValueAnimator p1)
										{
											// TODO: Implement this method
											float value = (float)p1.getAnimatedValue();
											thumbBounds.set(value, thumbBounds.top, value + (thumbRadii<<1), thumbBounds.bottom);
											invalidate();
										}
									});
								switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
								switchColor.setDuration(250);
								switchColor.start();
								isOn = true;
							} else {
								ValueAnimator switchColor = ValueAnimator.ofFloat((x < padding ? padding : x), padding);
								switchColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
										@Override
										public void onAnimationUpdate(ValueAnimator p1)
										{
											// TODO: Implement this method
											float value = (float)p1.getAnimatedValue();
											thumbBounds.set(value, thumbBounds.top, value + (thumbRadii<<1), thumbBounds.bottom);
											invalidate();
										}
									});
								switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
								switchColor.setDuration(250);
								switchColor.start();
								isOn = false;
							}
							if(onToggledListener != null) {
								onToggledListener.onSwitched(this, isOn);
							}
						}
						invalidate();
						return true;
					}

                default: {
						return super.onTouchEvent(event);
					}
            }
        } else {
            return false;
        }
    }

    public int getColorOn() {
        return colorOn;
    }

    public void setColorOn(int colorOn) {
        this.colorOn = colorOn;
        invalidate();
    }

    public int getColorOff() {
        return colorOff;
    }

    public void setColorOff(int colorOff) {
        this.colorOff = colorOff;
        invalidate();
    }

    public String getLabelOn() {
        return labelOn;
    }

    public void setLabelOn(String labelOn) {
        this.labelOn = labelOn;
        invalidate();
    }

    public String getLabelOff() {
        return labelOff;
    }

    public void setLabelOff(String labelOff) {
        this.labelOff = labelOff;
        invalidate();
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        paint.setTypeface(typeface);
        invalidate();
    }

	public boolean getOn() {
		return isOn;
	}

    public int getColorDisabled() {
        return colorDisabled;
    }

    public void setColorDisabled(int colorDisabled) {
        this.colorDisabled = colorDisabled;
        invalidate();
    }

    public int getColorBorder() {
        return colorBorder;
    }

    public void setColorBorder(int colorBorder) {
        this.colorBorder = colorBorder;
        invalidate();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = (int)(textSize * getResources().getDisplayMetrics().scaledDensity);
        invalidate();
    }

	public interface OnToggledListener {
		void onSwitched(View view, boolean isOn);
	}
}
