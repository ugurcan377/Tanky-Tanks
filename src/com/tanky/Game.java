package com.tanky;

import java.awt.*;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collections;
import  java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;




public class Game implements GLEventListener,KeyListener{
    /*Bu classta daha ziyade menular, oyunla ilgili seyler var
    * Tanklarin cizimi atis methodları falan Tank classinda
    *
    * */
    Tank[] players = new Tank[2]; // İki tank var diziye atıyorum instancelar initin icinde olusturuyor.
    boolean game_finished = false; // Oyun bitti mi
    boolean on_menu = true;       // menude miyim  kontrolleri
    boolean draw_help = false;
    boolean draw_high = false;
    boolean hit_test;             // hedef vuruldu mu
    int tc = 0;                   // sıra kimde onu anlamak icin
    int mc = 3;                   // menude hangi secenegin uzerindeyim
    ArrayList<String> highList;

    util fileop = new util();
    TextRenderer render_text = new TextRenderer(new Font("SansSerif", Font.BOLD, 32));   // Basligi cizdirmek icin
    TextRenderer render_title = new TextRenderer(new Font("SansSerif", Font.BOLD, 64));  // Genel metin cizdirmek icim


    @Override
    public void display(GLAutoDrawable gld) {
        // TODO Auto-generated method stub
        // Thank you Captain Obvious
        GL2 gl = gld.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        if (on_menu && !draw_help && !draw_high){  // menüyü cizdiiriyor

            render_title.beginRendering(800,480);
            render_title.setColor(1.0f, 1.0f, 1.0f, 1.f);
            render_title.draw("Tanky Tanks", 175, 375);
            render_title.endRendering();

            gl.glColor3d(0, 0, 1);
            gl.glRectd(0,(mc%4+3)*50,800,(mc%4+3)*50+50); // Secili şıkkı göstermek icin
            /* Menu şıkları
            * 3 - Yeni Oyun
            * 2 - Highscores
            * 1 - Yardım
            * 0 - Cikis
            * Asagida seceneklerin koordinatları var
            * Koordinatları uydurmak icin 3 ekleyip 50 ile çarpıyorum
            * Mesela Yardım (1+3)*50 = 200 gibi
            * */

            render_text.beginRendering(800,480);
            render_text.setColor(1.0f, 1.0f, 1.0f, 1.f);
            render_text.draw("New Game/Continue", 220, 300);
            render_text.draw("Highscores",300,250);
            render_text.draw("Help",300,200);
            render_text.draw("Exit",300,150);
            render_text.endRendering();
        }

        if (on_menu && draw_help){
            printHelp(gld);
        }
        if (on_menu && draw_high){
            printHigh(gld);
        }

        if (!game_finished && !on_menu) {
            // Oyun burada oynanıyor

            render_text.beginRendering(800,480);
            render_text.setColor(1.0f, 1.0f, 1.0f, 1.f);
            render_text.draw("Player "+((tc%2)+1),20,430); // Sıra kimde
            render_text.draw("Shot: " + (tc + 1), 20, 380); // Kac Atis yapıldı
//            render_text.draw("Angle: "+players[tc%2].angle+"   Velocity: "+players[tc%2].v,320,430); // Açı ve hız değerleri ne ?
            render_text.endRendering();

            players[0].drawTank(gld); // 1.Tankı çiz
            players[1].drawTank(gld); // 2. Tankı çiz
            players[0].drawTurret(gld); // 1. Turreti çiz
            players[1].drawTurret(gld); // 2. Turreti çiz
            if (players[tc%2].shooting) {
            // Atış yapılırken vurdu mu diye kontrol ediliyor.
                players[tc%2].shoot(gld);
                hit_test = players[(tc+1)%2].hitTest(players[tc%2].cx,players[tc%2].cy);
                if(hit_test){
                    // vurduysa
                    players[tc%2].shooting = !hit_test;
                    players[tc%2].t = 0;
                    game_finished = true;
                    System.out.println("Kill Confirmed");

                }
            }
            if (players[tc%2].shot){
                // Atış bittiyse turu geçir
                players[tc%2].shot = false;
                players[tc%2].angle = 0;
                tc++;
    //            System.out.println("tc++");

            }
        }
        if (game_finished){
            // Oyun bitince ekrana verileri yaz
            render_text.beginRendering(800,480);
            render_text.setColor(1.0f, 1.0f, 1.0f, 1.f);
            render_text.draw("Player "+((tc%2)+1)+" Won after "+(tc+1)+" Total Shots",150,240);
            render_text.endRendering();
        }

//        System.out.println("x = "+players[0].cx+" y= "+players[0].cy);
    }

    public void printHigh(GLAutoDrawable gld) {
        render_title.beginRendering(800,480);
        render_title.setColor(1.0f, 1.0f, 1.0f, 1.f);
        render_title.draw("Highscores",100,420);
        render_title.endRendering();

        int i = 330;
        Collections.sort(highList);
        render_text.beginRendering(800,480);
        render_text.setColor(1.0f, 1.0f, 1.0f, 1.f);
        render_text.draw("Score    Name", 65, 370);
        for (String score : highList) {
            if (score.startsWith("z")) render_text.draw(score.substring(1),100,i);
            else render_text.draw(score, 100,i);
            i -= 40;
        }

        render_text.endRendering();
    }

    public void displayChanged(GLAutoDrawable gld, boolean arg1, boolean arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(GLAutoDrawable gld) {
        GL2 gl = gld.getGL().getGL2();
        GLU glu = new GLU();

        try {
            highList = fileop.readFile();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        players[0] = new Tank(15,0,"blue");
        players[1] = new Tank(700,0,"red");
        gl.glClearColor(0f, 0f, 0f, 0.0f);

        gl.glPointSize(10.0f);
        gl.glLineWidth(10.0f);

        gl.glViewport(0, 0, 800, 480);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluOrtho2D(0, 800, 0, 480);

    }


    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        if (!game_finished && e.getKeyCode() == KeyEvent.VK_ESCAPE) on_menu=true;
        if (!on_menu) gameKeys(e); // oyunun alacağı tuşlar burada
        if (on_menu && !draw_high && !draw_help)  menuKeys(e); // menünün alacaği tuşlar burada
        if (draw_help && e.getKeyCode() == KeyEvent.VK_ESCAPE ) draw_help = false;
        if (draw_high && e.getKeyCode() == KeyEvent.VK_ESCAPE ) draw_high = false;
    }

    public void menuKeys(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {

            if (mc%4 == 3) on_menu = false; // Yeni oyun secilmis
            if (mc%4 == 0) System.exit(0);
            if (mc%4 == 1) draw_help = true;
            if (mc%4 == 2) draw_high = true;

        }
        // Menü sayacını ayarla
        if (e.getKeyCode() == KeyEvent.VK_UP) mc++;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) mc--;
        if (mc < 0) mc=3;

    }


    public void gameKeys(KeyEvent e){
        if (!game_finished) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE){
                // Atışa başla
                players[tc%2].shooting = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP){
                // Açıyı arttır
                players[tc%2].angle += 2;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN){
                // Açıyı azalt
                players[tc%2].angle -= 2;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT){
                // Hızı arttır
                players[tc%2].v -= 2;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                // Hızı azalt
                players[tc%2].v += 2;

            }
        }
        if (game_finished) {
            // Oyun bittiyse
            String temp;
            if (tc+1 > 10) {
                temp = "z"+(tc+1)+"         "+"Player"+((tc%2)+1);
            }
            else temp = (tc+1)+"           "+"Player"+((tc%2)+1);
            highList.add(temp);
            try {
                fileop.writeFile(temp);
            } catch (IOException ex) {
                ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            tc = 0; // Atış sayısını sıfırla
            players[0] = new Tank(15,0,"blue"); // Değişkenleri sıfırlamak yerine yeni nesneler oluşturuyorum
            players[1] = new Tank(700,0,"red");
            game_finished = false;
            on_menu = true;
            draw_high = true;
        }

    }
    public void printHelp(GLAutoDrawable gld){
        render_title.beginRendering(800,480);
        render_title.setColor(1.0f, 1.0f, 1.0f, 1.f);
        render_title.draw("Controls",100,375);
        render_title.endRendering();


        render_text.beginRendering(800,480);
        render_text.setColor(1.0f, 1.0f, 1.0f, 1.f);
        render_text.draw("Increase Angle -> UP", 100, 300);
        render_text.draw("Decrease Angle -> DOWN",100,250);
        render_text.draw("Increase Speed -> RIGHT",100,200);
        render_text.draw("Decrease Speed -> LEFT",100,150);
        render_text.draw("Shoot -> SPACE",100,100);
        render_text.draw("Back to Menu -> ESC",100,50);
        render_text.endRendering();

    }
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reshape(GLAutoDrawable gld, int arg1, int arg2, int arg3,
                        int arg4) {
        // TODO Auto-generated method stub

    }
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
