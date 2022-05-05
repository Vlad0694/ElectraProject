package com.electra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class Controller {
    @Autowired
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping("/insert")
    public void addAngajat(@RequestBody Person angajat ) throws SQLException {
        service.addAngajat(angajat);
    }

    @DeleteMapping("/delete")
    public String deleteAngajat() throws SQLException {
        return service.concediezAngajat();
    }

    @GetMapping("/getAngajati")
    public ArrayList<Person> getAngajati() throws SQLException {
        return service.getList();
    }

    @PutMapping("/updateSalariu")
    public String updateSalariu() throws SQLException {
        return service.marireSalariu();
    }

    @GetMapping("/start")
    public ArrayList<String> startWorking() throws SQLException {
        return service.startWork();
    }

    @PutMapping("/stopWorkAs")
    public String stopWorkAs(@RequestParam String nume, @RequestParam String prenume, @RequestParam String updateNumeFunctie) throws SQLException {
        return service.stopWorkingAs(nume, prenume, updateNumeFunctie);
    }
}
