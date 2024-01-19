package com.device.DeviceCommandModule.devicemanagement.api;

import com.device.DeviceCommandModule.AMQP.AMQPSender;
import com.device.DeviceCommandModule.configuration.KeyDecoder;
import com.device.DeviceCommandModule.devicemanagement.model.Role;
import com.device.DeviceCommandModule.devicemanagement.services.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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


    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader == null || ifMatchHeader.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

    @Operation(summary = "Creates a new Device")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<DeviceView> create(HttpServletRequest request, @Valid @RequestBody final CreateDeviceRequest resource){
        UUID userID = decoder.getIdbyToken(request);

        final var device = service.create(resource, userID);
        final var newdevUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(device.getDevID().toString())
                .build().toUri();

        return ResponseEntity.created(newdevUri).eTag(Long.toString(device.getVersion()))
                .body(deviceViewMapper.toDeviceView(device));
    }


    @Operation(summary = "Delete a device from logged in sub")
    @DeleteMapping(value = "/{devID}")
    @RolesAllowed(Role.SUBSCRIBER)
    public Map<String, Object> deleteFromLoggedInSub(HttpServletRequest request,
                                                     @PathVariable("devID") @Parameter(description = "The id of the device to delete") final UUID devID){
        UUID userID = decoder.getIdbyToken(request);

        final int count = service.deleteByIdOnSub(devID, userID);
        return deviceViewMapper.mapNrDeletedDevices(count);
    }

    @Operation(summary = "Update a device")
    @PatchMapping(value = "/{devID}")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<DeviceView> update(final HttpServletRequest request, @Valid @RequestBody final EditDeviceRequest resource,
                                             @PathVariable("devID") @Parameter(description = "The id of the device to update") final UUID devID){
        UUID userID = decoder.getIdbyToken(request);
        long desiredVersion = getVersionFromIfMatchHeader(request.getHeader("If-Match"));

        final var updatedDev = service.update(desiredVersion,userID,devID, resource);
        return ResponseEntity.ok().eTag(Long.toString(updatedDev.getVersion())).body(deviceViewMapper.toDeviceView(updatedDev));
    }


    @Operation(summary = "Deletes all the devices from a logged in sub")
    @DeleteMapping(value = "/delete")
    @RolesAllowed(Role.SUBSCRIBER)
    public Map<String, Object> deleteAllDevicesFromLoggedInSub(HttpServletRequest request){
        UUID userID = decoder.getIdbyToken(request);

        final var deviceDeletedNr = service.deleteAllFromSub(userID);

        return deviceViewMapper.mapNrDeletedDevices(deviceDeletedNr);
    }

}

