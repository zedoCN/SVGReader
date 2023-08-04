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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

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
        iconPath = Paths.get("/home/zedo/project/ZXNoter/res/resources/base-pack/icon/line/");


        view.getItems().addAll("小", "中", "大");


        HashMap<String, String> allLib = new HashMap<>();
        ObservableList list = iconLib.getItems();
        try {
            Files.list(iconPath).forEach(path -> {


                HashMap<String, String> lib = new HashMap<>();

                try {
                    Files.list(path).forEach(path1 -> {
                        try {
                            if (path1.getFileName().toString().endsWith(".svg")) {
                                String svg = readSvg(path1);
                                lib.put(String.valueOf(path1.getFileName()), svg);
                                allLib.put(String.valueOf(path1.getFileName()), svg);
                            }
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
                if (lib.keySet().size()==0)
                    return;
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


    public static String readSvg(Path path) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 使用 try-with-resources 确保解析器和输入流在使用后自动关闭
            try (var inputStream = Files.newInputStream(path)) {
                Document document = builder.parse(inputStream);

                StringBuilder stringBuilder = new StringBuilder();

                Element svgElement = document.getDocumentElement();
                String viewBox = svgElement.getAttribute("viewBox");
                String[] size = viewBox.split(" ");
                if (size.length != 4) {
                    throw new RuntimeException("没有指定大小！");
                }

                // 使用 Stream API 简化元素遍历和过滤的操作
                NodeList pathList = svgElement.getElementsByTagName("path");
                IntStream.range(0, pathList.getLength())
                        .mapToObj(pathList::item)
                        .filter(node -> node instanceof Element)
                        .map(node -> (Element) node)
                        .filter(element -> !element.getAttribute("fill").equalsIgnoreCase("none"))
                        .map(element -> element.getAttribute("d"))
                        .filter(dAttribute -> !dAttribute.equalsIgnoreCase("M" + size[0] + "," + size[1] + "h" + size[2] + "v" + size[3] + "h-" + size[2] + "Z"))
                        .forEach(dAttribute -> stringBuilder.append(dAttribute).append(" "));

                // 处理 <polygon> 元素
                NodeList polygonList = svgElement.getElementsByTagName("polygon");
                IntStream.range(0, polygonList.getLength())
                        .mapToObj(polygonList::item)
                        .filter(node -> node instanceof Element)
                        .map(node -> (Element) node)
                        .map(element -> element.getAttribute("points"))
                        .map(points -> convertPolygonToPath(points, size))
                        .forEach(pathData -> stringBuilder.append(pathData).append(" "));


                if (stringBuilder.length() == 0) {
                    throw new RuntimeException("不OK！");
                }

                stringBuilder.append("M").append(size[0]).append(",").append(size[1]).append("z ");
                stringBuilder.append("M").append(size[2]).append(",").append(size[3]).append("z");

                return stringBuilder.toString();
            }

        } catch (ParserConfigurationException | org.xml.sax.SAXException e) {
            e.printStackTrace();
            throw new IOException("解析 SVG 文件时出错！");
        }
    }
    private static String convertPolygonToPath(String points, String[] size) {
        StringBuilder pathBuilder = new StringBuilder("M");
        String[] coordinates = points.split("\\s+");

        for (int i = 0; i < coordinates.length; i++) {
            String coordinate = coordinates[i];
            String[] xy = coordinate.split(",");
            double x = Double.parseDouble(xy[0]);
            double y = Double.parseDouble(xy[1]);

            if (i == 0) {
                pathBuilder.append(x).append(",").append(y);
            } else {
                pathBuilder.append(" L").append(x).append(",").append(y);
            }
        }

        pathBuilder.append(" Z");
        return pathBuilder.toString();
    }

}