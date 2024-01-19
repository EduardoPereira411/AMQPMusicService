package com.device.DeviceCommandModule.devicemanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditDeviceRequest {

    @Size(min = 1, max = 50)
    private String designation;
}
