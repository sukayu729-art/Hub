package com.ashenrealms.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) { super.onCreate(b); getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512); setContentView(new GameView()); }

    class GameView extends View {
        Paint p = new Paint(3); Random rand = new Random();
        float px=0, py=0, vx=0, vy=0; int hp=100, xp=0, level=1, gold=0, potions=3, kills=0;
        long last=System.currentTimeMillis(), attackUntil=0, hurtUntil=0;
        ArrayList<Slime> slimes = new ArrayList<>();
        float joyX, joyY, joyDX, joyDY; boolean joy=false; int joyPointer=-1;
        boolean attack=false; float scale, cx, cy;
        String quest="Defeat 5 slimes";
        GameView(){ super(MainActivity.this); setFocusable(true); for(int i=0;i<5;i++) slimes.add(new Slime((rand.nextFloat()-.5f)*700,(rand.nextFloat()-.5f)*400)); }
        static class Slime { float x,y,hp=30; Slime(float a,float b){x=a;y=b;} }
        void text(Canvas c,String s,float x,float y,float size,int color){p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));c.drawText(s,x,y,p);}
        protected void onDraw(Canvas c){
            super.onDraw(c); float w=getWidth(),h=getHeight(); scale=Math.min(w/900f,h/500f); cx=w/2;cy=h/2;
            p.setColor(Color.rgb(27,39,38));c.drawColor(p.getColor());
            for(int y=0;y<h;y+=42*scale) for(int x=0;x<w;x+=42*scale){p.setColor(((x/(int)Math.max(1,42*scale)+y/(int)Math.max(1,42*scale))%2==0)?0xff30483c:0xff2b4037);c.drawRect(x,y,x+42*scale,y+42*scale,p);}
            c.save(); c.translate(cx-px*scale,cy-py*scale);
            p.setColor(0xff655b4b);c.drawRect(-700*scale,-450*scale,700*scale,450*scale,p);
            p.setColor(0xff88795e);p.setStrokeWidth(70*scale);c.drawLine(-650*scale,100*scale,650*scale,-100*scale,p);c.drawLine(-80*scale,-430*scale,60*scale,430*scale,p);
            for(int i=0;i<18;i++){float x=(i*113%1300)-650,y=(i*197%820)-410;p.setColor(0xff496748);c.drawCircle(x*scale,y*scale,12*scale,p);}
            for(Slime s:slimes){p.setColor(0x55000000);c.drawOval((s.x-20)*scale,(s.y+10)*scale,(s.x+20)*scale,(s.y+20)*scale,p);p.setColor(0xff68d86d);c.drawOval((s.x-20)*scale,(s.y-18)*scale,(s.x+20)*scale,(s.y+18)*scale,p);p.setColor(0xff17391c);c.drawCircle((s.x-7)*scale,s.y*scale,3*scale,p);c.drawCircle((s.x+7)*scale,s.y*scale,3*scale,p);p.setColor(0xffe9f3d6);c.drawRect((s.x-20)*scale,(s.y-28)*scale,(s.x+20)*scale,(s.y-24)*scale,p);p.setColor(0xffdf5151);c.drawRect((s.x-20)*scale,(s.y-28)*scale,(s.x-20+40*s.hp/30)*scale,(s.y-24)*scale,p);}
            p.setColor(0x66000000);c.drawOval(-18*scale,8*scale,18*scale,18*scale,p);p.setColor(0xff4ea5e8);c.drawRoundRect(-15*scale,-22*scale,15*scale,15*scale,7*scale,7*scale,p);p.setColor(0xffffd2a1);c.drawCircle(0,-25*scale,10*scale,p);p.setColor(0xff28354d);c.drawRect(-10*scale,-31*scale,10*scale,-28*scale,p);
            if(System.currentTimeMillis()<attackUntil){p.setColor(0xffffef99);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(5*scale);c.drawCircle(0,0,48*scale,p);p.setStyle(Paint.Style.FILL);}
            c.restore();
            p.setColor(0xdd101719);c.drawRoundRect(14,14,300*scale,102*scale,14,14,p);
            text(c,"ASHEN REALMS",26*scale,38*scale,18*scale,0xffffd98b);
            p.setColor(0xff3e4749);c.drawRoundRect(26*scale,49*scale,210*scale,64*scale,5,5,p);p.setColor(0xffd94c55);c.drawRoundRect(26*scale,49*scale,(26+184*hp/100f)*scale,64*scale,5,5,p);
            text(c,"HP "+hp+"   LV "+level,26*scale,83*scale,14*scale,Color.WHITE);text(c,"XP "+xp+"   GOLD "+gold,26*scale,100*scale,12*scale,0xffffd98b);
            text(c,quest+" ("+kills+"/5)",w-245*scale,30*scale,14*scale,Color.WHITE);
            float jx=95*scale,jy=h-95*scale;p.setColor(0x663b454a);c.drawCircle(jx,jy,58*scale,p);p.setColor(0x998a9da3);c.drawCircle(jx+joyDX*38*scale,jy+joyDY*38*scale,24*scale,p);
            button(c,w-135*scale,h-95*scale,"ATTACK",0xffb54c4c);button(c,w-135*scale,h-205*scale,"POTION",0xff498f6a);
            text(c,"Move",65*scale,h-20*scale,12*scale,0xffd9e4de);
            if(hp<=0){p.setColor(0xdd101010);c.drawRect(0,0,w,h,p);text(c,"YOU FELL",w/2-70*scale,h/2,30*scale,Color.WHITE);text(c,"Tap to respawn",w/2-75*scale,h/2+35*scale,16*scale,0xffffd98b);}
        }
        void button(Canvas c,float x,float y,String label,int color){p.setColor(0xdd101719);c.drawCircle(x,y,48*scale,p);p.setColor(color);c.drawCircle(x,y,39*scale,p);text(c,label,x-31*scale,y+5*scale,13*scale,Color.WHITE);}
        public boolean onTouchEvent(android.view.MotionEvent e){
            int a=e.getActionMasked(), idx=e.getActionIndex();float x=e.getX(idx),y=e.getY(idx),w=getWidth(),h=getHeight();
            if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_POINTER_DOWN){
                if(hp<=0){hp=100;px=py=0;invalidate();return true;}
                if(x>w-190*scale && y>h-155*scale){doAttack();return true;}
                if(x>w-190*scale && y>h-260*scale && y<h-150*scale){if(potions>0&&hp<100){hp=Math.min(100,hp+40);potions--;}invalidate();return true;}
                if(x<w*.35f && y>h*.52f){joy=true;joyPointer=e.getPointerId(idx);updateJoy(x,y);}
                return true;
            }
            if(a==MotionEvent.ACTION_MOVE&&joy){int i=e.findPointerIndex(joyPointer);if(i>=0)updateJoy(e.getX(i),e.getY(i));return true;}
            if(a==MotionEvent.ACTION_UP||a==MotionEvent.ACTION_POINTER_UP||a==MotionEvent.ACTION_CANCEL){if(e.getPointerId(idx)==joyPointer){joy=false;joyDX=joyDY=0;}return true;}
            return true;
        }
        void updateJoy(float x,float y){float dx=x-95*scale,dy=y-(getHeight()-95*scale),len=(float)Math.sqrt(dx*dx+dy*dy),max=55*scale;if(len>max){dx*=max/len;dy*=max/len;}joyDX=dx/max;joyDY=dy/max;}
        void doAttack(){long now=System.currentTimeMillis();attackUntil=now+250;for(Slime s:slimes)if(Math.hypot(s.x-px,s.y-py)<95){s.hp-=18;if(s.hp<=0){s.x=px+(rand.nextFloat()-.5f)*600;s.y=py+(rand.nextFloat()-.5f)*400;s.hp=30;xp+=20;gold+=5;kills++;if(xp>=level*60){xp-=level*60;level++;hp=100;}if(kills>=5)quest="Quest complete!";}}invalidate();}
        void tick(){long now=System.currentTimeMillis();float dt=Math.min(.04f,(now-last)/1000f);last=now;if(hp>0){px+=joyDX*220*dt;py+=joyDY*220*dt;for(Slime s:slimes){float dx=px-s.x,dy=py-s.y,d=(float)Math.hypot(dx,dy);if(d>28&&d<500){s.x+=dx/d*45*dt;s.y+=dy/d*45*dt;}if(d<30&&now>hurtUntil){hp=Math.max(0,hp-5);hurtUntil=now+800;}}}invalidate();}
        @Override protected void onAttachedToWindow(){super.onAttachedToWindow();post(new Runnable(){public void run(){tick();postDelayed(this,33);}});}
    }
}
