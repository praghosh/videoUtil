import java.awt.EventQueue;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;



public class VideoCapture {
	private static final String NATIVE_LIBRARY_NAME = "VideoCap1.0";

	static{
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("Loading VideoCap Library");
		System.loadLibrary(NATIVE_LIBRARY_NAME);// Load native library ADAS1.0.dll (Windows) 
		                               //or libADAS1.0.so (Unixes)
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//  at runtime
		// This library contains a native method called sayHello()
    }

    BufferedImage img;
    int width = -1, height = -1, channel = -1;
    private long addr;
    private int totalFrames = -1;
    private double totalMSec = -1;
    private native long createObject();
    private native int getWidth(long addr);
    private native int getHeight(long addr);
    private native int getChannel(long addr);
    private native boolean isStopped(long addr);
    private native byte[] getByteArr(long addr);
    private native int openCapture(long addr, String source);
    private native double totalLength(long addr,int flag); // flag 0:numFrames 1:millisec  2:fraction
    private native void setPointer(long addr, double value, int flag, boolean forward); //forward: forward or backward
    private native double getPointer(long addr, int flag);  // flag 0:no of Frames 1:millisec
    private native void setPause(long addr,boolean val);
    
    public VideoCapture() {
    	addr = createObject();
    }

    public VideoCapture(int h, int w, int c) {
    	addr = createObject();
        getSpace(h,w,c);
    }
    
    public void open(String source)
    {
    	openCapture(addr,source);
    	totalMSec = totalLength(addr,1);
    	totalFrames = (int) totalLength(addr,0);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void getSpace(int h, int w, int c) {
        int type = 0;
        if (c == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (c== 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        if (img == null || img.getWidth() != w || img.getHeight() != h || img.getType() != type)
            img = new BufferedImage(w, h, type);
    }

    BufferedImage getImage(){
    	if(width==-1 || height == -1 || channel == -1)
    	{
    		width = getWidth(addr);
    		height = getHeight(addr);
    		channel = getChannel(addr);
    		getSpace(height,width,channel);
    	}
        
        byte[] data = getByteArr(addr);
        img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(data, data.length), new Point() ) );
        return img;
    }
    
    public int publicGetWidth()
    {
    	return getWidth(addr);
    }
    public int publicGetHeight()
    {
    	return getHeight(addr);
    }
    public int publicGetChannel()
    {
    	return getChannel(addr);
    }
    
    public double getTotalLengthByMilliSec()
    {
    	return totalLength(addr,1);
    }
    public int getTotalLengthByFrame()
    {
    	return (int)totalLength(addr,0);
    }
    public void fastForwardByMilliSec(double val)
    {
    	setPointer(addr,val, 1, true);
    }
    public void fastForwardByFrame(int val)
    {
    	setPointer(addr,(double)val, 0, true);
    }
    public void rewindByMilliSec(double val)
    {
    	setPointer(addr,val, 1, false);
    }
    public void rewindByFrame(int val)
    {
    	setPointer(addr,(double)val, 1, true);
    }
    
    public double getPositionByMilliSec()
    {
    	return getPointer(addr,1);
    }
    public double getPositionByFrame()
    {
    	return getPointer(addr,0);
    }
    public double getPositionByFraction()
    {
    	return getPointer(addr,2);
    }
}
