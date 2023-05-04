import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class VideoPanel extends JPanel implements MouseWheelListener {
	
	VideoCapture cap;
    boolean isZoomed = true;
    Component mainCom;
    public boolean isZoomed() {
		return isZoomed;
	}
	public void setZoomed(boolean isZoomed) {
		this.isZoomed = isZoomed;
	}

	float scaleFactor =2;
	int Pwidth=800;
	int Pheight=600;
	int imgHeight = 720;
	int imgWidth = 1080;
	double xpos = 0;
	double ypos = 0;
	double scale = 1.0;
	public int getWidth() {
		return Pwidth;
	}
	public void setWidth(int width) {
		this.Pwidth = width;
	}
	public int getHeight() {
		return Pheight;
	}
	public void setHeight(int height) {
		this.Pheight = height;
	}
	public double getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public double getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public static void main(String[] args) {
		VideoPanel panel = new VideoPanel ("/home/prabir/Videos/fog/fog1.avi" );
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
       
    
        frame.setSize(1200,720);  
        frame.setVisible(true);
        panel.repaintInThread();
        panel.mainCom = (Component)frame;
    }
	

	public VideoPanel (String camView) {
		cap = new VideoCapture();
		cap.open  (camView);
		imgWidth = cap.publicGetWidth();
		imgHeight = cap.publicGetHeight();
		xpos = imgWidth/2;
		ypos = imgWidth/2;
		scale = Math.min((float)Pheight/imgHeight,(float)Pwidth/imgWidth);
		addMouseWheelListener(this);
    }
	
	public void repaintInThread () {
		new RepaintThread().start();
	}
	class RepaintThread extends Thread{
        @Override
        public void run() {
            for (;;){
                repaint();
                try { Thread.sleep(30);
                } catch (InterruptedException e) {    }
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	BufferedImage image;
    	int type=0;
    	
		  image =  cap.getImage();
		  //type=cap.getType();
		  Pheight = mainCom.getHeight();
		  Pwidth = mainCom.getWidth();
		  scaleFactor = Math.min((float)Pheight/imgHeight,(float)Pwidth/imgWidth);
		  
		  //System.out.println("scaleFactor="+scaleFactor);
    	
    	if (scale>1) {

    		double x1 = xpos - 0.5*imgWidth/scale;
    		double x2 = xpos + 0.5*imgWidth/scale;
    		double y1 = ypos - 0.5*imgHeight/scale;
    		double y2 = ypos + 0.5*imgHeight/scale;

    		if(x1<0)
    		{
    			x2 = x2 - x1;
    			x1 = 0;
    		}
    		else if(x2>imgWidth)
    		{
    			x1 = imgWidth - (x2-x1);
    			x2 = imgWidth;
    		}
    		if(y1<0)
    		{
    			y2 = y2 - y1;
    			y1 = 0;
    		}
    		else if(y2>imgHeight)
    		{
    			y1 = imgHeight - (y2-y1);
    			y2 = imgHeight;
    		}

    		xpos = (x1+x2)/2.0;
    		ypos = (y1+y2)/2.0;
    		g.drawImage(image,0,0,(int)(imgWidth*scaleFactor),(int)(imgHeight*scaleFactor),(int)x1,(int)y1,(int)x2,(int)y2, this);

    	} 
    	else
    	{
    		scaleFactor *= scale;
    		//mainCom.setSize((int)(imgWidth*scaleFactor)+1, (int)(imgHeight*scaleFactor)+1);
    		g.drawImage(image,0,0,(int)(imgWidth*scaleFactor),(int)(imgHeight*scaleFactor),this);
    	//g.drawImage(image, xpos, ypos, width, height, null);
    	}
                 
    }
    

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		System.out.println("mouseWheelMoved"+e.getScrollType());
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

			scale += (.05 * e.getWheelRotation());
			scale = Math.max(0.1, scale);
			
			Point p = e.getPoint();
			xpos += ((p.getX()/(double)Pwidth)-0.5)*imgWidth/scale;
			ypos += ((p.getY()/(double)Pheight)-0.5)*imgHeight/scale;

			xpos = Math.min(xpos,imgWidth);
			xpos = Math.max(xpos,0);
			ypos = Math.min(xpos,imgHeight);
			ypos = Math.max(xpos,0);
			
			System.out.println(xpos+" "+ypos+" "+scale);

		}
	}
}

