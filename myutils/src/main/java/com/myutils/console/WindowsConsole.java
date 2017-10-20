package com.myutils.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WindowsConsole {

	public static void main(String[] args) {
		try {
			Process p = Runtime.getRuntime().exec("jstat -gc 1196 1000");
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while(true){
				String line = reader.readLine();
				if(line == null){
					Thread.sleep(5000);
					continue;
				}
				System.out.println(line);
			}
			
			
		
//			for (Object obj : errStream.toArray()) {
//				System.out.println(obj.toString());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
