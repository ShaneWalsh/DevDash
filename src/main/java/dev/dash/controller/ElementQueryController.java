package dev.dash.controller;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.model.body.QueryExecution;
import dev.dash.service.QueryExecutorService;

@RestController
@RequestMapping("query")
public class ElementQueryController {

    @Autowired
    private QueryExecutorService queryExecutorService;

    @PostMapping("/execute")
    public ResponseEntity<List<Object>> execute(@RequestBody final QueryExecution queryExecution) throws SQLException {
        System.out.println(queryExecution.toString()); // todo debug
        final JSONArray arr = queryExecutorService.processQuery(queryExecution);
        final List<Object> list = arr.toList();
        System.out.println(arr.toList()); // todo debug
        return new ResponseEntity<List<Object>>(list,HttpStatus.OK);
    }
}
