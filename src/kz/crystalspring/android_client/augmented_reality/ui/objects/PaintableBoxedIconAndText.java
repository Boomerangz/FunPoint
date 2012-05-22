package kz.crystalspring.android_client.augmented_reality.ui.objects;

import java.text.BreakIterator;
import java.util.ArrayList;

//import kz.crystalspring.android_client.C_Log;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class PaintableBoxedIconAndText extends PaintableObject {

	    private float width=0, height=0;
		private float areaWidth=0, areaHeight=0;
		private ArrayList<String> lineList = null;
		private String[] lines = null;
		private float[] lineWidths = null;
		private float lineHeight = 0;
		private float maxLineWidth = 0;
		private float pad = 0;

		private String txt = null;
	    private float fontSize = 12;
		private int borderColor = Color.rgb(255, 255, 255);
		private int backgroundColor = Color.argb(160, 0, 0, 0);
		private int textColor = Color.rgb(255, 255, 255);
		
		private Bitmap bitmap = null;

		public PaintableBoxedIconAndText(String txtInit, float fontSizeInit, float maxWidth, Bitmap bitmap) {
			this(txtInit, fontSizeInit, maxWidth, Color.rgb(255, 255, 255), Color.argb(128, 0, 0, 0), Color.rgb(255, 255, 255), bitmap);
		}

		public PaintableBoxedIconAndText(String txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor, Bitmap bitmap) {
			set(txtInit, fontSizeInit, maxWidth, borderColor, bgColor, textColor, bitmap);
		}
		
		/**
		 * Set this objects parameters. This should be used instead of creating new objects.
		 * @param txtInit String to paint.
		 * @param fontSizeInit Font size to use.
		 * @param maxWidth max width of the text.
		 * @param borderColor Color of the border.
		 * @param bgColor Background color of the surrounding box.
		 * @param textColor Color of the text.
		 * @throws NullPointerException if String param is NULL.
		 */
		public void set(String txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor, Bitmap pBitmap) {
			if (txtInit==null) throw new NullPointerException();
			
			this.borderColor = borderColor;
			this.backgroundColor = bgColor;
			this.textColor = textColor;
			this.pad = getTextAsc();
			this.bitmap = pBitmap;
			
			set(txtInit, fontSizeInit, maxWidth);
		}
		
		/**
		 * Set this objects parameters. This should be used instead of creating new objects.
		 * Note: This uses previously set or default values for border color, background color, and text color.
		 * @param txtInit String to paint.
		 * @param fontSizeInit Font size to use.
		 * @param maxWidth max width of the text.
		 * @throws NullPointerException if String param is NULL.
		 */
		public void set(String txtInit, float fontSizeInit, float maxWidth) {
			if (txtInit==null) throw new NullPointerException();

			try {
				prepTxt(txtInit, fontSizeInit, maxWidth);
			} catch (Exception ex) {
				ex.printStackTrace();
				prepTxt("TEXT PARSE ERROR", 12, 200);
			}
		}
		
		private void prepTxt(String txtInit, float fontSizeInit, float maxWidth) {
			if (txtInit==null) throw new NullPointerException();
			
			setFontSize(fontSizeInit);

			txt = txtInit;
			fontSize = fontSizeInit;
			areaWidth = maxWidth - pad;
			lineHeight = getTextAsc() + getTextDesc();

			if (lineList==null) lineList = new ArrayList<String>();
			else lineList.clear();

			BreakIterator boundary = BreakIterator.getWordInstance();
			boundary.setText(txt);

			int start = boundary.first();
			int end = boundary.next();
			int prevEnd = start;
			while (end != BreakIterator.DONE) {
				String line = txt.substring(start, end);
				String prevLine = txt.substring(start, prevEnd);
				float lineWidth = getTextWidth(line);

				if (lineWidth > areaWidth) {
					// If the first word is longer than lineWidth 
					// prevLine is empty and should be ignored
					if(prevLine.length()>0) lineList.add(prevLine);

					start = prevEnd;
				}

				prevEnd = end;
				end = boundary.next();
			}
			String line = txt.substring(start, prevEnd);
			lineList.add(line);

			if (lines==null || lines.length!=lineList.size()) lines = new String[lineList.size()];
			if (lineWidths==null || lineWidths.length!=lineList.size()) lineWidths = new float[lineList.size()];
			lineList.toArray(lines);

			maxLineWidth = 0;
			for (int i = 0; i < lines.length; i++) {
				lineWidths[i] = getTextWidth(lines[i]);
				if (maxLineWidth < lineWidths[i])
					maxLineWidth = lineWidths[i];
			}
			areaWidth = maxLineWidth;
			areaHeight = lineHeight * lines.length;

			width = areaWidth + pad * 2;
			height = areaHeight + pad * 2;
			
			if (bitmap.getWidth() <= 48 & bitmap.getHeight() <= 48) {
				if (height < bitmap.getHeight() + 8) {
					height = bitmap.getHeight() + 8;
				}
				if (width < bitmap.getWidth() + 8) {
					width = bitmap.getWidth() + 8;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paint(Canvas canvas) {
			if (canvas==null || bitmap == null) throw new NullPointerException();
			
		    setFontSize(fontSize);
			float vBmpWidth = 0;

			if (bitmap.getWidth() <= 48 & bitmap.getHeight() <= 48) {
				vBmpWidth = bitmap.getWidth() + 4;
			}
			
			setFill(true);
			setColor(backgroundColor);
//			paintRect(canvas, 0, 0, width + vBmpWidth, height);
			paintRoundRect(canvas, 0, 0, width + vBmpWidth, height);
			paintCircle(canvas, (width + vBmpWidth )/2 - 2, height + 8, 4);

			setFill(false);
			setColor(borderColor);
			setStrokeWidth(3);
//			paintRect(canvas, 0, 0, width + vBmpWidth, height);
			paintRoundRect(canvas, 0, 0, width + vBmpWidth, height);

			paintCircle(canvas, (width + vBmpWidth )/2 - 2, height + 8, 4);

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				setFill(true);
				setStrokeWidth(0);
				setColor(textColor);
				paintText(canvas, pad + vBmpWidth, pad + lineHeight * i + getTextAsc(), line);
			}
			
//			setColor(textColor);
//			setFill(false);
//			paintRect(canvas, 2, 2, bitmap.getWidth()+4, bitmap.getHeight()+4);
			
//			paintBitmap(canvas,  bitmap, -(bitmap.getWidth()/2), -(bitmap.getHeight()/2));

//			C_Log.v(0, "sd", "draw h=" + bitmap.getHeight() + " w="+ bitmap.getWidth());
			
			if (vBmpWidth != 0) {
				paintBitmap(canvas,  bitmap, 4, 4);
			} else {
				paintBitmap(canvas,  bitmap, (width-bitmap.getWidth())/2 , - bitmap.getHeight() - 7 );
			}
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getWidth() {
			return width;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getHeight() {
			return height;
		}
	}
