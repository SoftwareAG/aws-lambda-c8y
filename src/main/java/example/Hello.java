package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Hello implements RequestHandler<DeviceInfo, String> {

    @Override
    public String handleRequest(DeviceInfo deviceInfo, Context context) {
        LambdaLogger log = context.getLogger();
        log.log("Received Event: "+deviceInfo.toString());
        log.log("Start c8y API call");
        C8y c8y = new C8y(context);
        c8y.registerDevice(deviceInfo);
        log.log("Finished c8y API call");
        return deviceInfo.deviceName;
    }
}
