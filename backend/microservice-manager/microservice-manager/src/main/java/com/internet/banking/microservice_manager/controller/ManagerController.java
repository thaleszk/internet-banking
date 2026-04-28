package com.internet.banking.manager.microservice.controller;

import com.internet.banking.manager.microservice.data.ManagerData;
import com.internet.banking.manager.microservice.facade.ManagerFacade;
import com.internet.banking.manager.microservice.model.ManagerModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/managers")
public class ManagerController {

    private final ManagerFacade managerFacade;

    public ManagerController(final ManagerFacade managerFacade) {
        this.managerFacade = managerFacade;
    }

    @PostMapping
    public ResponseEntity<ManagerData> createManager(@RequestBody final ManagerData managerData) {
        ManagerData createdManager = managerFacade.createManager(managerData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdManager);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ManagerModel> getManagerByCpf(@PathVariable final String cpf) {
        ManagerModel manager = managerFacade.getManagerByCpf(cpf);
        return ResponseEntity.ok(manager);
    }

    @GetMapping
    public ResponseEntity<List<ManagerModel>> getAllManagers() {
        List<ManagerModel> managers = managerFacade.getAllManagers();
        return ResponseEntity.ok(managers);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<ManagerModel> updateManager(@PathVariable final String cpf,
                                                       @RequestBody final ManagerModel managerModel) {
        ManagerModel updatedManager = managerFacade.updateManager(cpf, managerModel);
        return ResponseEntity.ok(updatedManager);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteManager(@PathVariable final String cpf) {
        managerFacade.deleteManager(cpf);
        return ResponseEntity.noContent().build();
    }
}
