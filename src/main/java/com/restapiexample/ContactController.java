package com.restapiexample;

import com.restapiexample.exception.ContactAlreadyPresentException;
import com.restapiexample.exception.ContactNotFoundException;
import com.restapiexample.exception.ContactNumberMismatchException;
import com.restapiexample.model.Contact;
import com.restapiexample.repository.ContactRepository;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.TelephoneType;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
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

    //
    // Code snippet to upload the VCF file
    //
    @PostMapping("/upload_single_contact")
    public ResponseEntity<?> uploadVCFFile(@RequestParam("file") MultipartFile vcfFile) {
        if(vcfFile.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not expected empty file");
        else {
            try {
                VCard vcard = Ezvcard.parse(vcfFile.getInputStream()).first();
                Contact uploadContact = new Contact();
                uploadContact.setUserName(vcard.getFormattedName().getValue());
                uploadContact.setPhoneNumber(Long.parseLong(vcard.getTelephoneNumbers().get(0).getUri().getNumber().split("-")[1]));
                uploadContact.setPhNumberType(vcard.getTelephoneNumbers().get(0).getTypes().get(0).getValue().toUpperCase());
                createContact(uploadContact);
                return ResponseEntity.ok("Uploaded file successfully!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //
    // Code snippet to download VCF file
    //
    @GetMapping("/download_single_contact/{getPhoneNumber}")
    public ResponseEntity<?> downloadVCFFile(@PathVariable Long getPhoneNumber) throws IOException {
        Optional<Contact> singleContact = contactRepository.findById(getPhoneNumber);

        if(singleContact.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please check the number again");
        else {
            VCard vcard = new VCard();
            vcard.setFormattedName(singleContact.get().getUserName());
            vcard.addTelephoneNumber(singleContact.get().getPhoneNumber().toString(),
                    TelephoneType.get(singleContact.get().getPhNumberType().toUpperCase()));

            StringWriter writer = new StringWriter();
            Ezvcard.write(vcard).go(writer);
            String vcardString = writer.toString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/vcard"));
            headers.setContentDispositionFormData("attachment", "contact.vcf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(vcardString);
        }
    }
}
