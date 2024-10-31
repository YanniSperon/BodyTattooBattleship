package GameScene.Components;

import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import static com.sun.javafx.util.Utils.clamp;

public class OrbitalCameraComponent extends Component {

    public PerspectiveCamera camera;

    public OrbitalCameraComponent() {
        this.type = ComponentType.ORBITAL_CAMERA;
    }

    @Override
    public void onAdded() {
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.01);
        camera.setFarClip(100.0);
        camera.setFieldOfView(60);
        camera.setVerticalFieldOfView(true);
        gameObject.childrenHolder.getChildren().add(camera);
    }

    public void focusCamera() {
        gameObject.childrenHolder.getScene().setCamera(camera);
    }

    public double mouseSensitivityX = 1.0;
    public double mouseSensitivityY = 1.0;
    public double scrollSpeed = 0.025;
    public double panSpeed = 0.05;
    public double minDistToCenter = 0.005;
    public double maxDistFromCenter = 20.0;

    private double mouseX;
    private double oldMouseX;
    private double mouseY;
    private double oldMouseY;
    private static boolean SHOULD_USE_KEYBOARD_CONTROLS = true;
    @Override
    public boolean onMouseEvent(MouseEvent mouseEvent) {
        if (!SHOULD_USE_KEYBOARD_CONTROLS) {
            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                mouseX = mouseEvent.getSceneX();
                mouseY = mouseEvent.getSceneY();
            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                oldMouseX = mouseX;
                oldMouseY = mouseY;

                mouseX = mouseEvent.getSceneX();
                mouseY = mouseEvent.getSceneY();

                if (mouseEvent.isMiddleButtonDown()) {
                    if (!mouseEvent.isShiftDown()) {
                        // Orbit
                        orbitCamera();
                    } else {
                        // Pan
                        gameObject.setTranslation(gameObject.getTranslation().add(getRightDirectionWS().multiply(-(mouseX - oldMouseX) * panSpeed).add(getUpDirectionWS().multiply((mouseY - oldMouseY) * panSpeed))));
                    }
                    return true;
                } else if (mouseEvent.isPrimaryButtonDown()) {
                    if (mouseEvent.isShiftDown()) {
                        // Orbit
                        orbitCamera();
                    }
                }
            }
        }
        return false;
    }

    private void orbitCamera() {
        gameObject.setXRotation(clamp(-85.0, gameObject.getXRotation() - ((mouseY - oldMouseY) * mouseSensitivityY), 85.0));
        gameObject.setYRotation(gameObject.getYRotation() + ((mouseX - oldMouseX) * mouseSensitivityX));
    }

    @Override
    public boolean onScrollEvent(ScrollEvent scrollEvent) {
        if (scrollEvent.getEventType() == ScrollEvent.SCROLL) {
            camera.setTranslateZ(clamp(-maxDistFromCenter, camera.getTranslateZ() + (scrollEvent.getDeltaY() * scrollSpeed), -minDistToCenter));
        }
        return false;
    }

    @Override
    public boolean onKeyEvent(KeyEvent keyEvent) {
        if (SHOULD_USE_KEYBOARD_CONTROLS) {
            oldMouseX = mouseX;
            oldMouseY = mouseY;
            if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED || keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
                if (keyEvent.getCode() == KeyCode.W) {
                    upHeld = keyEvent.getEventType() == KeyEvent.KEY_PRESSED;
                } else if (keyEvent.getCode() == KeyCode.A) {
                    leftHeld = keyEvent.getEventType() == KeyEvent.KEY_PRESSED;
                } else if (keyEvent.getCode() == KeyCode.S) {
                    downHeld = keyEvent.getEventType() == KeyEvent.KEY_PRESSED;
                } else if (keyEvent.getCode() == KeyCode.D) {
                    rightHeld = keyEvent.getEventType() == KeyEvent.KEY_PRESSED;
                }
            }
        }
        return false;
    }

    private boolean leftHeld = false;
    private boolean rightHeld = false;
    private boolean upHeld = false;
    private boolean downHeld = false;

    @Override
    public void onRenderUpdate(double deltaTime) {
        oldMouseX = mouseX;
        oldMouseY = mouseY;

        boolean didChange = false;
        if (leftHeld) {
            didChange = true;
            mouseX = oldMouseX - (15 * deltaTime);
        }
        if (rightHeld) {
            didChange = true;
            mouseX = oldMouseX + (15 * deltaTime);
        }
        if (upHeld) {
            didChange = true;
            mouseY = oldMouseY + (15 * deltaTime);
        }
        if (downHeld) {
            didChange = true;
            mouseY = oldMouseY - (15 * deltaTime);
        }
        if (didChange) {
            orbitCamera();
        }
    }

    public Point3D getLookDirectionWS() {
        return gameObject.getRotationTransform().transform(Component.FORWARD);
    }

    public Point3D getUpDirectionWS() {
        return gameObject.getRotationTransform().transform(Component.UP);
    }

    public Point3D getRightDirectionWS() {
        return gameObject.getRotationTransform().transform(Component.RIGHT);
    }
}