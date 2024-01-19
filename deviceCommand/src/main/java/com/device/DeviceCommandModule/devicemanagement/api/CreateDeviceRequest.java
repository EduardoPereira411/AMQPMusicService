package com.device.DeviceCommandModule.devicemanagement.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateDeviceRequest {
    @NonNull
    @NotBlank
    @Size(min = 17, max = 17)
    private String MAC;

    @NonNull
    @NotBlank
    private String designation;


}
