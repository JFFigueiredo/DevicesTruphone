package com.truphone.devices.service.impl;


import com.truphone.devices.exception.ResourceNotFoundException;
import com.truphone.devices.model.Device;
import com.truphone.devices.repository.DeviceRepository;
import com.truphone.devices.service.DeviceService;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    //This validator is used for the spring validations of the min sizes.
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();


    public void deleteDeviceById(long id) {
        Device existingDevice = deviceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Device", "Id", id));
        this.deviceRepository.deleteById(id);
    }

    @Override
    public Device getDeviceById(long id) {
        return deviceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Device", "Id", id));
    }

    @Override
    public Device getDeviceByBrand(String brand) {
        Device returnDevice = deviceRepository.findDeviceByBrand(brand);
        if (returnDevice != null) {
            return returnDevice;
        }
        throw new ResourceNotFoundException("Device", "Brand", brand);
    }

    @Override
    public Device updateDevice(Device device, long id) {
        Device existingDevice = deviceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Device", "Id", id));

        existingDevice.setName(device.getName());
        existingDevice.setBrand(device.getBrand());
        if (device.getCreationDate() != null) {
            existingDevice.setCreationDate(device.getCreationDate());
        }
        deviceRepository.save(existingDevice);
        return existingDevice;
    }

    @Override
    public Device updatePartialDevice(HashMap<String, String> jsonRequest, long id) {
        Device existingDevice = deviceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Device", "Id", id));

        if (jsonRequest.get("name") != null) {
            existingDevice.setName(jsonRequest.get("name"));
        }
        if (jsonRequest.get("brand") != null) {
            existingDevice.setBrand(jsonRequest.get("brand"));
        }
        if (jsonRequest.get("creationDate") != null) {
            existingDevice.setCreationDate(new Date(jsonRequest.get("creationDate")));
        }
        deviceRepository.save(existingDevice);
        return existingDevice;
    }

    @Override
    public Device saveDevice(Device device) {
        Set<ConstraintViolation<Device>> violations = validator.validate(device);
        if (device.getCreationDate() == null) {
            device.setCreationDate(new Date());
        }
        if (violations.size() > 0) {
            String violationMessges = "";
            for (ConstraintViolation<Device> violation : violations) {
                violationMessges = violationMessges + " " + violation.getMessage();
                System.out.println(violation.getMessage());
            }
            throw new RuntimeException("Error creating devices " + violationMessges);
        } else
            return this.deviceRepository.save(device);
    }

    @Override
    public List<Device> getAllDevices() {
        return this.deviceRepository.findAll();
    }


}
