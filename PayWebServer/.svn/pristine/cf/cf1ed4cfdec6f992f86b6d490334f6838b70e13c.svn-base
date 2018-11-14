package com.wldk.framework.utils;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片裁减、缩放工具
 * 
 * @author Administrator
 * 
 */
public class ImageUtil {

	public static final String IMG_TYPE_GIF = "gif";

	public static final String IMG_TYPE_JPG = "jpg";

	public static final String IMG_TYPE_PNG = "png";

	public static final String IMG_TYPE_BMP = "bmp";
	/** 分隔符 */
	public static final String divide = "#";
	
	public static final String IMG_PATH_FLAG = "show_image_z/";
	
	private File file;
	/** 图片类型 */
	private String imgType;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getImgType() {
		return imgType;
	}

	public void setImgType(String imgType) {
		this.imgType = imgType;
	}

	/**
	 * 此接口用于定义图片缩放的比例
	 */
	public interface ImageZoomSize {
		/**
		 * 根据原图高度、宽度返回缩放的比例
		 */
		public float size(int sWidth, int sHeight);
	}

	/**
	 * 此接口用于定义图片裁减的位置
	 */
	public interface ImageCropSize {
		/**
		 * 根据原图高度、宽度返回裁减的矩形
		 */
		public Rectangle size(int sWidth, int sHeight);
	}

	/**
	 * 图片缩放方法：支持JPEG,GIF,PNG
	 * 
	 * @param ImageZoomSize
	 *            缩放比例对象
	 * @param imgType
	 *            图片类型：JPG,GIF,PNG,BMP
	 * @param input
	 *            图片输入流
	 * @param out
	 *            图片输出流
	 */
	public static void zoom(InputStream input, OutputStream output, String imgType, ImageZoomSize zSize) {
		try {
			BufferedImage inImg = ImageIO.read(input);
			float zoom = zSize.size(inImg.getWidth(), inImg.getHeight());
			int w = (int) (inImg.getWidth() * zoom);
			int h = (int) (inImg.getHeight() * zoom);
			BufferedImage bufImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) bufImage.getGraphics();
			g.drawImage(inImg, 0, 0, w, h, null);
			ImageIO.write(bufImage, imgType.equalsIgnoreCase(IMG_TYPE_GIF) ? IMG_TYPE_PNG : imgType, output);
			input.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 图片裁减方法
	 * 
	 * @param cSize
	 *            ImageCropSize对象
	 * @param imgType
	 *            图片类型
	 * @param input
	 *            输入流
	 * @param output
	 *            输出流
	 */
	public static void crop(InputStream input, OutputStream output, String imgType, ImageCropSize cSize) {
		try {
			BufferedImage inImg = ImageIO.read(input);
			Rectangle rect = cSize.size(inImg.getWidth(), inImg.getHeight());
			BufferedImage bufImage = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) bufImage.getGraphics();
			g.drawImage(inImg, -(int) rect.getX(), -(int) rect.getY(), inImg.getWidth(), inImg.getHeight(), null);
			ImageIO.write(bufImage, imgType.equalsIgnoreCase(IMG_TYPE_GIF) ? IMG_TYPE_PNG : imgType, output);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 即缩图，又裁减
	 * 
	 * @param input
	 * @param output
	 * @param imgType
	 *            图片类型
	 * @param zSize
	 *            缩图接口
	 * @param cSize
	 *            裁减接口
	 */
	public static void zoomAndCrop(InputStream input, OutputStream output, String imgType, ImageZoomSize zSize, ImageCropSize cSize) {
		try {
			// 缩图
			BufferedImage inImg = ImageIO.read(input);
			float zoom = zSize.size(inImg.getWidth(), inImg.getHeight());
			int w = (int) (inImg.getWidth() * zoom);
			int h = (int) (inImg.getHeight() * zoom);
			BufferedImage bufImage1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g1 = (Graphics2D) bufImage1.getGraphics();
			g1.drawImage(inImg, 0, 0, w, h, null);
			// 裁减
			Rectangle rect = cSize.size(bufImage1.getWidth(), bufImage1.getHeight());
			BufferedImage bufImage2 = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) bufImage2.getGraphics();
			g2.drawImage(bufImage1, -(int) rect.getX(), -(int) rect.getY(), bufImage1.getWidth(), bufImage1.getHeight(), null);
			// 输出图片
			ImageIO.write(bufImage2, imgType.equalsIgnoreCase(IMG_TYPE_GIF) ? IMG_TYPE_PNG : imgType, output);
			input.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据文件名，获取图片类型
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getImgType(String fileName) {
		String end = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (end.equalsIgnoreCase(IMG_TYPE_GIF))
			return IMG_TYPE_PNG;
		if (end.equalsIgnoreCase(IMG_TYPE_PNG))
			return IMG_TYPE_PNG;
		if (end.equalsIgnoreCase(IMG_TYPE_JPG))
			return IMG_TYPE_JPG;
		if (end.equalsIgnoreCase(IMG_TYPE_BMP))
			return IMG_TYPE_BMP;
		throw new RuntimeException("不支持的图片类型：" + end);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		zoom(new FileInputStream("d:/b.bmp"), new FileOutputStream("d:/b1.bmp"), getImgType("d:/b.bmp"), new ImageZoomSize() {
			public float size(int sWidth, int sHeight) {
				return (float) (320.0 / sWidth);
			}
		});
		crop(new FileInputStream("d:/b.bmp"), new FileOutputStream("d:/b2.bmp"), IMG_TYPE_BMP, new ImageCropSize() {
			public Rectangle size(int sWidth, int sHeight) {
				Rectangle re = new Rectangle(100, 100, 320, 240);
				return re;
			}
		});
		zoomAndCrop(new FileInputStream("d:/q.jpg"), new FileOutputStream("d:/q2.jpg"), IMG_TYPE_JPG, new ImageUtil.ImageZoomSize() {
			public float size(int sWidth, int sHeight) {
				// 将图片最大程度的压缩到70像素
				if (sWidth < 70 || sHeight < 70)
					return 1;
				return (float) ((70 * 1.0) / (sWidth > sHeight ? sHeight : sWidth));
			}
		}, new ImageUtil.ImageCropSize() {
			public Rectangle size(int sWidth, int sHeight) {
				// 充0,0开始裁减最大70像素的图片
				int x = sWidth > 70 ? 70 : sWidth;
				int y = sHeight > 70 ? 70 : sHeight;
				return new Rectangle(0, 0, x, y);
			}
		});
	}

	/**
	 * 组合生成文件名
	 * 
	 * @param file
	 * @param IMG_TYPE
	 * @return
	 */
	public static String fileName(File file, String IMG_TYPE) {
		// return DateUtil.getDate("yyyyMMdd") + File.separator + MD5.MD5(System.currentTimeMillis() + file.getName()) + ImageUtil.divide + IMG_TYPE + ImageUtil.divide + ".img";
		return DateUtils.getDate("yyyyMMdd") + File.separator + MD5.toMD5(System.currentTimeMillis() + file.getName()) + "." + IMG_TYPE;

	}

	/**
	 * 获取img图片文件，如果不存在，则返回null
	 * 
	 * @param req
	 * @return
	 */
	public static ImageUtil getImageFile(HttpServletRequest req) {
		ImageUtil bean = new ImageUtil();
		String url = req.getRequestURI();
		String IMG_TYPE = IMG_TYPE_JPG;
		String fname = url.substring(url.indexOf(IMG_PATH_FLAG) + IMG_PATH_FLAG.length());
		AppVars av = AppVars.getAppVars();
		File res = new File("" + av.getVar(AppVars.WEB_APP_REAL_PATH) + av.getVar("image-store") + File.separator + fname);
		if (!res.exists())
			res = new File("" + av.getVar(AppVars.WEB_APP_REAL_PATH) + av.getVar("image-404"));
		bean.setImgType(IMG_TYPE);
		bean.setFile(res);
		return bean;
	}

	/**
	 * 获取nmg图片文件，如果不存在，则返回null
	 * 
	 * @param req
	 * @param dac
	 * @return
	 */
	public static ImageUtil getNmgFile(HttpServletRequest req) {
		ImageUtil bean = new ImageUtil();
		String URL= req.getRequestURI();
		String IMG_TYPE = IMG_TYPE_JPG;
		String fname = URL.substring(URL.indexOf(IMG_PATH_FLAG) + IMG_PATH_FLAG.length());
		AppVars av = AppVars.getAppVars();
		String path="" + av.getVar(AppVars.WEB_APP_REAL_PATH) + av.getVar("file_affix_store_dir") + File.separator + fname;
		File res = new File(path);
		if (!res.exists())
			res = new File("" + av.getVar(AppVars.WEB_APP_REAL_PATH) + av.getVar("image-404"));
		bean.setImgType(IMG_TYPE);
		bean.setFile(res);
		return bean;
	}
    
    
  
}
