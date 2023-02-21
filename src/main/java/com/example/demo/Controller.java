package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@RestController
public class Controller {

    private final InMemoryUserDetailsManager manager;

    @Autowired
    public Controller(InMemoryUserDetailsManager manager) {
        this.manager = manager;
    }

    @GetMapping("/")
    public String userExists() {
        return "Hello. It's bank lol";
    }

    // @GetMapping("/createAccount")
    @PostMapping("/createAccount")
    public AccountDTO createAccount(double balance, String password) throws Exception {
        AccountDTO account = OperationManager.createAccount(balance);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> currentPrincipalRoles = authentication.getAuthorities();

        if (currentPrincipalRoles.stream().noneMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        manager.createUser(User.withDefaultPasswordEncoder()
                .username(account.getId().toString())
                .password(password)
                .roles("USER")
                .build());

        return account;
    }

    // @GetMapping("/createTransaction")
    @PostMapping("/createTransaction")
    public TransactionDTO createTransaction(BigInteger sender, BigInteger receiver, double money) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        if (!Objects.equals(currentPrincipalName, sender.toString())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return OperationManager.createTransaction(sender, receiver, money);
    }

    // @GetMapping("/cancelTransaction")
    @PostMapping("/cancelTransaction")
    public TransactionDTO cancelTransaction(BigInteger id) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Collection<? extends GrantedAuthority> currentPrincipalRoles = authentication.getAuthorities();
        if ((!Objects.equals(
                currentPrincipalName,
                OperationManager.getTransaction(id).getSender().toString())
        )
                && (!Objects.equals(
                        currentPrincipalName,
                OperationManager.getTransaction(id).getReceiver().toString())
        )
                && (currentPrincipalRoles.stream().noneMatch(r -> r.getAuthority().equals("ROLE_ADMIN")))
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return OperationManager.cancelTransaction(id);
    }

    @GetMapping("/getAccount")
    public AccountDTO getAccount(BigInteger id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Collection<? extends GrantedAuthority> currentPrincipalRoles = authentication.getAuthorities();
        if ((!Objects.equals(currentPrincipalName, id.toString())
                && (currentPrincipalRoles.stream().noneMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return OperationManager.getAccount(id);
    }

    @GetMapping("/getListAccounts")
    public Map<BigInteger, AccountDTO> getListAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Collection<? extends GrantedAuthority> currentPrincipalRoles = authentication.getAuthorities();
        if (currentPrincipalRoles.stream().noneMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return OperationManager.getListAccounts();
    }

    @GetMapping("/getListTransactions")
    public Map<BigInteger, TransactionDTO> getListTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> currentPrincipalRoles = authentication.getAuthorities();
        if (currentPrincipalRoles.stream().noneMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return OperationManager.getListTransactions();
    }
}
