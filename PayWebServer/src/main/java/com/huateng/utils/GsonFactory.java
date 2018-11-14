package com.huateng.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
	 private static Gson gson = new GsonBuilder().create();
	 public static Gson getGson() {
		 return gson;
	 }
}
