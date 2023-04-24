package main.java.com.companyz.accountmanagementsystem.controller;


import com.companyz.accountmanagementsystem.dto.authdto.AuthRequest;
import com.companyz.accountmanagementsystem.dto.authdto.AuthResponse;
import com.companyz.accountmanagementsystem.dto.userdto.UserDtoGet;
import com.companyz.accountmanagementsystem.dto.userdto.UserDtoPost;
import com.companyz.accountmanagementsystem.service.UserService;
import com.companyz.accountmanagementsystem.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;


    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping
    public ResponseEntity<List<UserDtoGet>> getAll() {
        return new ResponseEntity<List<UserDtoGet>>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity getAllPaginated(Pageable pageable) {
        return  ResponseEntity.ok(userService.getAllPaginated(pageable));
    }


    @PostMapping
    public ResponseEntity<UserDtoGet> add(@RequestBody UserDtoPost userDtoPost){
        return new ResponseEntity<UserDtoGet>(userService.add(userDtoPost), HttpStatus.CREATED) ;
    }
    @GetMapping(path = "{id}")
    public ResponseEntity<UserDtoGet> get(
            @PathVariable("id") Long id) {
        return   new ResponseEntity<UserDtoGet>(userService.get(id),HttpStatus.OK);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<UserDtoGet> update(
            @PathVariable("id") Long id,@RequestBody UserDtoPost userDtoPost) {
        return   new ResponseEntity<UserDtoGet>(userService.update(userDtoPost,id),HttpStatus.OK);
    }


    @PutMapping("verify/{userId}")
    public ResponseEntity<UserDtoGet> setVerification(@PathVariable("userId") Long id) {
        return new ResponseEntity<>(userService.setVerification(id), HttpStatus.OK);
    }

}
