package com.cloudbean.trackerUtil;

public class StringUtil {
	public static String getStringByEnter(int length, String string)
	{         for (int i = 1; i <= string.length(); i++)
	 	{ 
	 		if (string.substring(0, i).length() > length) 
	 		{ 
	 			return string.substring(0, i - 1) + "\n" +  
	 			getStringByEnter(length, string.substring(i - 1));
	 		}
	 	}
	 	return string;
	}

}
