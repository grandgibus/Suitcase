package cnrd;

import cnrd.tlc5940.TLC5940;
import com.pi4j.io.gpio.*;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class InputOutputsControl {

    public static final int TLC_COUNT = 3;

    public static GpioPinDigitalInput buttonPin;

    public static GpioPinDigitalInput volumeUpPin;
    public static boolean volumeUpPressed;

    public static GpioPinDigitalInput volumeDownPin;
    public static boolean volumeDownPressed;

    public static GpioPinDigitalOutput buttonLedsPin;

    public static final int LED_COUNT = TLC_COUNT * TLC5940.LED_COUNT;

    public static boolean[] led_states = new boolean[LED_COUNT];

    //Button
    private static int buttonTicks = 0;

    private static final int TAP_TICKS = 50 / CNRD.TICK_TIME;
    private static final int PRESS_TICKS = 500 / CNRD.TICK_TIME;

    public static GpioPinDigitalOutput testPin;

    public static void init(){

        setAllOff();

        if(CNRD.RASPBERRYPI){

            GpioController gpio = GpioFactory.getInstance();

            testPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07);

            buttonPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
            buttonLedsPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23);

            volumeUpPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_DOWN);
            volumeDownPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_DOWN);
        }

    }


    public static void setAllOff(){
        for(int i = 0; i < LED_COUNT; i++){
            led_states[i] = false;
        }
    }

    public static void setAllOn(){
        for(int i = 0; i < LED_COUNT; i++){
            led_states[i] = true;
        }
    }

    private static int actual = led_states.length - 1;

    public static void test(){
        led_states[actual] = false;
        actual++;
        if(actual >= led_states.length){
            actual = 0;
        }
        led_states[actual] = true;
    }

    private static int pressedButtonTicks = 0;
    private static boolean pressLedState = false;

    public static void setLED(int id, boolean state){
        led_states[id] = state;
    }

    public static void update(){

        if(isButtonPressed()){
            pressedButtonTicks++;
            buttonTicks++;

            if(buttonTicks > PRESS_TICKS){
                buttonPressed();
                buttonTicks = 0;
            }

            if(CNRD.RASPBERRYPI)
            buttonLedsPin.high();

            if(pressedButtonTicks > PRESS_TICKS && CNRD.RASPBERRYPI){
                buttonLedsPin.setState(pressLedState);

                if(pressedButtonTicks % (500 / CNRD.TICK_TIME) == 0){
                    pressLedState = !pressLedState;
                }
            }
        }else{

            pressedButtonTicks = 0;

            if (buttonTicks > TAP_TICKS) {
                buttonTapped();
                buttonTicks = 0;
            }

            if(CNRD.RASPBERRYPI)
                buttonLedsPin.low();
        }


        if(CNRD.RASPBERRYPI){

            if(!volumeUpPressed && volumeUpPin.isHigh()){
                volumeUpPressed = true;
                CNRD.changeVolume(true);
                System.out.println("Button up");
            }else if(volumeUpPressed && volumeUpPin.isLow()){
                volumeUpPressed = false;
            }

            if(!volumeDownPressed && volumeDownPin.isHigh()){
                volumeDownPressed = true;
                CNRD.changeVolume(false);
                System.out.println("Button down");
            }else if(volumeDownPressed && volumeDownPin.isLow()){
                volumeDownPressed = false;
            }

        }

    }

    public static void buttonPressed(){
        CNRD.application.buttonPressed();
    }

    public static void buttonTapped(){
        CNRD.application.buttonTaped();
    }

    public static boolean isButtonPressed(){
        return !CNRD.RASPBERRYPI ? IsKeyPressed.isEnterKeyPressed() : buttonPin.isHigh();
    }

    public static class IsKeyPressed {

        private static volatile boolean wPressed = false;
        public static boolean isEnterKeyPressed() {
            synchronized (IsKeyPressed.class) {
                return wPressed;
            }
        }

        public static void init() {

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
                synchronized (IsKeyPressed.class) {
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                                wPressed = true;
                            }
                            break;

                        case KeyEvent.KEY_RELEASED:
                            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                                wPressed = false;
                            }
                            break;
                    }
                    return false;
                }
            });
        }
    }
}
