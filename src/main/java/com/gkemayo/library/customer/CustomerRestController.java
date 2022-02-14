package com.gkemayo.library.customer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/customer/api")
@Api(value = "Customer Rest Controller: contains all operations for managing customers")
public class CustomerRestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(CustomerRestController.class);

    private final CustomerServiceImpl customerService;

    private final JavaMailSender javaMailSender;

    /**
     * Adds a new customer to the H2 database. If the client already exists, a code is returned indicating that the creation was unsuccessful.
     * @param customerDTORequest
     * @return
     */
    @PostMapping("/addCustomer")
    @ApiOperation(value = "Add a new Customer in the Library", response = CustomerDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict: the customer already exist"),
            @ApiResponse(code = 201, message = "Created: the customer is successfully inserted"),
            @ApiResponse(code = 304, message = "Not Modified: the customer is unsuccessfully inserted") })
    public ResponseEntity<CustomerDTO> createNewCustomer(@RequestBody CustomerDTO customerDTORequest) {
        //, UriComponentsBuilder uriComponentBuilder
        Customer existingCustomer = customerService.findCustomerByEmail(customerDTORequest.getEmail());
        if (existingCustomer != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Customer customerRequest = mapCustomerDTOToCustomer(customerDTORequest);
        customerRequest.setCreationDate(LocalDate.now());
        Customer customerResponse = customerService.saveCustomer(customerRequest);
        if (customerResponse != null) {
            CustomerDTO customerDTO = mapCustomerToCustomerDTO(customerResponse);
            return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    }

    /**
     * Updates customer data in the H2 database. If the client is not found, a code is returned indicating that the update was unsuccessful.
     * @param customerDTORequest
     * @return
     */
    @PutMapping("/updateCustomer")
    @ApiOperation(value = "Update/Modify an existing customer in the Library", response = CustomerDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Not Found : the customer does not exist"),
            @ApiResponse(code = 200, message = "Ok: the customer is successfully updated"),
            @ApiResponse(code = 304, message = "Not Modified: the customer is unsuccessfully updated") })
    public ResponseEntity<CustomerDTO> updateCustomer(@RequestBody CustomerDTO customerDTORequest) {
        //, UriComponentsBuilder uriComponentBuilder
        if (!customerService.checkIfIdExists(customerDTORequest.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Customer customerRequest = mapCustomerDTOToCustomer(customerDTORequest);
        Customer customerResponse = customerService.updateCustomer(customerRequest);
        if (customerResponse != null) {
            CustomerDTO customerDTO = mapCustomerToCustomerDTO(customerResponse);
            return new ResponseEntity<>(customerDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    /**
     * Deletes a customer in the H2 database. If the client is not found, the HTTP Status NO_CONTENT is returned.
     * @param customerId
     * @return
     */
    @DeleteMapping("/deleteCustomer/{customerId}")
    @ApiOperation(value = "Delete a customer in the Library, if the customer does not exist, nothing is done", response = String.class)
    @ApiResponse(code = 204, message = "No Content: customer successfully deleted")
    public ResponseEntity<String> deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Returns all customers in pages with begin and end page.
     * @param beginPage
     * @param endPage
     * @return
     */
    @GetMapping("/paginatedSearch")
    @ApiOperation(value="List customers of the Library in a paginated way", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfully listed"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam("beginPage") int beginPage,
                                                             @RequestParam("endPage") int endPage) {
        //, UriComponentsBuilder uriComponentBuilder
        Page<Customer> customers = customerService.getPaginatedCustomersList(beginPage, endPage);
        if (customers != null) {
            List<CustomerDTO> customerDTOs = customers.stream().map(this::mapCustomerToCustomerDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(customerDTOs, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Returns the customer having the email address passed in parameter.
     * @param email
     * @return
     */
    @GetMapping("/searchByEmail")
    @ApiOperation(value="Search a customer in the Library by its email", response = CustomerDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfull research"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<CustomerDTO> searchCustomerByEmail(@RequestParam("email") String email) {
        //, UriComponentsBuilder uriComponentBuilder
        Customer customer = customerService.findCustomerByEmail(email);
        if (customer != null) {
            CustomerDTO customerDTO = mapCustomerToCustomerDTO(customer);
            return new ResponseEntity<>(customerDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Returns the customer having the last name passed in parameter.
     * @param lastName
     * @return
     */
    @GetMapping("/searchByLastName")
    @ApiOperation(value="Search a customer in the Library by its Last name", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfull research"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<List<CustomerDTO>> searchBookByLastName(@RequestParam("lastName") String lastName) {
        //,	UriComponentsBuilder uriComponentBuilder
        List<Customer> customers = customerService.findCustomerByLastName(lastName);
        if (customers != null && !CollectionUtils.isEmpty(customers)) {
            List<CustomerDTO> customerDTOs = customers.stream().map(this::mapCustomerToCustomerDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(customerDTOs, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Email a customer. The MailDTO object contains the identifier and the email of the customer concerned,
     * the subject of the email and the content of the message.
     * @param loanMailDto
     * @param uriComponentBuilder
     * @return
     */
    @PutMapping("/sendEmailToCustomer")
    @ApiOperation(value="Send an email to customer of the Library", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: Email successfully sent"),
            @ApiResponse(code = 404, message = "Not Found: no customer found, or wrong email"),
            @ApiResponse(code = 403, message = "Forbidden: Email cannot be sent")
    })
    public ResponseEntity<Boolean> sendMailToCustomer(@RequestBody MailDTO loanMailDto, UriComponentsBuilder uriComponentBuilder) {

        Customer customer = customerService.findCustomerById(loanMailDto.getCustomerId());
        if (customer == null) {
            String errorMessage = "The selected Customer for sending email is not found in the database";
            LOGGER.info(errorMessage);
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else if (ObjectUtils.isEmpty(customer.getEmail())) {
            String errorMessage = "No existing email for the selected Customer for sending email to";
            LOGGER.info(errorMessage);
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(MailDTO.MAIL_FROM);
        mail.setTo(customer.getEmail());
        mail.setSentDate(new Date());
        mail.setSubject(loanMailDto.getEmailSubject());
        mail.setText(loanMailDto.getEmailContent());

        try {
            javaMailSender.send(mail);
        } catch (MailException e) {
            return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * Transform an entity Customer to a POJO CustomerDTO
     *
     * @param customer
     * @return
     */
    private CustomerDTO mapCustomerToCustomerDTO(Customer customer) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(customer, CustomerDTO.class);
    }

    /**
     * Transform a POJO CustomerDTO to an entity Customer
     *
     * @param customerDTO
     * @return
     */
    private Customer mapCustomerDTOToCustomer(CustomerDTO customerDTO) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(customerDTO, Customer.class);
    }

}
