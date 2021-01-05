//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.mollin.yapi.command.YeelightCommand;
import com.mollin.yapi.enumeration.YeelightAdjustAction;
import com.mollin.yapi.enumeration.YeelightAdjustProperty;
import com.mollin.yapi.enumeration.YeelightEffect;
import com.mollin.yapi.enumeration.YeelightProperty;
import com.mollin.yapi.exception.YeelightResultErrorException;
import com.mollin.yapi.exception.YeelightSocketException;
import com.mollin.yapi.flow.YeelightFlow;
import com.mollin.yapi.result.YeelightResultError;
import com.mollin.yapi.result.YeelightResultOk;
import com.mollin.yapi.utils.YeelightUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class YeelightDevice {
    private final YeelightSocketHolder socketHolder;
    private YeelightEffect effect;
    private int duration;

    public YeelightDevice(String ip, int port, YeelightEffect effect, int duration) throws YeelightSocketException {
        this.socketHolder = new YeelightSocketHolder(ip, port);
        this.setEffect(effect);
        this.setDuration(duration);
    }

    public YeelightDevice(String ip, int port) throws YeelightSocketException {
        this(ip, port, YeelightEffect.SUDDEN, 0);
    }

    public YeelightDevice(String ip) throws YeelightSocketException {
        this(ip, 55443);
    }

    public void setEffect(YeelightEffect effect) {
        this.effect = effect == null ? YeelightEffect.SUDDEN : effect;
    }

    public void setDuration(int duration) {
        this.duration = Math.max(0, duration);
    }

    private String[] readUntilResult(int id) throws YeelightSocketException, YeelightResultErrorException {
        Optional errorResult;
        do {
            String datas = this.socketHolder.readLine();
            Optional<YeelightResultOk> okResult = YeelightResultOk.from(datas);
            errorResult = YeelightResultError.from(datas);
            if (okResult.isPresent() && ((YeelightResultOk)okResult.get()).getId() == id) {
                return ((YeelightResultOk)okResult.get()).getResult();
            }
        } while(!errorResult.isPresent() || ((YeelightResultError)errorResult.get()).getId() != id);

        throw ((YeelightResultError)errorResult.get()).getException();
    }

    private String[] sendCommand(YeelightCommand command) throws YeelightSocketException, YeelightResultErrorException {
        String jsonCommand = command.toJson() + "\r\n";
        this.socketHolder.send(jsonCommand);

        return new String[0];
//        return this.readUntilResult(command.getId());
    }

    public Map<YeelightProperty, String> getProperties(YeelightProperty... properties) throws YeelightResultErrorException, YeelightSocketException {
        YeelightProperty[] expectedProperties = properties.length == 0 ? YeelightProperty.values() : properties;
        Object[] expectedPropertiesValues = Stream.of(expectedProperties).map(YeelightProperty::getValue).toArray();
        YeelightCommand command = new YeelightCommand("get_prop", expectedPropertiesValues);
        String[] result = this.sendCommand(command);
        Map<YeelightProperty, String> propertyToValueMap = new HashMap();

        for(int i = 0; i < expectedProperties.length; ++i) {
            propertyToValueMap.put(expectedProperties[i], result[i]);
        }

        return propertyToValueMap;
    }

    public void setColorTemperature(int colorTemp) throws YeelightResultErrorException, YeelightSocketException {
        colorTemp = YeelightUtils.clamp(colorTemp, 1700, 6500);
        YeelightCommand command = new YeelightCommand("set_ct_abx", new Object[]{colorTemp, this.effect.getValue(), this.duration});
        this.sendCommand(command);
    }

    public void setRGB(int r, int g, int b) throws YeelightResultErrorException, YeelightSocketException {
        int rgbValue = YeelightUtils.clampAndComputeRGBValue(r, g, b);
        YeelightCommand command = new YeelightCommand("set_rgb", new Object[]{rgbValue, this.effect.getValue(), this.duration});
        this.sendCommand(command);
    }

    public void setHSV(int hue, int sat) throws YeelightResultErrorException, YeelightSocketException {
        hue = YeelightUtils.clamp(hue, 0, 359);
        sat = YeelightUtils.clamp(sat, 0, 100);
        YeelightCommand command = new YeelightCommand("set_hsv", new Object[]{hue, sat, this.effect.getValue(), this.duration});
        this.sendCommand(command);
    }

    public void setBrightness(int brightness) throws YeelightResultErrorException, YeelightSocketException {
        brightness = YeelightUtils.clamp(brightness, 1, 100);
        YeelightCommand command = new YeelightCommand("set_bright", new Object[]{brightness, this.effect.getValue(), this.duration});
        this.sendCommand(command);
    }

    public void setPower(boolean power) throws YeelightResultErrorException, YeelightSocketException {
        String powerStr = power ? "on" : "off";
        YeelightCommand command = new YeelightCommand("set_power", new Object[]{powerStr, this.effect.getValue(), this.duration});
        this.sendCommand(command);
    }

    public void toggle() throws YeelightResultErrorException, YeelightSocketException {
        YeelightCommand command = new YeelightCommand("toggle", new Object[0]);
        this.sendCommand(command);
    }

    public void setDefault() throws YeelightResultErrorException, YeelightSocketException {
        YeelightCommand command = new YeelightCommand("set_default", new Object[0]);
        this.sendCommand(command);
    }

    public void startFlow(YeelightFlow flow) throws YeelightResultErrorException, YeelightSocketException {
        YeelightCommand command = new YeelightCommand("start_cf", flow.createCommandParams());
        this.sendCommand(command);
    }

    public void stopFlow() throws YeelightResultErrorException, YeelightSocketException {
        YeelightCommand command = new YeelightCommand("stop_cf", new Object[0]);
        this.sendCommand(command);
    }

    public void addCron(int delay) throws YeelightResultErrorException, YeelightSocketException {
        delay = Math.max(0, delay);
        YeelightCommand command = new YeelightCommand("cron_add", new Object[]{0, delay});
        this.sendCommand(command);
    }

    public int getCronDelay() throws YeelightResultErrorException, YeelightSocketException {
        Map propertyToString = this.getProperties(YeelightProperty.DELAY_OFF);

        try {
            return Integer.parseInt((String)propertyToString.get(YeelightProperty.DELAY_OFF));
        } catch (Exception var3) {
            return 0;
        }
    }

    public void deleteCron() throws YeelightResultErrorException, YeelightSocketException {
        YeelightCommand command = new YeelightCommand("cron_del", new Object[]{0});
        this.sendCommand(command);
    }

    public void setAdjust(YeelightAdjustProperty property, YeelightAdjustAction action) throws YeelightResultErrorException, YeelightSocketException {
        String actionValue = action == null ? YeelightAdjustAction.CIRCLE.getValue() : action.getValue();
        String propertyValue = property == null ? YeelightAdjustProperty.COLOR.getValue() : property.getValue();
        YeelightCommand command = new YeelightCommand("set_adjust", new Object[]{actionValue, propertyValue});
        this.sendCommand(command);
    }

    public void setName(String name) throws YeelightResultErrorException, YeelightSocketException {
        if (name == null) {
            name = "";
        }

        YeelightCommand command = new YeelightCommand("set_name", new Object[]{name});
        this.sendCommand(command);
    }
}
