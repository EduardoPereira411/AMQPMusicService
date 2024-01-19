package com.device.DeviceCommandModule.devicemanagement.services;


import com.device.DeviceCommandModule.devicemanagement.api.CreateDeviceRequest;
import com.device.DeviceCommandModule.devicemanagement.api.EditDeviceRequest;
import com.device.DeviceCommandModule.devicemanagement.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import javax.validation.Valid;

@Mapper(componentModel = "spring")
public abstract class EditDeviceMapper {
    public abstract Device create(@Valid CreateDeviceRequest request);

    public abstract void update(EditDeviceRequest request, @MappingTarget Device device);
}
