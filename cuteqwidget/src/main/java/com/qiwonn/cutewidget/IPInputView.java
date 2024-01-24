package com.qiwonn.cutewidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.Selection;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 *	超级简单快捷的IP地址输入框
 *	author: 王琦 2023/12/3 星期日
 */

public class IPInputView extends EditText {

	private static String ipv4[] = {"192","168","1",""};
 
    private int height;
    private int width;
	
    private int bottomLineLength  = 0;
    private int bottomLineColor   = Color.GRAY;
	private int divideLineColor   = Color.GRAY;
	private int divideLineWidth   = 4;
	private int divideLineWStartX = 0;
	private int rectAngle         = 0;
    private int focusedColor      = Color.BLUE;
	private int textColor         = Color.BLACK;
    private int bordColor         = Color.GRAY;
    /**
     * 光标位置
     */
    private int position          = 0;
	
	private Rect  rect[] = new Rect[4];
	
    private RectF rectF  = new RectF();
    private RectF focusedRecF = new RectF();

    /**
     * 文本画笔
     */
    private Paint textPaint;
    /**
     * 线框画笔
     */
    private Paint bordPaint;

    public IPInputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getAtt(context,attrs);
        initPaint();

		this.setCursorVisible(false);
		curFocus = 3;
		
		setText(ipv4[0] + ipv4[1] + ipv4[2] + ("".equals(ipv4[3])?"0":ipv4[3]));
		
		position = curFocus;
		
		for (int i=-1; ++i<4;) {
			rect[i] = new Rect();
		}
		
		addTextChangedListener(new LimitedTextWatcher(this, 12));
		
		// 禁用长按复制、粘贴、分享等其他
		setLongClickable(false);
    }

    private void getAtt(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayPsdInputView);
        bottomLineColor = typedArray.getColor(R.styleable.PayPsdInputView_bottomLineColor, bottomLineColor);
        
        divideLineWidth = typedArray.getDimensionPixelSize(R.styleable.PayPsdInputView_divideLineWidth, divideLineWidth);
        divideLineColor = typedArray.getColor(R.styleable.PayPsdInputView_divideLineColor, divideLineColor);
      
        rectAngle = typedArray.getDimensionPixelOffset(R.styleable.PayPsdInputView_rectAngle, rectAngle);
        focusedColor = typedArray.getColor(R.styleable.PayPsdInputView_focusedColor, focusedColor);

        typedArray.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        textPaint = getPaint(5, Paint.Style.FILL, textColor);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize(20);
		
        bordPaint = getPaint(divideLineWidth, Paint.Style.STROKE, bordColor);
    }

    private Paint getPaint(int strokeWidth, Paint.Style style, int color) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        paint.setColor(color);
        paint.setAntiAlias(true);
        return paint;
    }
	
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width  = w;

        divideLineWStartX = w>>2;

        bottomLineLength = w / (4 + 2);

        rectF.set(0, 0, width, height);
		
		int bx = (divideLineWStartX - bottomLineLength)>>1;
		int left = 0;
		for (int i=-1; ++i<4;) {
			left = bx + i*divideLineWStartX;
			rect[i].set(left, 0, left + bottomLineLength, height);
		}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawIPAddressBorder(canvas);
		drawIPAddressText(canvas);
		drawItemFocused(canvas, position);
    }

	private void drawIPAddressBorder(Canvas canvas) {
		// 绘制背景线框
		canvas.drawRoundRect(rectF, rectAngle, rectAngle, bordPaint);
		// 绘制分隔点
		int py = height>>1;
		int px = 0;
		for (int i = 1; i < 4; i++) {
			px = i * divideLineWStartX;
		 	canvas.drawText("·", px, py, textPaint);
        }
    }

	private void drawIPAddressText(Canvas canvas) {
		int i = -1;
		for(;++i<ipv4.length;) {
			canvas.drawText(ipv4[i], rect[i].centerX(), rect[i].centerY(), textPaint);
		}
	}

	/*
	private void drawIPAddressBoundRectText(Canvas canvas) {
		int oldColor = bordPaint.getColor();
		bordPaint.setColor(divideLineColor);
		int i = -1;
		for(;++i<ipv4.length;) {
			canvas.drawText(ipv4[i], rect[i].centerX(), rect[i].centerY(), textPaint);
			canvas.drawRect(rect[i], bordPaint);
		}
		bordPaint.setColor(oldColor);
	} */
	
    private void drawItemFocused(Canvas canvas, int position) {
        if (0 > position || position > 3)
            return;
		int   oldColor = bordPaint.getColor();
		float oldWidth = bordPaint.getStrokeWidth();
		bordPaint.setColor(focusedColor);
		bordPaint.setStrokeWidth(divideLineWidth);
		int left = position * divideLineWStartX;
        focusedRecF.set(left + divideLineWidth + 2, divideLineWidth, left + divideLineWStartX - divideLineWidth - 1, height - divideLineWidth - 1);
        canvas.drawRoundRect(focusedRecF, rectAngle, rectAngle, bordPaint);
		bordPaint.setColor(oldColor);
		bordPaint.setStrokeWidth(oldWidth);
    }

	private long key_timestamp = 0;
	private int  curFocus      = 3;
	private boolean isDelete   = false;

	private void onIPA(CharSequence text, int start,int lengthBefore, int lengthAfter) {
		if (null==ipv4)
			return;
		long timestamp = System.currentTimeMillis();
		isDelete = lengthBefore < lengthAfter ? false : true;
		if (isDelete) {
			if(lengthBefore!=1)
				return;
			if (ipv4[curFocus].length() > 0) {
				ipv4[curFocus] = ipv4[curFocus].substring(0,ipv4[curFocus].length()-1);
			}
			if (ipv4[curFocus].length() <= 0) {
				curFocus--;
				if (curFocus<0)
					curFocus = 0;
				position = curFocus;
			}
		} else {
			if (lengthAfter!=1)
				return;
			if (key_timestamp!=0 && ipv4[curFocus].length()>0 && timestamp - key_timestamp > 1000) {
				curFocus++;
				if (curFocus>=ipv4.length)
					curFocus = ipv4.length - 1;
				position = curFocus;
			}
			if (ipv4[curFocus].length() >= 3) {
				curFocus++;
				if (curFocus>=ipv4.length)
					curFocus = ipv4.length - 1;
				position = curFocus;
			}
			if (ipv4[curFocus].length() < 3) {
				ipv4[curFocus] = ipv4[curFocus] + String.valueOf( text.charAt(start) );
			}
			if (ipv4[curFocus].length() >= 3) {
				curFocus++;
				if (curFocus>=ipv4.length)
					curFocus = ipv4.length - 1;
				position = curFocus;
			}
		}
		key_timestamp = timestamp;
	}

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        onIPA(text,start,lengthBefore,lengthAfter);
        invalidate();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        // 保证光标始终位于字符串末尾
        if (selStart == selEnd) {
			if(null == getText())
				return;
            setSelection(getText().length());
        }
    }

	private String getIPV4Text() {
		return String.format("%s.%s.%s.%s",ipv4[0],ipv4[1],ipv4[2],ipv4[3]);
	}

	public String getContentText() {
		return getIPV4Text();
	}

	public void setContentText(final String text) {
		String pp[] = text.split("\\.");
		if (pp.length>0)
			ipv4[0] = pp[0];
		if (pp.length>1)
			ipv4[1] = pp[1];
		if (pp.length>2)
			ipv4[2] = pp[2];
		if (pp.length>3)
			ipv4[3] = pp[3];
		invalidate();
	}
	
	/**
     * 固定文本输入长度监听器
     */
	private class LimitedTextWatcher implements TextWatcher
	{
		// 文本的固定长度
		private int maxLength = 0;
		private EditText edtText = null;
		
		public LimitedTextWatcher(EditText et, int max) {
			edtText = et;
			maxLength = max;
		}
		
		@Override
		public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			// TODO: Implement this method
		}

		@Override
		public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			// TODO: Implement this method
			if(null == edtText)
				return;
				
			Editable edtable = edtText.getText();
			if (null == edtable)
				return;
			
			int len = edtable.length();
			if (len > maxLength) {
				int endIndex = Selection.getSelectionEnd(edtable);
				String str = edtable.toString();
				String stn = str.substring(0, maxLength);
				edtText.setText(stn);
				edtable = edtText.getText();
				if (endIndex > edtable.length()) {
					endIndex = edtable.length();
				}
				Selection.setSelection(edtable, endIndex);
			}
		}

		@Override
		public void afterTextChanged(Editable p1)
		{
			// TODO: Implement this method
		}
	}
}
