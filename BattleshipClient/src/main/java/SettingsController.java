import Data.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements CustomController, Initializable {
    public Slider gameVolumeSlider;
    public Slider musicVolumeSlider;
    public Slider menuVolumeSlider;
    public Slider gameMusicVolumeSlider;
    public Button backButton;

    private int currFocus = 0;
    private int currFocusMin = 0;
    private int currFocusMax = 2;

    public void sendButtonPressed(ActionEvent actionEvent) {
        synchronized (GUIClient.clientConnection.dataManager) {
        }
    }

    private void refreshGUI() {
        synchronized (GUIClient.clientConnection.dataManager) {
            User u = GUIClient.clientConnection.dataManager.users.get(GUIClient.clientConnection.uuid);
            GUIClient.primaryStage.setTitle(u.username);
        }
    }

    @Override
    public void postInit() {
        Scene s = gameVolumeSlider.getScene();
        s.setOnKeyPressed(this::priorityKeyPress);
    }

    @Override
    public void updateUI(GUICommand command) {
        switch (command.type) {
            default:
                break;
        }
    }

    @Override
    public void onResizeWidth(Number oldVal, Number newVal) {

    }

    @Override
    public void onResizeHeight(Number oldVal, Number newVal) {

    }

    @Override
    public void onRenderUpdate(double deltaTime) {

    }

    public void buttonPressed() {
        ((HomeController) GUIClient.viewMap.get("home").controller).buttonPressed();
    }

    public void backButtonPressed(ActionEvent actionEvent) {
        buttonPressed();
        GUIClient.primaryStage.setScene(GUIClient.viewMap.get("home").scene);
    }

    public void onMouseEnteredBackButton(MouseEvent mouseEvent) {
        backButton.requestFocus();
    }

    public void onMouseInteractedSlider(MouseEvent mouseEvent) {
        buttonPressed();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GUIClient.viewMap.put("settings", new GUIView(null, this));
        gameVolumeSlider.setValue(GUIClient.volumeGame * 500.0);
        musicVolumeSlider.setValue(GUIClient.volumeMenuMusic * 500.0);
        menuVolumeSlider.setValue(GUIClient.volumeMenuSFX * 500.0);
        gameMusicVolumeSlider.setValue(GUIClient.volumeGameMusic * 500.0);
        gameVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal) {
                GUIClient.volumeGame = newVal.doubleValue() * 0.002;
            }
        });
        musicVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal) {
                GUIClient.volumeMenuMusic = newVal.doubleValue() * 0.002;
            }
        });
        menuVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal) {
                GUIClient.volumeMenuSFX = newVal.doubleValue() * 0.002;
            }
        });
        gameMusicVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal) {
                GUIClient.volumeGameMusic = newVal.doubleValue() * 0.002;
            }
        });
    }

    private void selectCurrentFocus() {
        if (currFocus == 4) {
            backButtonPressed(null);
        }
    }

    // Clamps currFocus with wrapAround
    private void clampCurrFocus() {
        if (currFocus == 5) {
            currFocus = 0;
        }
        if (currFocus == -1) {
            currFocus = 4;
        }
    }

    private void requestFocusOfCurrFocus() {
        switch (currFocus) {
            case 0:
                menuVolumeSlider.requestFocus();
                break;
            case 1:
                musicVolumeSlider.requestFocus();
                break;
            case 2:
                gameVolumeSlider.requestFocus();
                break;
            case 3:
                gameMusicVolumeSlider.requestFocus();
                break;
            case 4:
                backButton.requestFocus();
                break;
            default:
                break;
        }
        //findGameButton.requestFocus();
    }

    private void changeCurrentSlider(double by, boolean shouldBackout) {
        switch (currFocus) {
            case 0:
                menuVolumeSlider.setValue(menuVolumeSlider.getValue() + by);
                break;
            case 1:
                musicVolumeSlider.setValue(musicVolumeSlider.getValue() + by);
                break;
            case 2:
                gameVolumeSlider.setValue(gameVolumeSlider.getValue() + by);
                break;
            case 3:
                gameMusicVolumeSlider.setValue(gameMusicVolumeSlider.getValue() + by);
                break;
            case 4:
                if (shouldBackout) {
                    backButtonPressed(null);
                }
                break;
            default:
                break;
        }
    }

    public void priorityKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            if (keyEvent.getCode() == KeyCode.PERIOD) {
                GUIClient.volumeModifier += 0.05;
                if (GUIClient.volumeModifier < 0) {
                    GUIClient.volumeModifier = 0.0;
                }
            } else if (keyEvent.getCode() == KeyCode.COMMA) {
                GUIClient.volumeModifier -= 0.05;
                if (GUIClient.volumeModifier > 1) {
                    GUIClient.volumeModifier = 1.0;
                }
            } else if (keyEvent.getCode() == KeyCode.M) {
                GUIClient.volumeModifier = GUIClient.volumeModifier == 1.0 ? 0.0 : 1.0;
            }
        }
        switch (keyEvent.getCode()) {
            case W:
                currFocus--;
                clampCurrFocus();
                requestFocusOfCurrFocus();
                break;
            case S:
                currFocus++;
                clampCurrFocus();
                requestFocusOfCurrFocus();
                break;
            case D:
                changeCurrentSlider(10, false);
                break;
            case A:
                changeCurrentSlider(-10, true);
                break;
            case SPACE:
            case ENTER: {
                selectCurrentFocus();
                break;
            }
            case ESCAPE: {
                backButtonPressed(null);
                break;
            }
            default:
                break;
        }
    }
}
