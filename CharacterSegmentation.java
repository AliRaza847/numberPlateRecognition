package com.example.numberplaterecognition;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class CharacterSegmentation extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charactersegmentation);
    }
    private static List<BufferedImage> characterSegmentation(BufferedImage G) {
        int height = G.getHeight()-1;
        int x0 = -1;
        int x1 = -1;
        List<BufferedImage> characters = new LinkedList<>();

        for(int x = 0; x < G.getWidth(); ++x) {
            if(colHasBlackPixel(G, x)) {
                if(x0 == -1) { x0 = x; }
                x1 = x;
            } else if (x0 != -1 && x0 != x1) {
                BufferedImage subimage = G.getSubimage(x0, 0, x1-x0, height);
                characters.add(subimage);
                x0 = x1 = -1;
            }
            }
        if(x0 != -1 && x0 != x1) {
            BufferedImage subimage = G.getSubimage(x0, 0, x1-x0, height);
            characters.add(subimage);
        }

        return characters;
    }
    private static BufferedImage trim(BufferedImage G) {
        int width = G.getWidth()-1;
        int y0 = -1;
        int y1 = -1;

        for(int y = 0; y < G.getHeight(); ++y) {
            if(rowHasBlackPixel(G, y)) {
                if(y0 == -1) { y0 = y; }
                y1 = y;
            } else if (y0 != -1 && y0 != y1) {
                return G.getSubimage(0, y0, width, y1 - y0);
            }
        }

        return G.getSubimage(0, y0, width, y1 - y0);
    }
    private static boolean rowHasBlackPixel(BufferedImage G, int row) {
        for(int x = 0; x < G.getWidth(); ++x) {
            int color = Math.abs(G.getRGB(x, row));
            if(color > Bitmap.Config.THRESHOLD) { return true; }
        }
        return false;
    }
    private static boolean colHasBlackPixel(BufferedImage G, int col) {
        for(int y = 0; y < G.getHeight(); ++y) {
            int color = Math.abs(G.getRGB(col, y));
            if(color > Bitmap.Config.THRESHOLD) { return true; }
        }
        return false;
    }
}
