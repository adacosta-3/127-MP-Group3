package mp.group3.backend.controllers;

import mp.group3.backend.constants.CafeConstants;
import mp.group3.backend.entities.Bill;
import mp.group3.backend.services.BillService;
import mp.group3.backend.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/bill")
@RestController
public class BillController {
    @Autowired
    BillService billService;

    @PostMapping(path = "/generateReport")
    public ResponseEntity<String> generateReport(@RequestBody Map<String, Object> requestMap) {
        try {
            return billService.generateReport(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @GetMapping(path = "/getBills")
    public ResponseEntity<List<Bill>> getBills() {
        try {
            return billService.getBills();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(path = "/getPdf")
    public ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> requestMap) {
        try {
            return billService.getPdf(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteBill(@PathVariable Integer id) {
        try {
            return billService.delete(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
