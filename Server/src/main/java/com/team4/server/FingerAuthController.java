package com.team4.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team4.server.App.State;

@RestController
@RequestMapping(value = "/confirm_attendance")
public class FingerAuthController {
    @GetMapping
    public String Complete(@RequestHeader(value = "deviceMAC") String deviceID, 
                           @RequestHeader(value = "isAttended",required = false, defaultValue = "false") Boolean isAttended) {
        if (App.state != State.ClusteringComplete)
            throw new NotPreparedException("Fingerprint Authentication is not allowed at this time");

        int deviceNum = App.searchDevice(deviceID);
        if (deviceNum == -1) {
            throw new NotExistingException("Not existing deviceID");
        }
        
        if(App.List.get(deviceNum).getClusteringResult() == false || isAttended == false)
            throw new  NotAllowedException("Clustring result is Failed. Fingerprint Authentication is not allowed!");

        App.List.get(deviceNum).setFinalResult();
        return "Success!!";
    }
}
