package com.restapiexample.controllers;

import com.restapiexample.model.Contact;
import com.restapiexample.util.CommonUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.TelephoneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

@RestController
@RequestMapping("/extended/api")
public class ContactExtendedController {

    @Autowired
    private CommonUtils commonUtils;

    //
    // Code snippet to upload the VCF file
    //
    @RequestMapping(value = "/upload_single_contact", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
                commonUtils.createContact(uploadContact);
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
        Optional<Contact> singleContact = commonUtils.findOneContact(getPhoneNumber);

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
