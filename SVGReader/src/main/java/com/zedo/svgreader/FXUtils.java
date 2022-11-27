package com.zedo.svgreader;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class FXUtils {
    public static Background getBG(Color color){
       return new Background(new BackgroundFill(color,new CornerRadii(0),new Insets(0)));
    }
}
