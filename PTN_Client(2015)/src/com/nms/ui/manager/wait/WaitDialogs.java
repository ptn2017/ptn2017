package com.nms.ui.manager.wait;
   
import java.awt.Dimension;   
import java.util.concurrent.Executors;  
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.TimeUnit;   
import javax.swing.JDialog;  
import javax.swing.JFrame;   

import com.nms.ui.manager.ResourceUtil;
import com.nms.ui.manager.keys.StringKeysTitle;
      
    public class WaitDialogs {   
        private JDialog dialog = null;  
        private Gif gif; 
        private  int result = -5;  
        public int  showDialog(JFrame father) {  
              
           
         
            ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();  
            this.gif = new Gif(WaitDialog.class.getResourceAsStream("/com/nms/ui/images/gif/13221819.gif"), 105);
 
            gif.setBounds(10,10,350,80);  

     
            dialog = new JDialog(father, true);  
            dialog.setTitle(ResourceUtil.srcStr(StringKeysTitle.TIT_PLEASE_WAIT));  
            dialog.setLayout(null);    
            dialog.add(gif);  
            s.scheduleAtFixedRate(new Runnable() {  
                  
                @Override  
                public void run() {  
                    // TODO Auto-generated method stub  
                      
                        try {
							Thread.sleep(5000);
							WaitDialogs.this.dialog.dispose();  
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        
                    
                }  
            }, 1, 1, TimeUnit.SECONDS);  
            dialog.pack();  
            dialog.setSize(new Dimension(310,80));  
            dialog.setLocationRelativeTo(father);  
            dialog.setVisible(true);  
            return result;  
        }
		public Gif getGif() {
			return gif;
		}
		public void setGif(Gif gif) {
			this.gif = gif;
		}  
    }  