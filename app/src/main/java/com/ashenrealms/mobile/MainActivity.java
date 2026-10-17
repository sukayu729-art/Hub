package com.ashenrealms.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        setContentView(new GameView());
    }

    class GameView extends View {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random rand = new Random(7);
        float px=0, py=0, joyDX=0, joyDY=0, scale=1, cx, cy, joyOriginX, joyOriginY;
        int hp=100, xp=0, level=1, gold=0, potions=3, kills=0;
        long last=System.currentTimeMillis(), attackUntil=0, hurtUntil=0;
        ArrayList<Wraith> foes = new ArrayList<>();
        boolean joy=false; int joyPointer=-1;
        GameView() {
            super(MainActivity.this); setFocusable(true);
            for(int i=0;i<7;i++) foes.add(new Wraith((rand.nextFloat()-.5f)*1000,(rand.nextFloat()-.5f)*700));
        }
        class Wraith {
            float x,y,hp=42, phase;
            Wraith(float a,float b){x=a;y=b;phase=rand.nextFloat()*6.28f;}
        }
        void fill(Canvas c,int color){p.setColor(color);p.setStyle(Paint.Style.FILL);}
        void text(Canvas c,String s,float x,float y,float size,int color){
            p.setStyle(Paint.Style.FILL);p.setColor(color);p.setTextSize(size);
            p.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));c.drawText(s,x,y,p);
        }
        void panel(Canvas c,float l,float t,float r,float b){
            fill(c,0xe916171d);c.drawRoundRect(l,t,r,b,13*scale,13*scale,p);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.3f*scale);p.setColor(0xff786144);
            c.drawRoundRect(l,t,r,b,13*scale,13*scale,p);p.setStyle(Paint.Style.FILL);
        }
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            float w=getWidth(),h=getHeight();scale=Math.max(.65f,Math.min(w/900f,h/500f));cx=w/2;cy=h/2;
            c.drawColor(0xff101619);
            // Mossy stone ground with irregular cracks and scattered leaves.
            for(int yy=-1;yy<h/(48*scale)+2;yy++) for(int xx=-1;xx<w/(48*scale)+2;xx++){
                float x=xx*48*scale,y=yy*48*scale;
                int v=(Math.abs(xx*17+yy*31)%5);
                fill(c,v==0?0xff29332f:(v==1?0xff252f2d:0xff222b29));
                c.drawRect(x,y,x+47*scale,y+47*scale,p);
                if(v==2){p.setColor(0xff3c4035);p.setStrokeWidth(scale);c.drawLine(x+9*scale,y+13*scale,x+18*scale,y+19*scale,p);c.drawLine(x+18*scale,y+19*scale,x+15*scale,y+25*scale,p);}
                if(v==4){fill(c,0xff424238);c.drawCircle(x+31*scale,y+12*scale,2*scale,p);}
            }
            c.save();c.translate(cx-px*scale,cy-py*scale);
            // Ancient road and broken rune stones.
            fill(c,0xff4a4940);p.setStrokeWidth(94*scale);c.drawLine(-850*scale,180*scale,850*scale,-180*scale,p);
            p.setColor(0xff777263);p.setStrokeWidth(2*scale);
            for(int i=-8;i<9;i++){float x=i*100*scale,y=-i*45*scale;c.drawLine(x-20*scale,y-35*scale,x+25*scale,y+35*scale,p);}
            // Dead trees, roots and rocks frame the arena.
            for(int i=0;i<27;i++){
                float x=((i*173)%1500-750),y=((i*239)%1050-525);
                if(Math.hypot(x-px,y-py)<70) continue;
                if(i%3==0) drawRock(c,x,y);
                else drawTree(c,x,y, i%4==0);
            }
            // Broken shrine.
            for(int i=0;i<4;i++){float x=230*scale+(i%2)*44*scale,y=-115*scale+(i/2)*44*scale;
                fill(c,0xff4d514a);c.drawRoundRect(x,y,x+32*scale,y+34*scale,3*scale,3*scale,p);
                p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2*scale);p.setColor(0xffa18a60);
                c.drawRect(x+7*scale,y+7*scale,x+25*scale,y+27*scale,p);p.setStyle(Paint.Style.FILL);
            }
            for(Wraith s:foes) drawWraith(c,s);
            drawHero(c);
            if(System.currentTimeMillis()<attackUntil){
                p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(5*scale);p.setColor(0xffd7b46a);
                c.drawCircle(0,0,53*scale,p);p.setStyle(Paint.Style.FILL);
                p.setColor(0x55d7b46a);c.drawCircle(0,0,48*scale,p);
            }
            c.restore();
            // Vignette-like edge shade.
            p.setShader(new RadialGradient(w/2,h/2,Math.max(w,h)*.72f,new int[]{0x00000000,0x77000000},null,Shader.TileMode.CLAMP));
            c.drawRect(0,0,w,h,p);p.setShader(null);
            // Heads-up display.
            panel(c,12*scale,12*scale,286*scale,116*scale);
            text(c,"ASHEN REALMS",25*scale,36*scale,20*scale,0xffe4c58a);
            text(c,"THE SUNKEN MARCH",25*scale,52*scale,9*scale,0xff8d9b92);
            fill(c,0xff343b3a);c.drawRoundRect(25*scale,61*scale,250*scale,75*scale,5*scale,5*scale,p);
            fill(c,0xffa83d45);c.drawRoundRect(25*scale,61*scale,(25+225*hp/100f)*scale,75*scale,5*scale,5*scale,p);
            text(c,"HP  "+hp+" / 100",29*scale,72*scale,9*scale,Color.WHITE);
            text(c,"LV "+level+"     XP "+xp+"     GOLD "+gold,25*scale,96*scale,12*scale,0xffd5d0bb);
            panel(c,w-220*scale,12*scale,w-12*scale,61*scale);
            text(c,"HUNT THE SHADES",w-205*scale,32*scale,11*scale,0xffe4c58a);
            text(c,kills+" / 5 defeated",w-205*scale,48*scale,12*scale,0xffd5d0bb);
            // Mobile controls.
            float jx=88*scale,jy=h-91*scale;
            fill(c,0x883b4548);c.drawCircle(jx,jy,57*scale,p);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2*scale);p.setColor(0xff65716e);c.drawCircle(jx,jy,57*scale,p);p.setStyle(Paint.Style.FILL);
            fill(c,0xff7e8982);c.drawCircle(jx+joyDX*35*scale,jy+joyDY*35*scale,23*scale,p);
            actionButton(c,w-83*scale,h-92*scale,"STRIKE",0xff82423e);
            actionButton(c,w-83*scale,h-190*scale,"TONIC",0xff466e58);
            text(c,"MOVE",61*scale,h-18*scale,10*scale,0xffc4c7b9);
            if(hp<=0){fill(c,0xe80b0d10);c.drawRect(0,0,w,h,p);text(c,"YOU HAVE FALLEN",w/2-105*scale,h/2-8*scale,25*scale,0xffe4c58a);text(c,"Tap anywhere to rise again",w/2-105*scale,h/2+24*scale,13*scale,Color.WHITE);}
            if(kills>=5){panel(c,w/2-112*scale,70*scale,w/2+112*scale,108*scale);text(c,"THE SHADOWS RECEDE",w/2-95*scale,94*scale,14*scale,0xffe4c58a);}
        }
        void drawTree(Canvas c,float x,float y,boolean dead){
            fill(c,0x66000000);c.drawOval((x-20)*scale,(y+7)*scale,(x+22)*scale,(y+19)*scale,p);
            fill(c,dead?0xff292a27:0xff3b3930);c.drawRoundRect((x-5)*scale,(y-12)*scale,(x+6)*scale,(y+17)*scale,3*scale,3*scale,p);
            p.setStrokeWidth(5*scale);p.setColor(dead?0xff37352e:0xff343c32);c.drawLine(x,y,x-17*scale,y-20*scale,p);c.drawLine(x,y-2*scale,x+18*scale,y-25*scale,p);
            fill(c,dead?0xff373832:0xff303b31);c.drawCircle(x*scale,y*scale-17*scale,17*scale,p);
            fill(c,dead?0xff2b2d2b:0xff394738);c.drawCircle((x-8)*scale,(y-21)*scale,10*scale,p);
        }
        void drawRock(Canvas c,float x,float y){
            fill(c,0x77000000);c.drawOval((x-17)*scale,(y+5)*scale,(x+18)*scale,(y+13)*scale,p);
            fill(c,0xff50524a);Path path=new Path();path.moveTo((x-17)*scale,y*scale);path.lineTo((x-7)*scale,(y-13)*scale);path.lineTo((x+10)*scale,(y-10)*scale);path.lineTo((x+18)*scale,y*scale);path.close();c.drawPath(path,p);
            p.setColor(0xff777568);p.setStrokeWidth(2*scale);c.drawLine((x-7)*scale,(y-11)*scale,(x+5)*scale,(y-8)*scale,p);
        }
        void drawWraith(Canvas c,Wraith s){
            float bob=(float)Math.sin(System.currentTimeMillis()/220.0+s.phase)*3*scale;
            fill(c,0x77000000);c.drawOval((s.x-19)*scale,(s.y+12)*scale,(s.x+19)*scale,(s.y+22)*scale,p);
            fill(c,0xff292b37);Path hood=new Path();hood.moveTo(s.x*scale,(s.y-29*scale)+bob);hood.quadTo((s.x-24)*scale,(s.y-16*scale)+bob,(s.x-18)*scale,(s.y+17*scale)+bob);hood.quadTo(s.x*scale,(s.y+29*scale)+bob,(s.x+18)*scale,(s.y+17*scale)+bob);hood.quadTo((s.x+24)*scale,(s.y-16*scale)+bob,s.x*scale,(s.y-29*scale)+bob);c.drawPath(hood,p);
            fill(c,0xffa9b5a6);c.drawCircle((s.x-6)*scale,(s.y-9*scale)+bob,2.4f*scale,p);c.drawCircle((s.x+6)*scale,(s.y-9*scale)+bob,2.4f*scale,p);
            fill(c,0xff1b2026);c.drawRect((s.x-19)*scale,(s.y-35*scale)+bob,(s.x+19)*scale,(s.y-31*scale)+bob,p);
            fill(c,0xff4a3434);c.drawRect((s.x-19)*scale,(s.y-35*scale)+bob,(s.x-19+38*s.hp/42f)*scale,(s.y-32*scale)+bob,p);
        }
        void drawHero(Canvas c){
            fill(c,0x88000000);c.drawOval(-20*scale,10*scale,20*scale,21*scale,p);
            fill(c,0xff20242b);Path cloak=new Path();cloak.moveTo(0,-27*scale);cloak.lineTo(-18*scale,12*scale);cloak.quadTo(0,21*scale,18*scale,12*scale);cloak.close();c.drawPath(cloak,p);
            fill(c,0xff9c343c);c.drawRoundRect(-10*scale,-25*scale,10*scale,4*scale,5*scale,5*scale,p);
            fill(c,0xffd6b994);c.drawCircle(0,-26*scale,8*scale,p);
            fill(c,0xff272a30);c.drawArc(-10*scale,-35*scale,10*scale,-18*scale,180,180,true,p);
            p.setStrokeWidth(3*scale);p.setColor(0xffb8b5a8);c.drawLine(12*scale,-4*scale,23*scale,-17*scale,p);p.setStrokeWidth(1);
        }
        void actionButton(Canvas c,float x,float y,String label,int color){
            fill(c,0xe90d1014);c.drawCircle(x,y,43*scale,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2*scale);p.setColor(0xff88724f);c.drawCircle(x,y,43*scale,p);p.setStyle(Paint.Style.FILL);
            fill(c,color);c.drawCircle(x,y,34*scale,p);text(c,label,x-26*scale,y+4*scale,11*scale,Color.WHITE);
        }
        @Override public boolean onTouchEvent(MotionEvent e){
            int a=e.getActionMasked(),idx=e.getActionIndex();float x=e.getX(idx),y=e.getY(idx),w=getWidth(),h=getHeight();
            if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_POINTER_DOWN){
                if(hp<=0){hp=100;px=py=0;invalidate();return true;}
                if(x>w-150*scale&&y>h-145*scale){attack();return true;}
                if(x>w-150*scale&&y>h-240*scale&&y<h-145*scale){if(potions>0&&hp<100){hp=Math.min(100,hp+40);potions--;}invalidate();return true;}
                if(x<w*.48f&&y>h*.42f){joy=true;joyPointer=e.getPointerId(idx);joyOriginX=x;joyOriginY=y;updateJoy(x,y);}
                return true;
            }
            if(a==MotionEvent.ACTION_MOVE&&joy){int i=e.findPointerIndex(joyPointer);if(i>=0)updateJoy(e.getX(i),e.getY(i));return true;}
            if(a==MotionEvent.ACTION_UP||a==MotionEvent.ACTION_POINTER_UP||a==MotionEvent.ACTION_CANCEL){if(e.getPointerId(idx)==joyPointer){joy=false;joyDX=joyDY=0;}return true;}
            return true;
        }
        void updateJoy(float x,float y){float dx=x-joyOriginX,dy=y-joyOriginY,len=(float)Math.hypot(dx,dy),max=55*scale;if(len>max){dx*=max/len;dy*=max/len;}joyDX=dx/max;joyDY=dy/max;invalidate();}
        void attack(){
            long now=System.currentTimeMillis();attackUntil=now+260;
            for(Wraith s:foes)if(Math.hypot(s.x-px,s.y-py)<105){s.hp-=22;if(s.hp<=0){s.x=px+(rand.nextFloat()-.5f)*750;s.y=py+(rand.nextFloat()-.5f)*550;s.hp=42;xp+=20;gold+=7;kills++;if(xp>=level*70){xp-=level*70;level++;hp=100;}if(kills>=5)kills=5;}}
            invalidate();
        }
        void tick(){
            long now=System.currentTimeMillis();float dt=Math.min(.04f,(now-last)/1000f);last=now;
            if(hp>0){px+=joyDX*225*dt;py+=joyDY*225*dt;
                for(Wraith s:foes){float dx=px-s.x,dy=py-s.y,d=(float)Math.hypot(dx,dy);if(d>32&&d<470){s.x+=dx/d*34*dt;s.y+=dy/d*34*dt;}if(d<31&&now>hurtUntil){hp=Math.max(0,hp-7);hurtUntil=now+850;}}
            }invalidate();
        }
        @Override protected void onAttachedToWindow(){super.onAttachedToWindow();post(new Runnable(){public void run(){tick();postDelayed(this,33);}});}
    }
}