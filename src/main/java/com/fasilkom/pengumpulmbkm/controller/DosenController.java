package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dosen", description = "API for processing various operations with Dosen entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/dosen")
public class DosenController {
    @Autowired
    private DosenService dosenService;
    @Autowired
    private UsersService usersService;

    @Operation(summary = "Get detail dosen")
    @GetMapping(value = "/detail-profil/{dosenId}")
    public ResponseEntity<Dosen> getProductByUserId(@PathVariable("dosenId") Integer userId) {
        dosenService.getDosenByUserId(userId);
        return ResponseEntity.accepted().body(dosenService.getDosenByUserId(userId));
    }

    @Operation(summary = "add dosen")
    @PostMapping(value = "/add-dosen")
    public ResponseEntity<MessageResponse> addDosen(
            @RequestParam("user_id") Integer userId){
        Dosen dosen = new Dosen();
        Users users = usersService.findByUserId(userId);
        dosen.setUserId(users);
        dosenService.saveDosen(dosen);
        return ResponseEntity.ok(new MessageResponse("Successfully Added Lecturer"));
    }

}
