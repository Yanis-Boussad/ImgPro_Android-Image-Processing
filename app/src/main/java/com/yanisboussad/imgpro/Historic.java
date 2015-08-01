package com.yanisboussad.imgpro;

import android.graphics.Bitmap;

import java.util.ArrayList;

public final class Historic {
    public static ArrayList<Bitmap> imagesArray = new ArrayList<>(8);
    public static int hist = -1;
    int recentHistoric = 0;

	public void setNewHistoric(Bitmap bmp){
		if(hist< imagesArray.size())
			hist++;
			recentHistoric = hist;
			imagesArray.add(hist, bmp);
	}
	
	public Bitmap getCurrentHistoric(){
		return imagesArray.get(hist);
	}
	
	public Bitmap getLastHistoric(){
		if(hist>0)
			hist--;
		return imagesArray.get(hist);
	}
	public Bitmap getNextHistoric(){
		if(hist < recentHistoric)
			hist++;
		return imagesArray.get(hist);
	}	 
}
	
	

