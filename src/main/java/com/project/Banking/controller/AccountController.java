package com.project.Banking.controller;

import com.project.Banking.entity.Account;
import com.project.Banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/banking")
public class AccountController {


    @Autowired
    private AccountService accountService;

//    public AccountController(AccountService accountService) {
//        this.accountService = accountService;
//    }

    //add Account REST API
    @PostMapping
    public ResponseEntity<Account> addAccount(@RequestBody Account account){
        try{
           account.setDate(LocalDateTime.now());
           accountService.createAccount(account);
           return new ResponseEntity<>(account, HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // get all account REST API
    @GetMapping
    public ResponseEntity<?> getAll(){
        List<Account> all = accountService.getAll();
        if(all!=null && !all.isEmpty()){
            return new ResponseEntity<>(all,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // get the detail of particular account through id. REST API
    @GetMapping("/id/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id){
        Optional<Account> account= accountService.findById(id);
        if(account.isPresent()){
            return new ResponseEntity<>(account.get(),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // deposit to particular account. REST API
    @PutMapping("/deposit/id/{id}")
    public ResponseEntity<Account> deposit(@RequestBody Account newAccount, @PathVariable Long id){
        if(newAccount.getBalance()<=0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Account> account = accountService.findById(id);
        if(account.isPresent()){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Previous Balance", String.valueOf(account.get().getBalance()));

            account.get().setBalance(account.get().getBalance()+ newAccount.getBalance());
            accountService.createAccount(account.get());

            return new ResponseEntity<>(account.get(),headers,HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //withdraw from particular account. REST API
    @PutMapping("/withdraw/id/{id}")
    public ResponseEntity<Account> withdraw(@RequestBody Account newAccount, @PathVariable Long id){
        Optional<Account> account= accountService.findById(id);
        if(account.isPresent()){
            if(account.get().getBalance()<newAccount.getBalance()){
                HttpHeaders headers = new HttpHeaders();
                headers.add("Insufficent amount",String.valueOf(account.get().getBalance()));

                return new ResponseEntity<>(headers,HttpStatus.BAD_REQUEST);
            }
            else{
                HttpHeaders headers1 = new HttpHeaders();
                headers1.add("Previous Balance", String.valueOf(account.get().getBalance()));

                account.get().setBalance(account.get().getBalance()- newAccount.getBalance());
                accountService.createAccount(account.get());
                return new ResponseEntity<>(account.get(),headers1,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //deleting the account from database. REST API
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        accountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
