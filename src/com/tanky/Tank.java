package com.tanky;

import java.lang.Math;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;


public class Tank {
    /*Tank cizimleri
    * Atıslar vs. hep burada yapıyor
    * */
    double g = 9.82; // yercekimi ivmesi
    double t = 0;    // gecen sure
    int v = 40;      // hız
    int angle = 0;   // aci
    double cx,cy,vx,vy,sx,sy; //cizimlerde kullanılacak değiskenler
    double radian = Math.PI/180; // sin cos vs. radyan aldığı için gerekli
    double[] px = new double[8]; // Tank polygonu Xleri
    double[] py = new double[8]; // Tank polygonu Yleri
    String tcolor;               // Renkler
    boolean shooting = false;    // Atış yapılıyor mu değişkeni
    boolean shot = false;        // Atış mı bekleniyor yoksa atış mı bitirildi turun düzgün değişebilmesi için gerekli

    public Tank(double x, double y, String color){
        /* Constructor x,y ve renk alıp polygon degerlerini uretiyor
        *  Polygon koordinatlari
        *
        *             3|---------|4
        *              |         |
        *     1|-------|2       5|-----|6
        *      |                       |
        *     0|_______________________|7
        *               Şekil A
        * */

        px[0] = x;
        py[0] = y;
        tcolor = color;
        px[1] = px[0];
        px[2] = px[1]+25;
        px[3] = px[2];
        px[4] = px[3]+25;
        px[5] = px[4];
        px[6] = px[5]+25;
        px[7] = px[6];
        py[1] = py[0]+25;
        py[2] = py[1];
        py[3] = py[2]+20;
        py[4] = py[3];
        py[5] = py[4]-20;
        py[6] = py[5];
        py[7] = py[6]-25;

    }
    public void drawTank(GLAutoDrawable gld){
        GL2 gl = gld.getGL().getGL2();
        setColor(gld);
        // Tankı çiz
        gl.glBegin(GL2.GL_POLYGON);
        // Nedendir bilmediğim bir eğim var onu sağa ve sola çevirebilmek için
        if (px[0]>400){
            for (int i=0;i<=7;i++){
//                System.out.println(i+" x = "+px[i]+" y= "+py[i]);
                gl.glVertex2d(px[i],py[i]);
            }          }
        if (px[0]<400){
            for (int i=7;i>=0;i--){
//                System.out.println(i+" x = "+px[i]+" y= "+py[i]);
                gl.glVertex2d(px[i],py[i]);

            }
        }
        gl.glEnd();
    }

    public void drawTurret(GLAutoDrawable gld){
        /*Topun açıya göre hareket ettirebilmesi için
        * Ayrıca çizmek gerekiyor
        *
        * */
        GL2 gl = gld.getGL().getGL2();
        setColor(gld);
        int len = 30;
        // Tankın ekranda nerede olduğuna göre top o tarafa çiziliyor
        // Sağdaysa 2-3 çizgisine  soldaysa 4-5 çizgisine bkz şekil A
        if (px[0]>400){
            sx = px[2]+(Math.cos((180-angle)*radian))*len;
            sy = (py[3]-10)+(Math.sin((180-angle)*radian))*len;
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex2d(px[2],(py[3]-10));
            gl.glVertex2d(sx,sy);
            gl.glEnd();
        }
        if (px[0]<400){
            sx = px[4]+(Math.cos((angle)*radian))*len;
            sy = (py[4]-10)+(Math.sin((angle) * radian))*len;
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex2d(px[4], (py[4] - 10));
            gl.glVertex2d(sx,sy);
            gl.glEnd();
        }


    }

    public boolean hitTest(double x, double y){
        // Verilen koordinatlar tankın için de mi dışında mı
        // Vuruldu mu diye test etmek için
        if (y > 50) // Eğer mermi yukarıdaysa otomatik false döndür olmasa da olur performans için
            return false;
        if (x > px[3] && x < px[5] && y < py[3] && y > py[5])
            return true;
        else if (x > px[1] && x < px[7] && y < py[1] && y > py[7])
            return true;
        else
            return false;
    }

    public void shoot(GLAutoDrawable gld) {
        GL2 gl = gld.getGL().getGL2();
        int sangle;
        double[] hit = new double[2];

        if (px[0]>400){
            // Tankın ekrandaki durumuna göre açıyı ayarla
            sangle = 180-angle;
            }
        else{
            sangle = angle;
        }
        // Lise fiziği eğik atış denklemleri
        // cx,cy turret koordinatlarını başlangıç kabul ediyoruz
        vx = v * Math.cos((sangle)*radian); // Hızın x bileşeni
        vy = v * Math.sin((sangle)*radian); // Hızın y bileşeni
        cx = sx + vx * t;                   // Yeni X koordinatı X ekseninde konum sabit değişiyor.
        cy =((vy*t) - (0.5f*g*t*t))+sy;     // Yeni Y koordinatı H = Vy*t - 1/2*g*t^2 formülünden çıkıyor
        gl.glBegin(GL2.GL_POINTS);
        setColor(gld);
        gl.glColor3d(1,1,1);
        gl.glVertex2d(cx, cy);
        gl.glEnd();
        t+=0.2;
//        System.out.println("x = "+cx+" y= "+cy);
        if (cx > 800 || cx < 0 || cy < 0 || cy > 480){
            // Ekran dışına çıkarsa atışı bitir
            t = 0;
            shooting = false;
            shot = true;
//            System.out.println("Shot missed");
        }
    }

    public void setColor(GLAutoDrawable gld){
        // Rengi belirle
        GL2 gl = gld.getGL().getGL2();
        if(tcolor.equals("blue"))
            gl.glColor3f(0.0f,0.0f,1.0f);
        if(tcolor.equals("red"))
            gl.glColor3f(1.0f,0.0f,0.0f);
        if(tcolor.equals("green"))
            gl.glColor3f(0.0f,1.0f,0.0f);
        if(tcolor.equals("yellow"))
            gl.glColor3f(1.0f,1.0f,0.0f);
        if(tcolor.equals("black"))
            gl.glColor3f(0.0f,0.0f,0.0f);
        if(tcolor.equals("white"))
            gl.glColor3f(1.0f,1.0f,1.0f);

    }
}
