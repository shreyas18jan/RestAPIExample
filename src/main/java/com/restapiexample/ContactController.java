package com.restapiexample;

import com.restapiexample.exception.ContactAlreadyPresentException;
import com.restapiexample.exception.ContactNotFoundException;
import com.restapiexample.exception.ContactNumberMismatchException;
import com.restapiexample.model.Contact;
import com.restapiexample.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ContactController {
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/all_contacts")
    public Iterable getAllContacts() {
        return contactRepository.findAll();
    }

    @PostMapping("/create_contact")
    @ResponseStatus(HttpStatus.CREATED)
    public Contact createContact(@RequestBody Contact contact) {
        if(contactRepository.findById(contact.getPhoneNumber()).isPresent()) {
            throw new ContactAlreadyPresentException();
        }
        return contactRepository.save(contact);
    }

    @PutMapping("/{phoneNumber}")
    public Contact updateContact(@RequestBody Contact contact, @PathVariable Long phoneNumber) {
        if (contact.getPhoneNumber().longValue() != phoneNumber.longValue()) {
            throw new ContactNumberMismatchException();
        }
        contactRepository.findById(phoneNumber)
                .orElseThrow(ContactNotFoundException::new);
        return contactRepository.save(contact);
    }

    @GetMapping("/{getPhoneNumber}")
    public Optional<Contact> findOneContact(@PathVariable Long getPhoneNumber) {
        return contactRepository.findById(getPhoneNumber);
    }

    @DeleteMapping("/{getPhoneNumber}")
    public void deleteContact(@PathVariable Long getPhoneNumber) {
        contactRepository.findById(getPhoneNumber)
                .orElseThrow(ContactNotFoundException::new);
        contactRepository.deleteById(getPhoneNumber);
    }
}
