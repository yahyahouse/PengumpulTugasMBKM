package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.DosenResponse;
import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Admin", description = "API for processing various operations with Dosen entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private DosenService dosenService;
    @Autowired
    private UsersService usersService;

    @Operation(summary = "Get detail dosen")
    @GetMapping(value = "/detail-profil-dosen/{dosenId}")
    public ResponseEntity<DosenResponse> getDetailDosenByDosenId(@PathVariable("dosenId") Integer dosenId) {
        Dosen dosen =dosenService.getDosenByUserId(dosenId);
        return new ResponseEntity<>(new DosenResponse(dosen), HttpStatus.OK);
    }

    @Operation(summary = "add dosen")
    @PostMapping(value = "/add-dosen")
    public ResponseEntity<MessageResponse> addDosen(
            @RequestParam("userId") Integer userId){
        Dosen dosen = new Dosen();
        Users users = usersService.findByUserId(userId);
        dosen.setUserId(users);
        dosenService.saveDosen(dosen);
        return ResponseEntity.ok(new MessageResponse("Successfully Added Lecturer"));
    }

    @Operation(summary = "delete dosen")
    @DeleteMapping(value = "/delete-dosen")
    public ResponseEntity<MessageResponse> deleteDosen(
            @RequestParam("dosenId") Integer dosenId){
        try {
            dosenService.deletDosenByDosenId(dosenId);
            return ResponseEntity.ok(new MessageResponse("Successfully delete Lecturer"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Cannot delete lecturer with id"+dosenId));
        }

    }

    @Operation(summary = "Get all dosen")
    @GetMapping(value = "/all-dosen")
    public ResponseEntity<List<DosenResponse>> getAllDosen() {
        List<Dosen> dosen =dosenService.getAllDosen();
        List<DosenResponse> allDosen =
                dosen.stream().map(DosenResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allDosen, HttpStatus.OK);
    }

}
