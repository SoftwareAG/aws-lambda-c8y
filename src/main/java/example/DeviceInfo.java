package example;

public class DeviceInfo {
    String deviceName;
    String serialNumber;
    String deviceType;
    String identityType;

    public String getIdentityType() {
        return this.identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString(){
        return "deviceName: "+deviceName+"\nserialNumber: "+serialNumber+"\ndeviceType: "+deviceType+"\nidentityType: "+identityType;
    }
}
