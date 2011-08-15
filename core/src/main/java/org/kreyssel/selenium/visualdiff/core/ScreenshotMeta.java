package org.kreyssel.selenium.visualdiff.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ScreenshotMeta {

	public final String testClass;
	
	public final String testMethod;
	
	public final String screenshotId;
	
	public final String url;
	
	public final String title;
	
	public final String path;
	
	ScreenshotMeta(String testClass, String testMethod, String screenshotId, String url,String title,String path){
		this.testClass = testClass;
		this.testMethod = testMethod;
		this.screenshotId = screenshotId;		
		this.url=url;
		this.title=title;
		this.path=path;
	}
	
	public void store(OutputStream out) throws IOException{
		Properties props = new Properties();
		props.setProperty("testClass", testClass);
		props.setProperty("testMethod", testMethod);
		props.setProperty("screenshotId", screenshotId);
		props.setProperty("url", url);
		props.setProperty("title", title);
		props.setProperty("path", path);
		props.store(out, "");
	}
	
	public static ScreenshotMeta load(InputStream in) throws IOException{
		Properties props = new Properties();
		props.load(in);
		
		return new ScreenshotMeta(props.getProperty("testClass"),
			props.getProperty("testMethod"),
			props.getProperty("screenshotId"),
			props.getProperty("url"),
			props.getProperty("title"),
			props.getProperty("path"));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) {
			return false;
		}else if( !(obj instanceof ScreenshotMeta)){
			return false;
		}
		
		ScreenshotMeta compMeta = (ScreenshotMeta) obj;
		
		return new EqualsBuilder().append(testClass, compMeta.testClass).append(testMethod, compMeta.testMethod).append(screenshotId, compMeta.screenshotId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(testClass).append(testMethod).append(screenshotId).hashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	
}
