package com.huateng.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EBCDICGBK
{

    private static final char[] digit = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private static Map<Integer, EBCD> gbkMap = new HashMap<Integer, EBCD>();

    private static Map<Integer, EBCD> edbcMap = new HashMap<Integer, EBCD>();

    private static boolean isLoadMap = false;
    static
    {
        //loadMap();
    }

    public EBCDICGBK()
    {

    }

    public static String toHex(byte[] values)
    {
        StringBuilder ret = new StringBuilder();
        byte value = 0;
        for (int i = 0; i < values.length; i++)
        {
            value = values[i];
            int tmpValue = value & 0x00FF;
            ret.append(digit[(tmpValue >>> 4) & 0x0F]);
            ret.append(digit[(tmpValue & 0x0F)]);
        }
        return ret.toString();
    }

    public static byte[] ebcdicToGBK(byte[] edbicValue) throws Exception
    {
        if (edbicValue == null)
            return new byte[0];
        loadMap();
        int i = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte ch1 = 0;
        byte ch2 = 0;
        boolean inHan = false;
        int tmpValue = 0;
        EBCD ebcd = null;
        int codeValue;
        for (i = 0; i < edbicValue.length; i++)
        {
            ch1 = edbicValue[i];
            if (ch1 == 0x0E)
            {//汉字开始
                inHan = true;
                continue;
            } else if (ch1 == 0x0F)
            {//汉字结束
                inHan = false;
                continue;
            }
            if (inHan)
            {//是汉字
                i++;

                //这里添加修改，这个函数并不是单纯的中文EBCDIC码转GBK，结合浙江农信报文实际情况修改。　by Denny yao
                if (i >= edbicValue.length)
                {
                    break;
                }
                ch2 = edbicValue[i];

                // 跳过半中文
                if (ch2 == 0x0E)
                {
                    continue;
                }

                tmpValue = (0x000000FF & ch1) << 8;
                tmpValue = tmpValue | (0x000000FF & ch2);
                ebcd = edbcMap.get(tmpValue);
                if (ebcd == null)
                {//实在找不着，用一个字代替
                 //                    System.out.println("ebcdicToGBK chinese码表中未找到" + tmpValue);
                 //codeValue = new Integer(8140);
                    inHan = false;
                    out.write(32);
                    continue;

                } else
                {
                    codeValue = ebcd.getV();
                }
                out.write((codeValue & 0x0000FF00) >>> 8);
                out.write(codeValue & 0x000000FF);
            } else
            {//是Assic码
                ebcd = edbcMap.get(0x000000FF & ch1);
                if (ebcd == null)
                {
                    codeValue = 0;
                } else
                {
                    codeValue = ebcd.getV();
                }
                //throw new Exception("ebcdicToGBK ASSIC码表中未找到" + ch1);

                out.write(codeValue);
            }
        }
        return out.toByteArray();
    }

    public static byte[] gbkToEBCDIC(byte[] gbkValue) throws Exception
    {
        if (gbkValue == null)
            return new byte[0];
        loadMap();
        int i = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte ch1 = 0;
        byte ch2 = 0;
        boolean inHan = false;
        int tmpValue = 0;
        EBCD ebcd = null;
        int codeValue = 0;
        for (i = 0; i < gbkValue.length; i++)
        {
            ch1 = gbkValue[i];
            if (ch1 < 0x0)
            {//汉字
                if (inHan == false)
                {
                    inHan = true;
                    out.write(0x0E);
                }
                i++;
                ch2 = gbkValue[i];
                tmpValue = (0x000000FF & ch1) << 8;
                tmpValue = tmpValue | (0x000000FF & ch2);
                ebcd = gbkMap.get(tmpValue);
                if (ebcd == null)
                {
                    throw new Exception("gbkToEBCDIC CHINESE码表中未找到" + tmpValue);
                } else
                {
                    codeValue = ebcd.getK();
                }
                out.write((codeValue & 0x0000FF00) >>> 8);
                out.write(codeValue & 0x000000FF);
            } else
            {//0x00-0x7F
                if (inHan)
                {
                    inHan = false;
                    out.write(0x0F);
                }
                ebcd = gbkMap.get(0xFF & ch1);
                if (ebcd == null)
                {
                    throw new Exception("gbkToEBCDIC asc码表中未找到" + ch1);
                } else
                {
                    codeValue = ebcd.getK();
                }
                out.write(codeValue);
            }

        }
        if (inHan)
            out.write(0x0F);
        byte[] result = out.toByteArray();

        //这里并不是单纯的中文GBK转EBCDIC码，结合浙江农信实际情况修改		by Denny Yao
        //		if(result.length == gbkValue.length)
        return result;
        //		else
        //		{
        //			byte[] result1 = new byte[gbkValue.length];
        //			System.arraycopy(result, 0, result1, 0, gbkValue.length);
        //			return result1;
        //		}
    }

    public static void loadMap() throws Exception
    {
        if (isLoadMap)
            return;
        synchronized (edbcMap)
        {
            if (isLoadMap)
                return;
            InputStream in = EBCDICGBK.class
                    .getResourceAsStream("gbk.properties");
            //FileInputStream in = new FileInputStream(new File("//E://TDDOWNLOAD//GBK_CVT.txt"));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            String buf = null;
            buf = reader.readLine();
            while (buf != null)
            {
                String[] codes = buf.split(",");
                int key = (Integer.parseInt(codes[0], 16));
                int value = Integer.parseInt(codes[1], 16);
                EBCD ebcd = new EBCD(key, value);
                edbcMap.put(key, ebcd);
                gbkMap.put(value, ebcd);
                buf = reader.readLine();
            }
            isLoadMap = true;
            reader.close();
            in.close();
        }
    }

    public static int bytesToInteger(byte[] bytes)
    {
        int tmpValue = 0;
        tmpValue = (0x000000FF & bytes[0]) << 8;
        tmpValue = tmpValue | (0x000000FF & bytes[1]);
        return tmpValue;
    }

    public static void demoChangeStringToHex(String inputString)
    {
        int changeLine = 1;
        String s = "Convert a string to HEX/こんにちは/你好";
        if (inputString != null)
        {
            s = inputString;
        }
        System.out.println(s);
        for (int i = 0; i < s.length(); i++)
        {
            byte[] ba = s.substring(i, i + 1).getBytes();
            // & 0xFF for preventing minus
            String tmpHex = Integer.toHexString(ba[0] & 0xFF);
            System.out.print("0x" + tmpHex.toUpperCase());
            System.out.print(" ");
            if (changeLine++ % 8 == 0)
            {
                System.out.println("");
            }
            // Multiply byte according
            if (ba.length == 2)
            {
                tmpHex = Integer.toHexString(ba[1] & 0xff);
                System.out.print("0x" + tmpHex.toUpperCase());
                System.out.print(" ");
                if (changeLine++ % 8 == 0)
                {
                    System.out.println("");
                }
            }
        }

        System.out.println(""); // change line
        System.out.println(""); // change line
    }

    public static String trace(byte inBytes[])
	{
		int j = 0;
		byte temp[] = new byte[76];
		bytesSet(temp, ' ');
		StringBuffer strc = new StringBuffer(" ");
		strc.append("----------------------------------------------------------------------------\n");
		for (int i = 0; i < inBytes.length; i++)
		{
			if (j == 0)
			{
				System.arraycopy(String.format("%03d: ", new Object[] {
					Integer.valueOf(i)
				}).getBytes(), 0, temp, 0, 5);
				System.arraycopy(String.format(":%03d", new Object[] {
					Integer.valueOf(i + 15)
				}).getBytes(), 0, temp, 72, 4);
			}
			System.arraycopy(String.format("%02X ", new Object[] {
				Byte.valueOf(inBytes[i])
			}).getBytes(), 0, temp, j * 3 + 5 + (j <= 7 ? 0 : 1), 3);
			if (inBytes[i] == 0)
				temp[j + 55 + (j <= 7 ? 0 : 1)] = 46;
			else
				temp[j + 55 + (j <= 7 ? 0 : 1)] = inBytes[i];
			if (++j == 16)
			{
				strc.append(new String(temp)).append("\n");
				bytesSet(temp, ' ');
				j = 0;
			}
		}

		if (j != 0)
		{
			strc.append(new String(temp)).append("\n");
			bytesSet(temp, ' ');
		}
		strc.append("----------------------------------------------------------------------------\n");
		System.out.println(strc.toString());
		return strc.toString();
	}
    private static void bytesSet(byte inBytes[], char fill)
	{
		if (inBytes.length == 0)
			return;
		for (int i = 0; i < inBytes.length; i++)
			inBytes[i] = (byte)fill;

	}
    
    /**
     * 检查字符串中汉字有几部分    比如“中国aaa”,汉字有一部分， 如果是“中国aaa中国”,那么汉字就用两部分
     * @param chnStr
     * @return
     */
    public static int chnArrLen(String chnStr) {
    	byte[] bytStr = chnStr.getBytes();
    	if (chnStr.length() == bytStr.length) {
    		return 0;
    	}
    	int count = 1; //默认字符串全是汉字
    	List<Integer> list = new ArrayList<Integer>();
    	for (int i = 0; i < bytStr.length; i++) {
    		if (bytStr[i] < 0) {
    			i++;
    			list.add(i);
    		}
    	}
    	for (int j = 0; j < list.size() - 1; j++) {
    		if (list.get(j + 1) - list.get(j) != 2) {
    			count++;
    		}
    	}
    	return count;
    }
    
    public static void main(String args[]) throws Exception
    {

        loadMap();
        String r = "[台]";
        demoChangeStringToHex(r);
        byte[] ori = r.getBytes();
        byte[] enc = EBCDICGBK.gbkToEBCDIC(ori);
        String ss = new String(enc);
        System.out.println("ebcdic:"+ss);
        demoChangeStringToHex(ss);
        //		System.out.println(ss);
        String str = "c4c2f1f1404040404040404040404040f5f07cd9d7c8c4d94040d9c5d7f0f0f0404040f040404040404040404040404040404040404040404040404040f0404040f1f9f17cd6c4c1e3c14040404040f1f7f7c2c2d6f6f0f3f0f1f8f9f2f3f9f5f5f8f9f2f0f0f0c1e3d40e61625863498452924c9f57f05bcf58c3404040404040404040404040404040404040404040404040404040404040404040404040404040404040f1404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040404040";
		//byte[] body = HexBinary.decode(str);
		//String bb =new String(EBCDICGBK.ebcdicToGBK(body));
		//System.out.println(bb);

    }

}
