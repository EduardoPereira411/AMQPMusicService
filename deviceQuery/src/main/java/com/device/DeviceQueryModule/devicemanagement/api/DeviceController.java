package com.device.DeviceQueryModule.devicemanagement.api;

import com.device.DeviceQueryModule.configuration.KeyDecoder;
import com.device.DeviceQueryModule.devicemanagement.model.Role;
import com.device.DeviceQueryModule.devicemanagement.services.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Devices", description = "Endpoints for managing devices")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService service;
    private final DeviceViewMapper deviceViewMapper;
    private final KeyDecoder decoder;

    @Operation(summary = "Gets all devices from loggedIn subscription")
    @GetMapping("/all")
    @RolesAllowed(Role.SUBSCRIBER)
    public Map<String, Object> findAllFromLoggedInUser(HttpServletRequest request){
        UUID userID = decoder.getIdbyToken(request);

        return deviceViewMapper.toAllDevicesMap(service.findAllFromSub(userID));
    }

    @Operation(summary = "Gets a specific device from logged in sub")
    @GetMapping(value = "/{devID}")
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<DeviceView> findById(HttpServletRequest request,
                                               @PathVariable("devID")
                                               @Parameter(description = "The id of the device to find") final UUID devID){
        UUID userID = decoder.getIdbyToken(request);

        final var device = service.findByIdOnSub(devID, userID);

        return ResponseEntity.ok().eTag(Long.toString(device.getVersion())).body(deviceViewMapper.toDeviceView(device));
    }

    @Operation(summary = "Gets a specific device threw its MAC")
    @GetMapping(value = "/MAC/{macAddr}")
    public ResponseEntity<DeviceView> findByMAC(@PathVariable("macAddr")
                                                @Parameter(description = "The id of the device to find") final String macAddr){

        final var device = service.findByMac(macAddr);

        return ResponseEntity.ok().eTag(Long.toString(device.getVersion())).body(deviceViewMapper.toDeviceView(device));
    }

    @Operation(summary = "Counts number of devices from a logged in sub")
    @GetMapping(value = "/count")
    @RolesAllowed(Role.SUBSCRIBER)
    public Map<String, Object> countDeviceNr(HttpServletRequest request){

        UUID userID = decoder.getIdbyToken(request);

        final var deviceNr = service.countDevicesFromSub(userID);

        return deviceViewMapper.mapNrDevices(deviceNr);
    }
}

