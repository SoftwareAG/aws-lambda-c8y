package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.fasterxml.jackson.databind.ObjectMapper;

public class C8y {

    String tenant ="<tenantID>";
    String username="<Cumulocity User ID>";
    String password= "<Password>";
    String url="https://<tenantId>.eu-latest.cumulocity.com";

    LambdaLogger log;

    ObjectMapper mapper = new ObjectMapper();

    public C8y(Context context){
        this.log = context.getLogger();
    }

    public void getDevices(){
        
        log.log(tenant+": "+username+": "+password);
        try{
		    Platform platform = openC8yConn();
            if(platform!=null){
                InventoryApi inventoryApi = platform.getInventoryApi();
                Iterable<ManagedObjectRepresentation> result = inventoryApi.getManagedObjects().get().elements(2);
                log.log("connection: Got Inventory"+result);
                for (ManagedObjectRepresentation mo: result){
                    log.log("mo: "+mo.getName());
                }
                closeC8yConn(platform);
                log.log("connection: closed platform");
            }else
                log.log("Unable to open the c8y connection"); 
            
        }catch(Exception e){
            log.log("error Device : "+e.getMessage());
        }            
    }

    public void registerDevice(DeviceInfo deviceInfo){
        log.log("check if device exist: ");
        Boolean flag = false;
        Platform platform = openC8yConn();
        
        if(platform!=null){
            IdentityApi identityApi = platform.getIdentityApi();
            try{
                ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
                ID id = new ID(deviceInfo.identityType, deviceInfo.serialNumber);
                externalIDRepresentation = identityApi.getExternalId(id);
                log.log("Device already exist: "+externalIDRepresentation.getManagedObject());
            }catch(Exception e){
                log.log("device does not exist, kindly register");
                flag = true;                
            }
            if(Boolean.TRUE.equals(flag)){
                ManagedObjectRepresentation result = createDevice(platform, deviceInfo);
                ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
                externalIDRepresentation.setExternalId(deviceInfo.serialNumber);
                externalIDRepresentation.setType(deviceInfo.identityType);
                externalIDRepresentation.setManagedObject(result);
                ExternalIDRepresentation eresult = identityApi.create(externalIDRepresentation);
                log.log("Attached the serial Number with managedObject: "+eresult);
            }
            closeC8yConn(platform);
        }else
            log.log("Unable to open the c8y connection"); 


    }

    private Platform openC8yConn(){
        try{
            CumulocityCredentials credentials = CumulocityBasicCredentials.builder()
                        .tenantId(tenant)
                        .username(username)
                        .password(password)
                        .applicationKey(null)
                        .build();
            return new PlatformImpl(url,  credentials);
        }catch(Exception e){
            return null;
        }
    }

    private void closeC8yConn(Platform platform){
        platform.close();
    }

    private ManagedObjectRepresentation createDevice(Platform platform, DeviceInfo deviceInfo){
        InventoryApi inventoryApi = platform.getInventoryApi();
        ManagedObjectRepresentation mor = new ManagedObjectRepresentation();
        mor.setName(deviceInfo.deviceName);
        mor.setType(deviceInfo.deviceType);
        mor.set(mapper.createObjectNode(),"c8y_IsDevice");
        ManagedObjectRepresentation result = inventoryApi.create(mor);
        log.log("new device has created: "+result);
        return result;
    }
    
}
