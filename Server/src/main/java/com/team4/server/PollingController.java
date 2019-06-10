package com.team4.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.team4.server.App.State;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/polling")
public class PollingController {
    @GetMapping
    public @ResponseBody Map<String, Object> getJsonByMap(@RequestHeader(value = "deviceMAC") String deviceID) {
        if (App.state != State.GetPlan && App.state != State.ClusteringComplete)
            throw new NotPreparedException("Get Plan or Clustering result is not allowed in this time");

        int deviceNum = App.searchDevice(deviceID);
        if (deviceNum == -1) {
            throw new NotExistingException("Not existing deviceID");
        }

        if (App.state == State.GetPlan && App.List.get(deviceNum).getUploadResult() == false ) {
            System.out.println("GetPan Called :: " + deviceID);
            Map<String, Object> jsonObject = new LinkedHashMap<String, Object>();
            Map<String, Object> jsonSubObject = null;
            ArrayList<Map<String, Object>> planList = new ArrayList<Map<String, Object>>();

            jsonObject.put("type", "plan");
            jsonObject.put("deviceID", deviceID);
            jsonObject.put("duration", 10);
            jsonObject.put("deviceOrder", deviceNum);

            for (int i = 0; i < App.List.size(); i++) {
                jsonSubObject = new LinkedHashMap<String, Object>();
                jsonSubObject.put("round", i);
                jsonSubObject.put("role", getRole(deviceID, i));
                planList.add(jsonSubObject);
            }
            jsonObject.put("plan", planList);
            System.out.println("Plan:: " + jsonObject);

            App.List.get(deviceNum).setGetPlan();

            return jsonObject;
        } else if (App.state == State.GetPlan && App.List.get(deviceNum).getUploadResult() == true){
            throw new NotPreparedException("Get Plan or Clustering result is not allowed in this time");
        } else {
            if (App.List.get(deviceNum).getClusteringResult() == true) {
                Map<String, Object> jsonObject = new LinkedHashMap<String, Object>();
                jsonObject.put("type", "authentication");
                return jsonObject;
            }else{
                throw new NotAllowedException("Clustering Result Failed. Your Attendance is not valid!!");
            }
        }
    }

    private String getRole(String deviceID, int i) {
        if (App.searchDevice(deviceID) == i)
            return "transmit";
        else
            return "scan";
    }
}
