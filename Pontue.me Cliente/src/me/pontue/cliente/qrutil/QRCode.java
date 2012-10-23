package me.pontue.cliente.qrutil;

import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.view.MenuItem.OnMenuItemClickListener;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class QRCode {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	public static Bitmap encodeAsBitmap(String email) throws Exception {
		if (email == null)
			throw new Exception();

		Map<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(email);
		if (encoding != null) {
			hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}

		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(email, BarcodeFormat.QR_CODE, 250, 250, hints);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

}
