package com.restapiexample.controllers;

import com.restapiexample.exception.ContactAlreadyPresentException;
import com.restapiexample.exception.ContactNotFoundException;
import com.restapiexample.exception.ContactNumberMismatchException;
import com.restapiexample.model.Contact;
import com.restapiexample.repository.ContactRepository;
import com.restapiexample.util.CommonUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.TelephoneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ContactController {

    @Autowired
    private CommonUtils commonUtils;

    @GetMapping("/all_contacts")
    public Iterable<Contact> getAllContacts() {
        return commonUtils.getAllContacts();
    }

    @PostMapping("/create_contact")
    @ResponseStatus(HttpStatus.CREATED)
    public Contact createContact(@RequestBody Contact contact) {
        return commonUtils.createContact(contact);
    }

    @PutMapping("/{phoneNumber}")
    public Contact updateContact(@RequestBody Contact contact, @PathVariable Long phoneNumber) {
        return commonUtils.updateContact(contact, phoneNumber);
    }

    @GetMapping("/{getPhoneNumber}")
    public Optional<Contact> findOneContact(@PathVariable Long getPhoneNumber) {
        return commonUtils.findOneContact(getPhoneNumber);
    }

    @DeleteMapping("/{getPhoneNumber}")
    public void deleteContact(@PathVariable Long getPhoneNumber) {
        commonUtils.deleteContact(getPhoneNumber);
    }
}
