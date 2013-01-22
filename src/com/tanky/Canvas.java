package com.tanky;

import java.awt.*;
import javax.swing.*;

import com.jogamp.opengl.util.FPSAnimator;
import javax.media.opengl.awt.GLCanvas;

public class Canvas extends JFrame {
    /* Sadece pencereyi oluşturup ekrana kanvası çiziyor
     Bu classın çok bir esprisi yok
    * */
    static FPSAnimator animator = null;

    public static void main(String[] args) {
        final Canvas app = new Canvas();


        SwingUtilities.invokeLater (
                new Runnable() {
                    public void run() {
                        app.setVisible(true);
                    }
                }
        );


        SwingUtilities.invokeLater (
                new Runnable() {
                    public void run() {
                        animator.start();

                    }
                }
        );
    }

    public Canvas() {

        super("Tanky Tanks");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Game display = new Game();

        GLCanvas glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(display);
        glcanvas.addKeyListener(display);

        animator = new FPSAnimator(glcanvas,60,true); // Fizik denklemleri kullanıldığı için displayin ekrana kaç kez
        // çizildiğini bilmem gerekir diye düşündüm FPSanimatorla bunu belirleyebiliyorsun mesela bu satır displayi ekr
        // ana saniyede 60 kere çizdiriyor.


        getContentPane().add(glcanvas, BorderLayout.CENTER);
        setSize(800, 480);


        centerWindow(this);
    }

    public void centerWindow(Component frame) {
        /*Pencereyi ekranda ortalıyor çok bir muhabbeti yok
        * */
        Dimension screenSize =
                Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize  = frame.getSize();

        if (frameSize.width  > screenSize.width )
            frameSize.width  = screenSize.width;
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;

        frame.setLocation (
                (screenSize.width  - frameSize.width ) >> 1,
                (screenSize.height - frameSize.height) >> 1
        );
    }
}
