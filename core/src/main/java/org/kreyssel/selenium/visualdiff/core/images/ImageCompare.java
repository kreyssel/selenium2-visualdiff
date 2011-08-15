/*************************************************************************
 *
 *  OpenOffice.org - a multi-platform office productivity suite
 *
 *  $RCSfile: ScreenComparer.java,v $
 *
 *  $Revision: 1.3 $
 *
 *  last change: $Author: rt $ $Date: 2005/10/19 11:54:12 $
 *
 *  The Contents of this file are made available subject to
 *  the terms of GNU Lesser General Public License Version 2.1.
 *
 *
 *    GNU Lesser General Public License Version 2.1
 *    =============================================
 *    Copyright 2005 by Sun Microsystems, Inc.
 *    901 San Antonio Road, Palo Alto, CA 94303, USA
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License version 2.1, as published by the Free Software Foundation.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *
 ************************************************************************/
package org.kreyssel.selenium.visualdiff.core.images;


import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * 
 * 
 * <p>This implementation based on http://ooo.googlecode.com/svn/trunk/bean/qa/complex/ScreenComparer.java
 * 
 * 
 * @author kreyssel
 *
 */
public class ImageCompare {
	
	private int diffColor;
    
	private BufferedImage img1;
	private BufferedImage img2;
	private BufferedImage imgDiff;
    
    public ImageCompare(InputStream in1, InputStream in2) throws IOException {
    	int red = 0xff;
        int alpha = 0xff;
        diffColor = (alpha << 24);
        diffColor = diffColor | (red << 16);
        
        img1 = ImageIO.read(in1);
        img2 = ImageIO.read(in2);
	}
        
    public boolean compare() throws InterruptedException {

        boolean ret = true;
        int w1 = img1.getWidth();
        int h1 = img1.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        if (w1 != w2 || h1 != h2)
        {
            System.out.println("### 1\n");
            //Different size. Create an image that holds both images.
            int w = w1 > w2 ? w1 : w2;
            int h = h1 > h2 ? h1 : h2;
            imgDiff = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < h; y ++)
            {
                for (int x = 0; x < w; x++)
                {
                    boolean bOutOfRange = false;
                    int pixel1 = 0;
                    int pixel2 = 0;
                    //get the pixel for m_img1
                    if (x < w1 && y < h1)
                        pixel1 = img1.getRGB(x, y);
                    else
                        bOutOfRange = true;

                    if (x < w2 && y < h2)
                        pixel2 = img2.getRGB(x, y);
                    else
                        bOutOfRange = true;

                    if (bOutOfRange || pixel1 != pixel2)
                        imgDiff.setRGB(x, y, diffColor);
                    else
                        imgDiff.setRGB(x, y, pixel1);

                }
            }
            return false;
        }

        //Images have same dimension
        int[] pixels1 = new int[w1 * h1];
        PixelGrabber pg = new PixelGrabber(
            img1.getSource(), 0, 0, w1, h1, pixels1, 0, w1);
        pg.grabPixels();

        int[] pixels2 = new int[w2 * h2];
        PixelGrabber pg2 = new PixelGrabber(
            img2.getSource(), 0, 0, w2, h2, pixels2, 0, w2);
        pg2.grabPixels();

        imgDiff = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_ARGB);

        //First check if the the images differ.
        int lenAr = pixels1.length;
        int index = 0;
        for (index = 0; index < lenAr; index++)
        {
            if (pixels1[index] != pixels2[index])
                break;
        }

        //If the images are different, then create the diff image
        if (index < lenAr)
        {
            for (int y = 0; y < h1; y++)
            {
                for (int x = 0; x < w1; x++)
                {
                    int offset = y * w1 + x;
                    if (pixels1[offset] != pixels2[offset])
                    {
                        ret = ret && false;
                        imgDiff.setRGB(x, y, diffColor);
                    }
                    else
                    {
                        imgDiff.setRGB(x, y, pixels1[offset]);
                    }
                }
            }
        }
        return ret;
    }
    
    public void saveDiffAsPng(File file) throws IOException {
    	FileOutputStream out = new FileOutputStream(file);
    	try{
    		saveDiffAsPng(out);
    	}finally{
    		out.close();
    	}
    }
    
    public void saveDiffAsPng(OutputStream out) throws IOException {
    	ImageIO.write(imgDiff, "png", out);
    }
}
