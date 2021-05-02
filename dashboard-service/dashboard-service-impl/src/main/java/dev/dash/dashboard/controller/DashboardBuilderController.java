package dev.dash.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.dashboard.DashboardBuilderService;
import dev.dash.model.builder.DashboardBuilderData;
import dev.dash.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("dashboardBuilder")
public class DashboardBuilderController {

    @Autowired
    DashboardBuilderService dashboardBuilderService;

    /**
     * import method for dashboard data
     */
    @PostMapping("/importdata")
	public ResponseEntity<Integer> importData(@RequestBody DashboardBuilderData dashboardBuilderData) {
        boolean successFullImport = dashboardBuilderService.importConfig(dashboardBuilderData);
        if(successFullImport) {
            log.debug("Dashboard data imported successfully");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("Dashboard data failed to import successfully");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * export method for dashboard data
     */
    @GetMapping("/exportdata")
	public ResponseEntity<String> exportData(@RequestParam(name = "dashboards") String dashboards) {
        if ( StringUtil.isVaildString(dashboards) ) {
            String exportJson = dashboardBuilderService.exportConfig(dashboards.split(","));
            if(StringUtil.isVaildString( exportJson )) {
                return new ResponseEntity<>(exportJson,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("",HttpStatus.NO_CONTENT);
    }
}
