package com.util;

import java.awt.image.BufferedImage;  

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import java.util.Hashtable;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;

/**
 * ��ά���������Ҫ����MatrixToImageWriter�࣬��������Google�ṩ�ģ����Խ�����ֱ�ӿ�����Դ����ʹ��
 */
public class MatrixToImageWriter {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private MatrixToImageWriter() {
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static void writeToFile(BitMatrix matrix, String format, File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format "
                    + format + " to " + file);
        }
    }

    public static void writeToStream(BitMatrix matrix, String format,
            OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }
    
    public static void createEWM(String name ,String content,String path) throws Exception {
    	
    	String text = content; // ��ά������
        int width = 300; // ��ά��ͼƬ���
        int height = 300; // ��ά��ͼƬ�߶�
        String format = "jpg";// ��ά���ͼƬ��ʽ

        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // ������ʹ���ַ�������

        BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
                BarcodeFormat.QR_CODE, width, height, hints);
        
        File cre = new File(path);
        cre.mkdir();
        
        // ���ɶ�ά��
        File outputFile = new File(path + File.separator + name+".jpg");
        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
        System.out.println("��ά�����ɳɹ�");
        System.out.println();
    }
    
    public static void deleteAll(File delFile){
    	if(delFile.isFile() || delFile.list().length == 0){
    		delFile.delete();
    	} else {
    		for (File f  : delFile.listFiles()) {
    			deleteAll(f);
			}
    	}
    	
    	
    }
    
    public static void main(String[] args) throws Exception {
    	
    	//ɾ��·���������ļ�
    	File delFile = new File("D:/home/ewmpath");
    	deleteAll(delFile);
    	
        String text = "weixin://wxpay/bizpayurl?pr=PHQWxTM"; // ��ά������
        int width = 300; // ��ά��ͼƬ���
        int height = 300; // ��ά��ͼƬ�߶�
        String format = "jpg";// ��ά���ͼƬ��ʽ

        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // ������ʹ���ַ�������

        BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
                BarcodeFormat.QR_CODE, width, height, hints);
        
        File cre = new File("D:/home/ewmpath");
        cre.mkdir();
        
        // ���ɶ�ά��
        File outputFile = new File("D:/home/ewmpath" + File.separator + "new.jpg");
        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
    }
}