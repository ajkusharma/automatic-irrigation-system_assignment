package com.example.automaticirrigationsystem.controller;

import com.example.automaticirrigationsystem.model.Plot;
import com.example.automaticirrigationsystem.repo.IrrigationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class IrrigationSystemController {

    @Autowired
    private IrrigationRepository irrigationRepository;

    private Map<LocalTime, Boolean> timeSlots = new HashMap();


    @GetMapping("/getAllPlots")
    public ResponseEntity<List<Plot>> getAllPlots(){

        try{
            List<Plot> plotList= new ArrayList<>();
            irrigationRepository.findAll().forEach(plotList::add);
            if(plotList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(plotList,HttpStatus.OK);
        }catch(Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getPlotById/{id}")
    public ResponseEntity<Plot> findPlotById(@PathVariable(value="id") long id){
        Optional<Plot> responsePlotObj= irrigationRepository.findById(id);
        if(responsePlotObj.isPresent()){
            return new ResponseEntity<>(responsePlotObj.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addPlot")
    public void  addPlot(@Validated @RequestBody Plot plot){

        initializeSlots();
        allocateSlots(plot.getStartTime(),plot.getEndTime());
        Plot plotObj= irrigationRepository.save(plot);
        new ResponseEntity<>(plotObj,HttpStatus.OK);
    }

    @PutMapping("/updatePlotById/{id}")
    public void  updatePlotById(@PathVariable Long id, @RequestBody Plot newPlot){
        Optional<Plot> oldPlot= irrigationRepository.findById(id);
        if(oldPlot.isPresent()){
            Plot updatedPlotData= oldPlot.get();
            updatedPlotData.setAmount_water(newPlot.getAmount_water());
            updatedPlotData.setStartTime(newPlot.getStartTime());
            updatedPlotData.setEndTime(newPlot.getEndTime());
           Plot plotObj= irrigationRepository.save(updatedPlotData);
           new ResponseEntity<>(plotObj,HttpStatus.OK);
        }
        new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deletePlotById/{id}")
    public ResponseEntity<HttpStatus> deletePlotById(@PathVariable Long id){
        irrigationRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void initializeSlots() {

        LocalTime time = LocalTime.of(9, 0);
        timeSlots.put(time, true);
        for (int i = 1; i < 24; i++) {
            timeSlots.put(time.plusHours(i), true);
        }
    }

    private void allocateSlots(String strTime, String edTime) {
        LocalTime startTime = LocalTime.parse(strTime);
        LocalTime endTime = LocalTime.parse(edTime);

        while (startTime.isBefore(endTime)) {
            //check if the time slots between start and end time are available
            if (!timeSlots.get(startTime) || !timeSlots.get(endTime)) {
                System.out.println("slots not available" + " start time: " + strTime + " end time: " + edTime);
                return;
            }
            startTime = startTime.plusHours(1);
            endTime = endTime.minusHours(1);
        }

        System.out.println("slots are available" + " start time: " + strTime + " end time: " + edTime);
        //then here u can mark all slots between to unavailable.
        startTime = LocalTime.parse(strTime);
        endTime = LocalTime.parse(edTime);
        while (startTime.isBefore(endTime)) {
            timeSlots.put(startTime, false);
            timeSlots.put(endTime, false);
            startTime = startTime.plusHours(1);
            endTime = endTime.minusHours(1);
        }
    }

}



