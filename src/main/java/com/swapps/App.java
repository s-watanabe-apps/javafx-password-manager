package com.swapps;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    List<Button> buttonList;
    
    Clipboard clipboard;

    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Password Manager");

        clipboard = Clipboard.getSystemClipboard();

        // Jsonデータの読み込み
        String json = "";
        for (String buff : Files.readAllLines(Paths.get("./data.json"))) {
            json += buff;
        }

        // JsonデータをMapへ変換するためのオブジェクト
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(json, new TypeReference<Map<String,Object>>(){});

        // 検索テキストボックスの設定
        // Key入力時にリアルタイムで検索する
        final TextField search = new TextField();
        search.setOnKeyTyped(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent e) {
                String keyword = search.getText().trim();
                for (Button button : buttonList) {
                    if (button.getText().toLowerCase().contains(keyword.toLowerCase())) {
                        button.setVisible(true);
                        button.setManaged(true);
                    } else {
                        button.setVisible(false);
                        button.setManaged(false);
                    }
                }
            }
        });

        // 各種ボタンの設定
        // クリック時にパスワードをクリップボードにコピーする
        buttonList = new ArrayList<>();
        int i = 0;
        for (String key : data.keySet()) {
            Button button = new Button(key);
            button.setId(key);
            button.setAlignment(Pos.CENTER_LEFT);
            button.setUserData(data.get(key.toString()));
            button.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent e) {
                    Button target = (Button) e.getSource();
                    ClipboardContent content = new ClipboardContent();;
                    content.putString(target.getUserData().toString());
                    clipboard.setContent(content);
                }
            });

            buttonList.add(button);
            buttonList.get(i).setPrefWidth(480);
            i++;
        }

        // コントロールをパネルにセット
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(search);
        vBox.getChildren().addAll(buttonList);
 
        Scene scene = new Scene(new ScrollPane(vBox), -1, 600);
        stage.setScene(scene);
        stage.show();
    }
}
