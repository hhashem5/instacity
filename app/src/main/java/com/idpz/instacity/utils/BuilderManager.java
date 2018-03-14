package com.idpz.instacity.utils;

import android.graphics.Color;

import com.idpz.instacity.R;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;

/**
 * Created by Weiping Huang at 23:44 on 16/11/21
 * For Personal Open Source
 * Contact me at 2584541288@qq.com or nightonke@outlook.com
 * For more projects: https://github.com/Nightonke
 */
public class BuilderManager {

    private static int[] imageResources = new int[]{
            R.drawable.ifood,
            R.drawable.iedu,
            R.drawable.ihealth,
            R.drawable.ireligion,
            R.drawable.irepair,
            R.drawable.ishop,
            R.drawable.iservices,
            R.drawable.isport,
            R.drawable.ifood,
            R.drawable.iedu,
            R.drawable.ihealth,
            R.drawable.ireligion,
            R.drawable.irepair,
            R.drawable.ishop,
            R.drawable.iservices,
            R.drawable.isport
    };
    private static int[] textreses=new int[]{
            R.string.app_name,

    };

    static int gettextResource() {
        if (textResourceIndex >= textreses.length) textResourceIndex = 0;
        return textreses[textResourceIndex++];
    }
    private static int imageResourceIndex = 0;
    private static int textResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    static SimpleCircleButton.Builder getSimpleCircleButtonBuilder() {
        return new SimpleCircleButton.Builder()
                .normalImageRes(getImageResource());
    }

    static TextInsideCircleButton.Builder getTextInsideCircleButtonBuilder() {
        return new TextInsideCircleButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(gettextResource());
    }



    public static TextOutsideCircleButton.Builder getTextOutsideCircleButtonBuilder() {
        return new TextOutsideCircleButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.text_outside_circle_button_text_normal);
    }

    public static TextOutsideCircleButton.Builder getTextOutsideCircleButtonBuilderWithDifferentPieceColor() {
        return new TextOutsideCircleButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.text_outside_circle_button_text_normal)
                .pieceColor(Color.WHITE);
    }

    static HamButton.Builder getHamButtonBuilder() {
        return new HamButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.text_ham_button_text_normal)
                .subNormalTextRes(R.string.text_ham_button_sub_text_normal);
    }

    static HamButton.Builder getHamButtonBuilderWithDifferentPieceColor() {
        return new HamButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(R.string.text_ham_button_text_normal)
                .subNormalTextRes(R.string.text_ham_button_sub_text_normal)
                .pieceColor(Color.WHITE);
    }

    private static BuilderManager ourInstance = new BuilderManager();

    public static BuilderManager getInstance() {
        return ourInstance;
    }

    private BuilderManager() {
    }
}
