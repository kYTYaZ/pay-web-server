package com.wldk.framework.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统工具类
 * 
 * @author Administrator
 * 
 */
public class SystemUtils {
	private static Logger log = LoggerFactory.getLogger(SystemUtils.class);

	/**
	 * 命令常量
	 * 
	 * @author Administrator
	 * 
	 */
	public enum Command {
		DB2("db2 -tvf"), DB2_WIN("db2cmd /c /i /w db2 -tvf"), RAR("rar x -o+"), RAR_WITH_PWD(
				"rar x -o+ -ptjnhsd2007"), SEVEN_Z("7z x"), TAR("tar Czxvf");
		private String cmd;

		/**
		 * @return the cmd
		 */
		public String getCmd() {
			return cmd;
		}

		/**
		 * @param cmd
		 *            the cmd to set
		 */
		public void setCmd(String cmd) {
			this.cmd = cmd;
		}

		/**
		 * 构造方法
		 * 
		 * @param cmd
		 */
		Command(String cmd) {
			this.cmd = cmd;
		}
	}

	/**
	 * 文件类型常量定义
	 * 
	 * @author Administrator
	 * 
	 */
	public enum FileType {
		DOC(new byte[] { -48, -49, 17, -32, -95, -79, 26, -31, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 62, 0, 3, 0, -2, -1, 9, 0, 6,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }), XLS(new byte[] {
				-48, -49, 17, -32, -95, -79, 26, -31, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 62, 0, 3, 0, -2, -1, 9, 0 }), RAR(
				new byte[] { 82, 97, 114, 33, 26, 7, 0 }), TAR_Z(new byte[] {
				31, -99, -112, 68, -122, -64 }), TAR(new byte[] { 68, 67, 48 });
		/** 魔数 */
		private byte[] magicNumber;

		public byte[] mn() {
			return magicNumber;
		}

		/**
		 * 构造方法
		 */
		FileType(byte[] magicNumber) {
			this.magicNumber = magicNumber;
		}
	}

	/**
	 * 根据指定的日期判断是属于上半年还是下半年，如果是上半年返回字符串yy06，如果是下半年返回字符串yy12
	 * 
	 * @param dt
	 * @return
	 */
	public static Calendar getSemiYearStr(Date dt) {
		Calendar c = Calendar.getInstance();
		if (dt != null) {
			c.setTime(dt);
			if (c.get(Calendar.MONTH) <= 5) { // 上半年
				c.set(Calendar.MONTH, 5);
			} else {
				c.set(Calendar.MONTH, 11);
			}
			return c;
		}
		return null;
	}

	/**
	 * 格式化日期
	 * 
	 * @param dt
	 * @param pattern
	 * @return
	 */
	public static String format(Date dt, String pattern) {
		FastDateFormat fdf = FastDateFormat.getInstance(pattern);
		return fdf.format(dt);
	}

	/**
	 * 格式化日期
	 * 
	 * @param dt
	 * @param pattern
	 * @return
	 */
	public static String format(Calendar c, String pattern) {
		FastDateFormat fdf = FastDateFormat.getInstance(pattern);
		return fdf.format(c);
	}

	/**
	 * 执行命令
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	public static void exec(Command cmd, String arg) {
		// 组合命令行字符串
		StringBuffer cmds = new StringBuffer();
		if (arg == null) {
			cmds.append(cmd.getCmd().toLowerCase());
		} else {
			cmds.append(cmd.getCmd().toLowerCase()).append(" ").append(arg);
		}
		//log.debug(cmds);
		BufferedReader reader = null;
		Process p = null;
		try {
			Runtime runtime = Runtime.getRuntime();
			p = runtime.exec(cmds.toString());
			reader = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				// 一个空的循环，用于等待命令执行结束
				log.debug(line);
				// System.out.println(line);
			}
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			// System.err.println(e);
		} finally {
			IOUtils.closeQuietly(reader);
			try {
				if (p != null) {
					p.waitFor();
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * 正则表达式匹配
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static boolean matcher(String str, String pattern) {
		try {
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(str);
			return m.matches();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}

	/**
	 * 将字符串转换成BigDecimal对象
	 * 
	 * @param source
	 * @return
	 */
	public static BigDecimal toBigDecimal(String source) {
		DecimalFormat df = new DecimalFormat("##,####,####.##");
		try {
			BigDecimal bd = new BigDecimal(df.parse(source).doubleValue());
			// 设置精度
			bd = bd.setScale(4, RoundingMode.HALF_UP);
			return bd;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}

	/**
	 * 识别指定的文件是否是由参数type指定的文件类型
	 * 
	 * @param file
	 *            待识别的文件
	 * @param type
	 *            文件类型常量
	 * @return
	 */
	public static boolean fileType(File file, FileType type) {
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			byte[] b = new byte[type.mn().length];
			int read = input.read(b); // 读取指定字节数
			if(read == -1){
				log.info("流中没有数据！");
			}
			return Arrays.equals(b, type.mn());
		} catch (IOException ioe) {
			log.error(ioe.getMessage(),ioe);
			return false;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	/**
	 * 解压缩给定目录及子目录中的所有压缩文件
	 */
	public static void uncompress(File file, String pwd) {
		// 使用栈替代递归
		Stack<File> s = new Stack<File>();
		// 将文件列表进栈
		if (file.isDirectory()) {
			pushAll(file.listFiles(), s);
		} else {
			s.push(file);
		}
		// 如果栈不为空则循环
		while (!s.isEmpty()) {
			// 弹栈
			File f = s.pop();
			// 如果是目录，则获取该目录下的所有文件包括目录，并压入栈中
			if (f.isDirectory()) {
				pushAll(f.listFiles(), s);
			} else { // 如果是文件，则判断是否是压缩文件格式，如果是则进行解压缩处理，否则继续循环
				if (fileType(f, FileType.RAR)) { // rar格式
					uncompress(f, FileType.RAR, pwd, s);
				} else if (fileType(f, FileType.TAR_Z)) { // tar.Z格式
					uncompress(f, FileType.TAR_Z, pwd, s);
				} else if (fileType(f, FileType.TAR)) { // tar格式
					uncompress(f, FileType.TAR, pwd, s);
				} else {
					continue;
				}
				// 删除压缩格式文件
				FileUtils.deleteQuietly(f);
			}
		}
	}

	/**
	 * 将文件集合全部压入栈中
	 * 
	 * @param fs
	 * @param s
	 */
	public static void pushAll(File[] fs, Stack<File> s) {
		for (File f : fs) {
			s.push(f);
		}
	}

	/**
	 * 解压缩文件，目前支持rar、tar.Z和tar格式的的压缩文件
	 */
	public static void uncompress(File file, FileType type, String pwd,
			Stack<File> s) {
		// 创建一个随机的临时目录
		File tmpDir = mkTmpDir();
		if (tmpDir == null) { // 如果创建临时目录失败，则直接返回
			return;
		}
		// 执行解压缩命令
		switch (type) {
		case RAR:
			if (pwd != null && !pwd.equals("")) { // 带密码的RAR
				exec(Command.RAR, "-p" + pwd + " " + file.getAbsolutePath()
						+ " " + tmpDir.getAbsolutePath());
			} else {
				exec(Command.RAR, file.getAbsolutePath() + " "
						+ tmpDir.getAbsolutePath());
			}
			break;
		case TAR_Z:
			// exec(Command.SEVEN_Z, file.getAbsolutePath() + " -aoa -o" +
			// tmpDir);
			exec(Command.TAR, file.getAbsolutePath() + " " + tmpDir);
			break;
		case TAR:
			// exec(Command.SEVEN_Z, file.getAbsolutePath()
			// + " -aoa -o"
			// + new File(tmpDir, file.getName().substring(0,
			// file.getName().indexOf('.'))));
			exec(Command.TAR, file.getAbsolutePath()
					+ " "
					+ new File(tmpDir, file.getName().substring(0,
							file.getName().indexOf('.'))));
			break;
		default:
			log.info("类型不支持");
		}
		File[] files = tmpDir.listFiles();
		// 获取解压缩之后的所有文件集合
		if (files != null && files.length > 0) {
			try {
				for (File f : files) {
					File d = new File(file.getParentFile(), f.getName());
					if (f.isDirectory()) {
						FileUtils.copyDirectory(f, d);
					} else {
						FileUtils.copyFile(f, d);
					}
					s.push(d);
				}
			} catch (IOException e) {
				System.err.println(e);
			} finally {
				try {
					FileUtils.deleteDirectory(tmpDir);
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
	}

	/**
	 * 创建一个随机的临时目录
	 * 
	 * @return
	 */
	public static File mkTmpDir() {
		// 获取一个UUID作为数据处理的临时目录
		String tmpDirName = UUID.randomUUID().toString();
		File tmpDir = new File(org.apache.commons.lang.SystemUtils
				.getJavaIoTmpDir(), tmpDirName);
		// 如果临时目录不存在，则创建之
		if (!tmpDir.exists()) {
			try {
				FileUtils.forceMkdir(tmpDir);
			} catch (IOException e) {
				return null;
			}
		}
		return tmpDir;
	}

	/**
	 * 将指定目录下的所有不为压缩文件格式的文件复制到目标目录中
	 * 
	 * @param src
	 * @param dest
	 */
	public static void copy(File src, File dest) {
		// 使用栈替代递归
		Stack<File> s = new Stack<File>();
		// 将文件列表进栈
		if (src.isDirectory()) {
			pushAll(src.listFiles(), s);
		} else {
			s.push(src);
		}
		// 如果栈不为空则循环
		while (!s.isEmpty()) {
			// 弹栈
			File f = s.pop();
			// 如果是目录，则获取子目录并压入栈中
			if (f.isDirectory()) {
				pushAll(f.listFiles(), s);
			} else {
				// 如果不是压缩文件，则复制到目标目录中
				if (!isCompressFile(f)) {
					try {
						FileUtils.copyFileToDirectory(f, dest);
					} catch (IOException e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		}
	}

	/**
	 * 判断指定的文件是否是压缩文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isCompressFile(File file) {
		if (fileType(file, FileType.RAR)) { // rar格式
			return true;
		} else if (fileType(file, FileType.TAR_Z)) { // tar.Z格式
			return true;
		} else if (fileType(file, FileType.TAR)) { // tar格式
			return true;
		}
		return false;
	}

	/**
	 * 查找src数组在dest数组中不存在的元素，并生成一个子数组返回
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public static String[] findNotExistsArray(String[] src, String[] dest) {
		return null;
	}

	public static void main(String[] args) throws Exception {
		
//		int i = 5;
//		
//        switch(i){
//        case 1:
//        	System.out.println("1");
//        	break;
//        case 2:
//        	System.out.println("2");
//        	break;
//        case 3:
//        	System.out.println("3");
//        	break;
//        default:
//        	System.out.println("没有");
//        }
	}
}
