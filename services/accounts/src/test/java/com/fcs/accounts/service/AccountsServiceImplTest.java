package com.fcs.accounts.service;

import com.fcs.accounts.dto.AccountsDto;
import com.fcs.accounts.dto.CustomerDto;
import com.fcs.accounts.entity.Account;
import com.fcs.accounts.entity.Customer;
import com.fcs.accounts.exception.ResourceNotFoundException;
import com.fcs.accounts.repository.AccountRepository;
import com.fcs.accounts.repository.CustomerRepository;
import com.fcs.accounts.service.impl.AccountsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountsServiceImpl accountsService;

    private CustomerDto customerDto;
    private AccountsDto accountsDto;
    private Account account;
    private Customer customer;

    private static final Long ACCOUNT_NUMBER = 1234567890L;
    private static final Long CUSTOMER_ID = 1L;
    private static final String MOBILE_NUMBER = "1234567890";

    @BeforeEach
    void setUp() {
        accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(ACCOUNT_NUMBER);
        accountsDto.setAccountType("Savings");
        accountsDto.setBranchAddress("123 Main Street");

        customerDto = new CustomerDto();
        customerDto.setName("Jane Doe");
        customerDto.setEmail("jane.doe@example.com");
        customerDto.setMobileNumber("9876543210");
        customerDto.setAccountsDto(accountsDto);

        account = new Account();
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setCustomerId(CUSTOMER_ID);
        account.setAccountType("Current");
        account.setBranchAddress("Old Address");

        customer = new Customer();
        customer.setCustomerId(CUSTOMER_ID);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setMobileNumber("1234567890");
    }

    @Test
    @DisplayName("Should successfully update account and customer when valid data is provided")
    void updateAccount_WhenValidData_ShouldReturnTrue() {
        when(accountRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        boolean isUpdated = accountsService.updateAccount(customerDto);

        assertTrue(isUpdated);
        verify(accountRepository).findById(ACCOUNT_NUMBER);
        verify(accountRepository).save(account);
        verify(customerRepository).findById(CUSTOMER_ID);
        verify(customerRepository).save(customer);

        assertEquals("Savings", account.getAccountType());
        assertEquals("123 Main Street", account.getBranchAddress());
        assertEquals("Jane Doe", customer.getName());
        assertEquals("jane.doe@example.com", customer.getEmail());
        assertEquals("9876543210", customer.getMobileNumber());
    }

    @Test
    @DisplayName("Should return false when customerDto is null")
    void updateAccount_WhenCustomerDtoIsNull_ShouldReturnFalse() {
        boolean isUpdated = accountsService.updateAccount(null);

        assertFalse(isUpdated);
        verifyNoInteractions(accountRepository, customerRepository);
    }

    @Test
    @DisplayName("Should return false when accountsDto is null")
    void updateAccount_WhenAccountsDtoIsNull_ShouldReturnFalse() {
        customerDto.setAccountsDto(null);

        boolean isUpdated = accountsService.updateAccount(customerDto);

        assertFalse(isUpdated);
        verifyNoInteractions(accountRepository, customerRepository);
    }

    @Test
    @DisplayName("Should return false when accountNumber is null")
    void updateAccount_WhenAccountNumberIsNull_ShouldReturnFalse() {
        accountsDto.setAccountNumber(null);

        boolean isUpdated = accountsService.updateAccount(customerDto);

        assertFalse(isUpdated);
        verifyNoInteractions(accountRepository, customerRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account is not found")
    void updateAccount_WhenAccountNotFound_ShouldThrowResourceNotFoundException() {
        when(accountRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountsService.updateAccount(customerDto)
        );

        assertTrue(exception.getMessage().contains("Account not found with the given input data accountNumber"));
        verify(accountRepository).findById(ACCOUNT_NUMBER);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(customerRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer is not found")
    void updateAccount_WhenCustomerNotFound_ShouldThrowResourceNotFoundException() {
        when(accountRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountsService.updateAccount(customerDto)
        );

        assertTrue(exception.getMessage().contains("Customer not found with the given input data customerId"));
        verify(accountRepository).findById(ACCOUNT_NUMBER);
        verify(accountRepository).save(account);
        verify(customerRepository).findById(CUSTOMER_ID);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully delete account and customer when customer exists")
    void deleteAccount_WhenCustomerExists_ShouldReturnTrue() {
        when(customerRepository.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.of(customer));

        boolean isDeleted = accountsService.deleteAccount(MOBILE_NUMBER);

        assertTrue(isDeleted);
        verify(customerRepository).findByMobileNumber(MOBILE_NUMBER);
        verify(accountRepository).deleteByCustomerId(CUSTOMER_ID);
        verify(customerRepository).deleteById(CUSTOMER_ID);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer to delete is not found")
    void deleteAccount_WhenCustomerNotFound_ShouldThrowResourceNotFoundException() {
        when(customerRepository.findByMobileNumber(MOBILE_NUMBER)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountsService.deleteAccount(MOBILE_NUMBER)
        );

        assertTrue(exception.getMessage().contains("Customer not found with the given input data mobileNumber"));
        verify(customerRepository).findByMobileNumber(MOBILE_NUMBER);
        verifyNoInteractions(accountRepository);
        verify(customerRepository, never()).deleteById(any());
    }
}

