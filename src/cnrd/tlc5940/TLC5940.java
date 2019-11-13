package cnrd.tlc5940;

import cnrd.CNRD;
import cnrd.InputOutputsControl;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class TLC5940 {

    public static final int LED_COUNT = 16;

    private int number;

    GpioPinDigitalOutput sinPin;
    GpioPinDigitalOutput sclkPin;
    GpioPinDigitalOutput blankPin;
    GpioPinDigitalOutput dcprgPin;
    GpioPinDigitalOutput vprgPin;
    GpioPinDigitalOutput xlatPin;
    GpioPinDigitalOutput gsclkPin;

    private boolean firstCycle = false;

    public TLC5940(int number, GpioPinDigitalOutput sinPin, GpioPinDigitalOutput sclkPin, GpioPinDigitalOutput blankPin, GpioPinDigitalOutput dcprgPin, GpioPinDigitalOutput vprgPin, GpioPinDigitalOutput xlatPin, GpioPinDigitalOutput gsclkPin){

        this.sinPin = sinPin;
        this.sclkPin = sclkPin;
        this.blankPin = blankPin;
        this.dcprgPin = dcprgPin;
        this.vprgPin = vprgPin;
        this.xlatPin = xlatPin;
        this.gsclkPin = gsclkPin;

        this.number = number;

        this.init();
    }

    private void init(){
        sinPin.low();
        sclkPin.low();
        blankPin.low();
        dcprgPin.low();
        vprgPin.low();
        xlatPin.low();
        gsclkPin.low();
    }

    public void update(){

        updateInit();

        int channelCounter = (number * LED_COUNT) - 1;
        int gsclkCounter = 0;
//        boolean pulseGsclk = true;

        while(gsclkCounter < 4096){
            if(channelCounter >= 0){

                boolean lit = getPinValueForChannel(channelCounter);

                for(int i = 11; i >= 0; i--){

                    sinPin.setState(lit);
                    pulse(sclkPin);

                    pulse(gsclkPin);
                    gsclkCounter++;
                }

                channelCounter--;
            }else{
                sinPin.low();

                pulse(gsclkPin);
                gsclkCounter++;
            }
        }

        updatePost();
    }

    private void updateInit(){
//        if(this.firstCycle){
//            vprgPin.low();
//        }
        blankPin.low();
    }

    private void updatePost(){
        blankPin.high();
        pulse(xlatPin);
    }

    private void pulse(GpioPinDigitalOutput pin){
        //Min time is 20ns, small enough to be ignored
        pin.high();
        pin.low();
    }

    private boolean getPinValueForChannel(int channel)
    {
//        return ((InputOutputsControl.led_values[channel + InputOutputsControl.transistor * LED_COUNT] >> bit) & 1) > 0;
        return InputOutputsControl.led_states[channel];
    }

}
