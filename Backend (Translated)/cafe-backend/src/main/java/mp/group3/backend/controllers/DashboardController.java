package mp.group3.backend.controllers;


import mp.group3.backend.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/dashboard")

public class DashboardController {

    @Autowired
    DashboardService dashboardService;
    @GetMapping(path = "/details")
    public ResponseEntity<Map<String, Object>> getCount() {
        return dashboardService.getCount();
    }
}

