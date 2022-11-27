package com.zedo.svgreader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextField search;

    @FXML
    private Label page;

    @FXML
    private ListView iconLib;
    @FXML
    private FlowPane flowPane;
    @FXML
    private ChoiceBox view;
    private Path iconPath;
    private final HashMap<String, HashMap<String, String>> iconLibs = new HashMap<>();

    private String listLibName;

    private float zoom = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        iconPath = Paths.get("icons");


        view.getItems().addAll("小", "中", "大");


        HashMap<String, String> allLib = new HashMap<>();
        ObservableList list = iconLib.getItems();
        try {
            Files.list(iconPath).forEach(path -> {


                HashMap<String, String> lib = new HashMap<>();

                try {
                    Files.list(path).forEach(path1 -> {
                        try {
                            String svg = readSvg(path1);
                            lib.put(String.valueOf(path1.getFileName()), svg);
                            allLib.put(String.valueOf(path1.getFileName()), svg);
                        } catch (Exception e) {

                        }

                    });
                } catch (IOException e) {

                }


                String libName = String.valueOf(path.getFileName());
                Button button = new Button(libName);
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    listLibName = libName;
                    search.setText("");
                    upData();
                });

                HBox hBox = new HBox();
                Pane ic = new Pane();
                SVGPath svgPath = new SVGPath();
                svgPath.setContent(lib.get(lib.keySet().toArray()[0]));
                HBox.setMargin(ic,new Insets(0,6,0,0));
                ic.setShape(svgPath);
                hBox.heightProperty().addListener((observableValue, number, t1) -> {
                    ic.setPrefWidth((Double) t1);
                });
                //
                ic.setBackground(FXUtils.getBG(Color.rgb(173, 173, 173)));
                hBox.getChildren().add(ic);
                hBox.getChildren().add(button);

                list.add(hBox);


                iconLibs.put(libName, lib);

            });
        } catch (IOException e) {

        }

        iconLibs.put("All", allLib);
/*
        Button allButton = new Button("All");
        allButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            listLibName = "All";
            upData();
        });
        list.add(0, allButton);*/


        //listLibName = "All";

        view.setValue("中");
        view.valueProperty().addListener((observableValue, o, t1) -> {
            switch (t1.toString()) {
                case "小": {
                    zoom = 0.6f;
                    break;
                }
                case "中": {
                    zoom = 1f;
                    break;
                }
                case "大": {
                    zoom = 1.5f;
                    break;
                }
            }
            upData();
        });


        flowPane.widthProperty().addListener((observableValue, number, t1) -> {
            float inset = ((t1.floatValue() - 20f) % (160.f * zoom)) / ((t1.floatValue() - 20f) / (160f * zoom));
            flowPane.setPadding(new Insets(10, 10, 10, 8 + inset));
            flowPane.setHgap(inset);
        });


        search.textProperty().addListener((observableValue, number, toString) -> {
            upData();
        });


    }

    int count = 0;
    String[] searchStr;

    private void upData() {
        String searchStrTemp = search.getText();
        boolean isAll = (searchStrTemp.startsWith(".") && searchStrTemp.length() > 2);
        searchStr = (isAll ? searchStrTemp.substring(1) : searchStrTemp).split("\\|");

        ObservableList list = flowPane.getChildren();
        list.clear();

        count = 0;
        HashMap<String, String> hashMap = iconLibs.get((isAll ? "All" : listLibName));
        if (hashMap == null)
            return;
        hashMap.forEach((s, svg) -> {

            for (String ss : searchStr) {
                if (!s.contains(ss))
                    return;
            }


            VBox vBox = new VBox();
            vBox.setBorder(new Border(new BorderStroke(Color.rgb(89, 89, 89), BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2))));
            vBox.setPrefSize(160 * zoom, 160 * zoom);
            vBox.setAlignment(Pos.CENTER);
            ObservableList children = vBox.getChildren();

            Button svgButton = new Button();
            SVGPath svgPath = new SVGPath();
            svgPath.setContent(svg);
            //System.out.println(svg);
            //svgButton.setBackground(FXUtils.getBG(Color.rgb(186, 186, 186)));
            svgButton.setShape(svgPath);
            svgButton.setPrefSize(130 * zoom, 130 * zoom);
            //VBox.setVgrow(svgButton, Priority.ALWAYS);
            svgButton.getStyleClass().add("button-item");
            svgButton.setOnAction(actionEvent -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent cc = new ClipboardContent();
                cc.putString(svg);
                clipboard.setContent(cc);
            });


            //svgButton.setTextFill(Color.rgb(186, 186, 186));
            children.add(svgButton);


            Label label = new Label(s);
            label.setTooltip(new Tooltip(s));
            children.add(label);


            list.add(vBox);
            count++;
        });
        page.setText(count + "/" + iconLibs.get((isAll ? "All" : listLibName)).size());

    }


    private String readSvg(Path path) throws IOException {
        //System.out.println(path);

        StringBuilder stringBuilder = new StringBuilder();
        String clip = Files.readString(path);

        String temp;

        //String clip="<svg t=\"1657259513758\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"6050\" width=\"32\" height=\"32\"><path d=\"M703.782 179.178c31.35 18.111 60.275 40.774 85.897 67.587C858.241 318.519 896 412.714 896 512c0 51.868-10.144 102.15-30.15 149.451-19.337 45.719-47.034 86.792-82.321 122.078-35.286 35.287-76.359 62.983-122.078 82.321C614.15 885.856 563.868 896 512 896c-99.286 0-193.481-37.759-265.234-106.321-26.813-25.621-49.476-54.547-67.587-85.897C228.224 724.74 281.523 736 336 736c220.561 0 400-179.439 400-400 0-54.477-11.26-107.776-32.218-156.822M533.997 64.536C617.645 125.651 672 224.47 672 336c0 185.568-150.432 336-336 336-111.53 0-210.349-54.354-271.464-138.003C76.005 771.202 271.952 960 512 960c247.424 0 448-200.576 448-448 0-240.048-188.798-435.995-426.003-447.464zM288 224c17.6 0 32-14.4 32-32s-14.4-32-32-32h-64V96c0-17.6-14.4-32-32-32s-32 14.4-32 32v64H96c-17.6 0-32 14.4-32 32s14.4 32 32 32h64v64c0 17.6 14.4 32 32 32s32-14.4 32-32v-64h64z\" p-id=\"6051\"></path><path d=\"M416 416h-32v-32c0-17.6-14.4-32-32-32s-32 14.4-32 32v32h-32c-17.6 0-32 14.4-32 32s14.4 32 32 32h32v32c0 17.6 14.4 32 32 32s32-14.4 32-32v-32h32c17.6 0 32-14.4 32-32s-14.4-32-32-32z\" p-id=\"6052\"></path></svg>";

        if (clip == null) {
            throw new RuntimeException("剪切板为空");
        }
        if (clip.indexOf("<svg") == -1) {
            throw new RuntimeException("文件类型有误！");
        }

        temp = clip.substring(clip.indexOf("viewBox=") + "viewBox=".length() + 1);
        temp = temp.substring(0, temp.indexOf("\""));
        String[] size = temp.split(" ");
        if (size.length != 4) {
            throw new RuntimeException("没有指定大小！");
        }
        //System.out.println("temp:" + Arrays.toString(size));


        int t = 0;
        //stringBuilder.append("\"");
        while (true) {
            t = clip.indexOf("<path", t + 1);
            if (t == -1)
                break;

            temp = clip.substring(t, clip.indexOf(">", t));
            //System.out.println(temp);

            if (temp.replaceAll(" ", "").indexOf("fill=\"none\"") != -1)
                continue;
            if (temp.replaceAll(" ", "").indexOf("M" + size[0] + "," + size[1] + "h" + size[2] + "v" + size[3] + "H" + size[0] + "V" + size[1] + "z") != -1)
                continue;

            temp = temp.substring(temp.indexOf("d=\"") + "d=\"".length());
            temp = temp.substring(0, temp.indexOf("\""));
            //System.out.println(temp);
            stringBuilder.append(temp);
            stringBuilder.append(" ");
        }
        if (stringBuilder.length() == 1) {
            throw new RuntimeException("不OK！");
        }

        stringBuilder.append("M" + size[0] + "," + size[1] + "z ");
        stringBuilder.append("M" + size[2] + "," + size[3] + "z");
        //stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        //stringBuilder.append("\"");

        return stringBuilder.toString();
    }
}