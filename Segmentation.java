package com.example.numberplaterecog;

import common.Config;
import common.ImageHelper;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class Segmentation{


    public List<BufferedImage> segment(BufferedImage G) {
        List<BufferedImage> separatedChars = new LinkedList<>();
        List<BufferedImage> lines = lineSegmentation(G);

        for (BufferedImage line : lines)
        {
            List<BufferedImage> characters = characterSegmentation(line);
            for (BufferedImage character : characters) {
                BufferedImage trimmed = trim(character);
                separatedChars.add(trimmed);
            }
        }
        return separatedChars;
    }
    private static List<BufferedImage> lineSegmentation(BufferedImage G) {
        int width = G.getWidth() - 1;
        int y0 = -1;
        int y1 = -1;
        List<BufferedImage> lines = new LinkedList<>();

        for (int y = 0; y < G.getHeight(); ++y) {
            if (rowHasBlackPixel(G, y)) {
                if (y0 == -1) {
                    y0 = y;
                }
                y1 = y;
            } else if (y0 != -1 && y0 != y1) {
                BufferedImage subimage = G.getSubimage(0, y0, width, y1 - y0);
                lines.add(subimage);
                y0 = y1 = -1;
            }
        }
        if (y0 != -1 && y0 != y1) {
            BufferedImage subimage = G.getSubimage(0, y0, width, y1 - y2);
            lines.add(subimage);
        }
        return lines;
    }
}
