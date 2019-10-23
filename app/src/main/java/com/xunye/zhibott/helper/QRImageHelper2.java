package com.xunye.zhibott.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileOutputStream;
import java.util.Hashtable;

/**
 * @类功能说明: 生成二维码图片示例
 */
public class QRImageHelper2
{
	private static int QR_WIDTH = 1200, QR_HEIGHT = 1200;
	/**
	 * @方法功能说明: 生成二维码图片,实际使用时要初始化sweepIV,不然会报空指针错误
	 * @参数: @param url 要转换的地址或字符串,可以是中文
	 * @return void
	 * @throws
	 */

	public static boolean createQRImage(String content, Bitmap logoBm, String filePath, int width, int height) {
		return _createQRImage(content, logoBm, filePath, width, height);
	}

	private static boolean createQRImage(String content, Bitmap logoBm, String filePath) {
		return _createQRImage(content, logoBm, filePath, QR_WIDTH, QR_HEIGHT);
	}

	/**
	 * 要转换的地址或字符串,可以是中文
	 * @param content	内容
	 * @param logoBm	二维码Logo
	 * @param filePath	生成二维码图片路径
     * @return
     */
	private static boolean _createQRImage(String content, Bitmap logoBm, String filePath, int width, int height) {
		try {
			if (content == null || "".equals(content) || content.length() < 1) {
				return false;
			}
			//配置参数
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//图像数据转换，使用了矩阵转换
			//容错级别
			//hints.put(EncodeHintType.ERROR_CORRECTION, String.valueOf(ErrorCorrectionLevel.H));
			//设置空白边距的宽度
            //hints.put(EncodeHintType.CHARACTER_SET, String.valueOf(2)); //default is 4
			hints.put(EncodeHintType.MARGIN, "1");
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			if (null == bitmap) return false;
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

			if (logoBm != null) {
				bitmap = addLogo(bitmap, logoBm);
			}
			return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 在二维码中间添加Logo图案
	 */
	private static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}

		if (logo == null) {
			return src;
		}

		//获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();

		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}

		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}

		//logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 6 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

//			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.save();
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}

		return bitmap;
	}

	public static Bitmap createBitmap(String content,int width,int height) throws WriterException {
		if (content == null || "".equals(content) || content.length() < 1) {
			return null;
		}
		//配置参数
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		//图像数据转换，使用了矩阵转换
		//容错级别
		//hints.put(EncodeHintType.ERROR_CORRECTION, String.valueOf(ErrorCorrectionLevel.H));
		//设置空白边距的宽度
		//hints.put(EncodeHintType.CHARACTER_SET, String.valueOf(2)); //default is 4
		hints.put(EncodeHintType.MARGIN, "1");
		BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
		int[] pixels = new int[width * height];
		//下面这里按照二维码的算法，逐个生成二维码的图片，
		//两个for循环是图片横列扫描的结果
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (bitMatrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				} else {
					pixels[y * width + x] = 0xffffffff;
				}
			}
		}
		//生成二维码图片的格式，使用ARGB_8888
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		if (null == bitmap)
			return null;
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		return bitmap;
	}
//	private static BitMatrix updateBit(BitMatrix matrix, int margin){
//		int tempM = margin*2;
//		int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性
//		int resWidth = rec[2] + tempM;
//		int resHeight = rec[3] + tempM;
//		BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
//		resMatrix.clear();
//		for(int i= margin; i < resWidth- margin; i++){   //循环，将二维码图案绘制到新的bitMatrix中
//			for(int j=margin; j < resHeight-margin; j++){
//				if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
//					resMatrix.set(i,j);
//				}
//			}
//		}
//		return resMatrix;
//	}
}
