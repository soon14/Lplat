package com.zengshi.butterfly.core.util;

import java.util.Random;

import com.zengshi.butterfly.core.security.encrypt.DESEncrypt;

public class ButterflyHelpMain {

	/**
	 * @param args
	 */
	public static void main(String[] arg) {
		int lenght = arg.length;
		if(arg != null && lenght > 0 ) {
			for(int i=0;i<lenght ;i++) {
				if(arg[i].equalsIgnoreCase("-h")) {
					printHelp();
					System.exit(0);
				}
				if(arg[i].equalsIgnoreCase("-t")) {
					i++;
					String[] args = new String[lenght - 1 - i];
					for (int j = 0; j < args.length; j++) {
						args[j] = arg[i + 1 + j];
					}
					if ("RVSG".equals(arg[i])) {
						RandomValueStringGenerator.generatCode(args);
					} else if ("PWD".equals(arg[i])) {
						DESEncrypt.pwdGenerator(args);
					}
				}
				if(arg[i].equalsIgnoreCase("-l")) {
					i++;
				}
			}
		} else {
			printHelp();
		}
	}

	private static void printHelp() {
		System.out.println("useage: -h -c 编码数据 -l 长度 -t 运行的目标java");
		System.out.println("-h 打印帮助");
		System.out.println("-c [参数] 使用编码列表 ,默认包含所有的字母和数字");
		System.out.println("-l 长度 编码长度");
		System.out.println("-t 运行的目标java 参数：RVSG，生成32位随机数（RandomValueStringGenerator）/r/n 参数：PWD，生成加密的密码（用于数据库密码加密）");

	}
}
