package com.device.DeviceCommandModule.devicemanagement.api;

import com.device.DeviceCommandModule.devicemanagement.model.Device;
import org.mapstruct.Mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class DeviceViewMapper {
    public abstract DeviceView toDeviceView(Device device);

    public abstract Iterable<DeviceView> toDeviceView(Iterable<Device> devices);

    public Integer mapOptInt(final Optional<Integer> i) {
        return i.orElse(null);
    }

    public Long mapOptLong(final Optional<Long> i) {
        return i.orElse(null);
    }

    public String mapOptString(final Optional<String> i) {
        return i.orElse(null);
    }

    public UUID mapOptUUID(final Optional<UUID> i){return i.orElse(null);
    }

    public Map<String, Object> toAllDevicesMap(Iterable<Device> devices) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("devices", toDeviceView(devices));
        return response;
    }

    public Map<String, Object> mapNrDeletedDevices(int deletedDeviceNr) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("NrDevicesDeleted", deletedDeviceNr);
        return response;
    }

    public Map<String, Object> mapNrDevices(int deviceNr) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("deviceCount", deviceNr);
        return response;
    }
}
