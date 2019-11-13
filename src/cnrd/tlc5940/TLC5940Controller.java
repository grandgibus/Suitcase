package cnrd.tlc5940;

import cnrd.CNRD;
import cnrd.InputOutputsControl;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class TLC5940Controller {

    public static void init(){

        if(CNRD.RASPBERRYPI){

            Thread tlc5940Thread = new Thread(TLC5940Controller::updateTLC5940);
            tlc5940Thread.start();

        }

    }

    private static void updateTLC5940(){

        GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput sinPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        GpioPinDigitalOutput sclkPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14);
        GpioPinDigitalOutput blankPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        GpioPinDigitalOutput dcprgPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        GpioPinDigitalOutput vprgPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);
        GpioPinDigitalOutput xlatPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10);
        GpioPinDigitalOutput gsclkPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11);

        TLC5940 tlc5940 = new TLC5940(InputOutputsControl.TLC_COUNT, sinPin, sclkPin, blankPin, dcprgPin, vprgPin, xlatPin, gsclkPin);

        while(true){
            tlc5940.update();
        }
    }
}
