package com.device.DeviceQueryModule.devicemanagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "A Device")
public class DeviceView {
    @Schema(description = "The name of the device")
    private String designation;
    private String MAC;
    private UUID SubscriptionUUID;
    private UUID devID;

}
