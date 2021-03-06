package org.enoy.klc.devices;

import com.logitech.gaming.LogiLED;
import org.enoy.klc.common.device.DeviceImpl;
import org.enoy.klc.common.device.DeviceInformation;
import org.enoy.klc.common.effects.lights.DeviceLightMatrix;
import org.enoy.klc.common.effects.lights.Light;
import org.enoy.klc.common.effects.lights.LightRow;

public class LogitechDevice extends DeviceImpl {

	private static final int DEVICE_WIDTH = 21;
	private static final int DEVICE_HEIGHT = 6;

	private DeviceInformation deviceInformation;

	public LogitechDevice() {
		deviceInformation = new DeviceInformation("Logitech Device", DEVICE_WIDTH, DEVICE_HEIGHT);
	}

	@Override
	public DeviceInformation getDeviceInformation() {
		return deviceInformation;
	}

	@Override
	public void applyDeviceLightMatrix(DeviceLightMatrix deviceLightMatrix) {

		byte[] bitmap = new byte[DEVICE_WIDTH * DEVICE_HEIGHT * 4];

		for (int i = 0; i < DEVICE_HEIGHT; i++) {
			LightRow row = deviceLightMatrix.getLightRow(i);
			for (int j = 0; j < DEVICE_WIDTH; j++) {
				int pos = i * DEVICE_WIDTH + j;
				Light light = row.getLight(j);
				bitmap[pos * 4 + 3] = light.getAlphaByte();
				bitmap[pos * 4 + 2] = light.getRedByte();
				bitmap[pos * 4 + 1] = light.getGreenByte();
				bitmap[pos * 4 + 0] = light.getBlueByte();
			}
		}

		LogiLED.LogiLedSetLightingFromBitmap(bitmap);

	}
	
	@Override
	protected void internalInit() {
		LogiLED.LogiLedInit();
	}

	@Override
	protected void internalShutdown() {
		LogiLED.LogiLedShutdown();
	}

}
