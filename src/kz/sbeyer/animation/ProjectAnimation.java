package kz.sbeyer.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class ProjectAnimation {

	//Перемещение фильтра наверх экрана
    public static void goTop(View v){
        final View target = v;
        final View targetParent = (View) target.getParent();
        Animation a1 = new TranslateAnimation(0.0f,
                0.0f,0.0f, -target.getTop());
        
        a1.setDuration(300);
        a1.setFillAfter(true);
        target.startAnimation(a1);
        a1.setAnimationListener(
        		new Animation.AnimationListener() {
				    @Override
					public void onAnimationEnd(Animation animation) {
				    	Animation a2 = new TranslateAnimation(0.0f,
				                0.0f,0.0f,0.0f);
				    	target.startAnimation(a2);
				    }
			
				    @Override
				    public void onAnimationRepeat(Animation animation) {
				    }
			
				    @Override
				    public void onAnimationStart(Animation animation) {
				    }
        		}
        );
    }
    
    //Перемещение элемента за верхнюю границу экрана
    public static void hideTop(View v, int coef){
        final View target = v;
        final View targetParent = (View) target.getParent();
        Animation a1 = new TranslateAnimation(0.0f,
                0.0f,0.0f, -target.getTop()-(target.getHeight()*coef));
        
        a1.setDuration(300);
        a1.setFillAfter(true);
        target.startAnimation(a1);
        a1.setAnimationListener(
        		new Animation.AnimationListener() {
				    @Override
					public void onAnimationEnd(Animation animation) {
				    	target.setVisibility(View.GONE);
				    }
			
				    @Override
				    public void onAnimationRepeat(Animation animation) {
				    }
			
				    @Override
				    public void onAnimationStart(Animation animation) {
				    }
        		}
        );
    }
    
    //Перемещение элемента за верхнюю границу экрана
    public static void hideRight(View v){
        final View target = v;
        final View targetParent = (View) target.getParent();
        Animation a1 = new TranslateAnimation(0.0f,
        		-target.getWidth(),0.0f, 0.0f);
        
        a1.setDuration(350);
        a1.setFillAfter(true);
        target.startAnimation(a1);
        a1.setAnimationListener(
        		new Animation.AnimationListener() {
				    @Override
					public void onAnimationEnd(Animation animation) {
				    	target.setVisibility(View.GONE);
				    }
			
				    @Override
				    public void onAnimationRepeat(Animation animation) {
				    }
			
				    @Override
				    public void onAnimationStart(Animation animation) {
				    }
        		}
        );
    }

    //Перемещение элемента за верхнюю границу экрана
    public static void hideLeft(View v){
        final View target = v;
        final View targetParent = (View) target.getParent();
        Animation a1 = new TranslateAnimation(0.0f,target.getWidth(),0.0f, 0.0f);
        
        a1.setDuration(350);
        a1.setInterpolator(new AccelerateInterpolator());
        a1.setFillAfter(true);
        target.startAnimation(a1);
        a1.setAnimationListener(
        		new Animation.AnimationListener() {
				    @Override
					public void onAnimationEnd(Animation animation) {
				    	target.setVisibility(View.GONE);
				    }
			
				    @Override
				    public void onAnimationRepeat(Animation animation) {
				    }
			
				    @Override
				    public void onAnimationStart(Animation animation) {
				    }
        		}
        );
    }
    
    //Перемещение элемента за нижнюю границу экрана
    public static void hideBottom(View v){
        final View target = v;
        final View targetParent = (View) target.getParent();
        Animation a1 = new TranslateAnimation(0.0f,
                0.0f,0.0f, targetParent.getHeight());
        
        a1.setDuration(300);
        a1.setFillAfter(true);
        target.startAnimation(a1);
        a1.setAnimationListener(
        		new Animation.AnimationListener() {
				    @Override
					public void onAnimationEnd(Animation animation) {
				    	target.setVisibility(View.GONE);
				    }
			
				    @Override
				    public void onAnimationRepeat(Animation animation) {
				    }
			
				    @Override
				    public void onAnimationStart(Animation animation) {
				    }
        		}
        );
    }

    //Возвращение элемента списка на исходную позицию с верхней стороны
    public static void showInitialTop(View v, int coef){
        final View target = v;
        final View targetParent = (View) target.getParent();
        Animation a1 = new TranslateAnimation(0.0f,
                0.0f,-target.getTop()-(target.getHeight()*coef),0.0f);
        
        a1.setDuration(300);
        a1.setFillAfter(true);
        target.startAnimation(a1);
    }
    //Возвращение элемента списка на исходную позицию с нижней стороны
    public static void showInitialBottom(View v){
        final View target = v;
        final View targetParent = (View) target.getParent();
        Animation a1 = new TranslateAnimation(0.0f,
                0.0f,targetParent.getHeight(),0.0f);
        
        a1.setDuration(300);
        a1.setFillAfter(true);
        target.startAnimation(a1);
    }
}
