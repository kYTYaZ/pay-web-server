/**
 * 
 */
package com.wldk.framework.system.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
/**
 * 压缩文件的包装器类
 * 
 * @author Administrator
 * 
 */
public class CompressFile {
	/** 压缩文件 */
	private File file;
	/** 压缩密码 */
	private String pwd;

	/**
	 * 构造方法
	 * 
	 * @param file
	 */
	public CompressFile(File file) throws IOException {
		this.file = file;
		// 检查文件的压缩类型
		if (!FileHeader.RAR.isSame(this.file)) {
			throw new IOException("不是合法的压缩文件格式或文件不存在.");
		}
	}

	/**
	 * 构造方法
	 * 
	 * @param file
	 * @param pwd
	 * @throws IOException
	 */
	public CompressFile(File file, String pwd) throws IOException {
		this(file);
		this.pwd = pwd;
	}

	
	public String toString() {
		// TODO Auto-generated method stub
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("file", file).append("pwd", pwd).toString();
	}

	/**
	 * 解压缩
	 * 
	 * @param dest
	 *            目标目录
	 * @throws IOException
	 */
//	public void uncompress(File destDir) throws IOException,Exception {
//		uncompress(file, destDir, pwd);
//	}

	/**
	 * 解压缩，递归算法
	 * 
	 * @param src
	 *            源文件，可能是文件也可能是目录
	 * @param dest
	 *            目标目录
	 * @param pwd
	 *            解压缩密码
	 * @throws IOException
	 */
//	public void uncompress(File src, File dest, String pwd) throws IOException,Exception {
//		// 判断是否是文件还是目录，如果是目录则进行递归
//		if (src.isDirectory()) {
//			File[] files = src.listFiles(); // 如果是目录，则获取目录下的所有文件然后进行迭代递归
//			for (int i = 0, n = files.length; i < n; i++) {
//				uncompress(files[i], dest, pwd);
//			}
//			if (src.listFiles().length <= 0) {
//				// 如果是空目录则删除掉
//				FileUtils.deleteDirectory(src);
//			}
//		} else {
//			// 如果不是目录，则判断文件的类型,如果不是压缩格式的文件则直接复制到目标目录中,否则进行解压缩
//			if (FileHeader.RAR.isSame(src)) {
//				// 获取解压缩命令行执行器
//				Execute exec = ExecuteFactory.getExecute(FileHeader.RAR, src);
//				if (StringUtils.isNotBlank(pwd)) {
//					exec.setPwd(pwd); // 设置解压缩密码
//				}
//				// 在系统临时目录中创建一个临时的工作目录，用于存放解压文件
//				File workDir = new File(SystemUtils.getJavaIoTmpDir(), UUID
//						.randomUUID().toString());
//				if (!workDir.exists()) { // 如果不存在则创建一个
//					boolean isMkDir = workDir.mkdir();
//					if(!isMkDir){
//						throw new Exception(workDir.getName()+"创建目录失败！");
//					}
//				}
//				// 设置解压缩目录
//				exec.setUcDir(workDir);
//				// 执行解压缩
//				int res = exec.execute();
//				if (res == 0) {
//					// 解压缩成功之后，先删除压缩文件，在递归子目录中的所有文件
//					FileUtils.deleteQuietly(src);
//					uncompress(workDir, dest, pwd);
//				} else {
//					// 如果解压缩失败，则删除空目录
//					FileUtils.deleteDirectory(workDir);
//				}
//			} else {
//				// 如果不是RAR压缩文件，则直接移动到目标目录中
//				// 检查目标文件是否已经存在，如果已经存在则删除先
//				File t = new File(dest, src.getName());
//				if (t.exists()) {
//					FileUtils.deleteQuietly(t);
//				}
//				FileUtils.moveFileToDirectory(src, dest, false);
//			}
//		}
//	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) throws Exception {
//		// TODO Auto-generated method stub
//		File f = new File("D:\\in_20090101.rar");
//		File destDir = new File("F:\\test");
//		CompressFile cf = new CompressFile(f, "tjnhsd2007");
////		System.out.println(cf);
//		cf.uncompress(destDir);
////		System.out.println("complete.");
//	}

}
