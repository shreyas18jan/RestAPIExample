package com.restapiexample.util;

import com.restapiexample.exception.ContactAlreadyPresentException;
import com.restapiexample.exception.ContactNotFoundException;
import com.restapiexample.exception.ContactNumberMismatchException;
import com.restapiexample.model.Contact;
import com.restapiexample.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class CommonUtils {

    @Autowired
    private ContactRepository contactRepository;

    public Contact createContact(@RequestBody Contact contact) {
        if(contactRepository.findById(contact.getPhoneNumber()).isPresent()) {
            throw new ContactAlreadyPresentException();
        }
        return contactRepository.save(contact);
    }

    public Contact updateContact(@RequestBody Contact contact, @PathVariable Long phoneNumber) {
        if (contact.getPhoneNumber().longValue() != phoneNumber.longValue()) {
            throw new ContactNumberMismatchException();
        }
        contactRepository.findById(phoneNumber)
                .orElseThrow(ContactNotFoundException::new);
        return contactRepository.save(contact);
    }

    public Iterable<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Optional<Contact> findOneContact(@PathVariable Long getPhoneNumber) {
        return contactRepository.findById(getPhoneNumber);
    }

    public void deleteContact(@PathVariable Long getPhoneNumber) {
        contactRepository.findById(getPhoneNumber)
                .orElseThrow(ContactNotFoundException::new);
        contactRepository.deleteById(getPhoneNumber);
    }
}
