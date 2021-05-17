package dev.dash.security.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.dash.model.dto.admin.SecurityUserDTO;
import dev.dash.security.AdminLogicService;

@Transactional
@RestController
@RequestMapping("admin")
public class AdminController {
    
    @Autowired
    AdminLogicService adminLogicService;
  
    //TODO get list of users, filter by username search like?
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public ResponseEntity<List<SecurityUserDTO>> getSecurityUserList() throws Exception {
        List<SecurityUserDTO> findAll = adminLogicService.getSecurityUserList();
        return new ResponseEntity<>(findAll, HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
	public ResponseEntity<Long> addSecurityUser(@RequestBody SecurityUserDTO addSecurityUser) throws Exception {
        Long newUserId = adminLogicService.addSecurityUser(addSecurityUser);
        if(newUserId == null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(newUserId, HttpStatus.OK);
        }
    }

    // add user
    // update user
    // update user password
    // disable user
    // remove user

    
    // get list of roles, filter by role as parent? e.g get all children?

    // @RequestMapping(value = "/role/list", method = RequestMethod.GET)
	// public ResponseEntity<List<SecurityUser>> getSecurityRoleList() throws Exception {
    //     List<SecurityUser> findAll = securityRoleRepository.findAll();
    //     return new ResponseEntity<>(findAll, HttpStatus.OK);
    // }

    

    // add role
    // update role, change parent?
    // remove role

}
