package org.kreyssel.selenium.visualdiff.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileOutputStream;

public class ScreenshotStore {

	private TFile archive;
	
	public ScreenshotStore(File storeFile) {
		this.archive = new TFile(storeFile);
	}
	
	public ScreenshotMeta addScreenshot(String testClass, String testMethod, String screenshotId, String url, String title, File file) throws IOException{
		String pngPath = getPath(testClass, testMethod, screenshotId, ".png");
		String propPath = getPath(testClass, testMethod, screenshotId, ".properties");
		
		TFile fileInArchive = new TFile(this.archive, pngPath);
		
		// copy screenshot file to archive
		new TFile(file).cp(fileInArchive);
		
		ScreenshotMeta meta = new ScreenshotMeta(testClass, testMethod, screenshotId, url, title, fileInArchive.getInnerEntryName());
		
		TFileOutputStream out = new TFileOutputStream(new TFile(this.archive, propPath));
		try {
			meta.store(out);
		}finally{
			out.close();
		}		
		
		return meta;
	}
	
	public List<ScreenshotMeta> getScreenshots() throws IOException{
		ArrayList<ScreenshotMeta> metaList = new ArrayList<ScreenshotMeta>();
		
		readMeta(metaList, archive.listFiles(new PropertiesFileFilter()));
		
		return metaList;
	}
	
	public InputStream getInputStream(String path) throws FileNotFoundException{
		return new TFileInputStream(new TFile(archive, path));
	}
	
	protected void readMeta(ArrayList<ScreenshotMeta> metaList, TFile[] entries) throws IOException {
		for(TFile file:entries){
			if(file.isDirectory()){
				readMeta(metaList, file.listFiles());
			}else if(file.getName().endsWith(".properties")) {
				metaList.add(loadMeta(file));
			}
		}
	}
	
	protected ScreenshotMeta loadMeta(TFile file) throws IOException{
		ScreenshotMeta meta;
		
		TFileInputStream in = new TFileInputStream(file);
		try {
			meta = ScreenshotMeta.load(in);
		}finally{
			in.close();
		}
		
		return meta;
	}
	
	protected String getPath(String testClass, String testMethod,String screenshotId, String fileEnding) {
		return (testClass + "_"+testMethod + "_"+screenshotId).replace('.', '/') + fileEnding;
	}
	
	/**
	 * class PropertiesFileFilter
	 * 
	 * @author kreyssel
	 */
	private static class PropertiesFileFilter implements FileFilter {
		
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().endsWith(".properties");
		}
	}
}
