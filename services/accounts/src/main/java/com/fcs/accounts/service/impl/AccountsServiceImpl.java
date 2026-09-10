package com.fcs.accounts.service.impl;

import com.fcs.accounts.constants.AccountsConstants;
import com.fcs.accounts.dto.AccountsDto;
import com.fcs.accounts.dto.CustomerDto;
import com.fcs.accounts.entity.Account;
import com.fcs.accounts.entity.Customer;
import com.fcs.accounts.exception.CustomerAlreadyExistsException;
import com.fcs.accounts.exception.ResourceNotFoundException;
import com.fcs.accounts.mapper.AccountsMapper;
import com.fcs.accounts.mapper.CustomerMapper;
import com.fcs.accounts.repository.AccountRepository;
import com.fcs.accounts.repository.CustomerRepository;
import com.fcs.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber "
                    +customerDto.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);
        accountRepository.save(createNewAccount(savedCustomer));
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Account createNewAccount(final Customer customer) {
        Account newAccount = new Account();
        newAccount.setCustomerId(customer.getCustomerId());
        final long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */
    @Override
    public CustomerDto fetchAccount(final String mobileNumber) {
        final Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        final Account account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );
        final CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(account, new AccountsDto()));
        return customerDto;
    }

    @Override
    @Transactional
    public boolean updateAccount(CustomerDto customerDto) {
        if (customerDto == null || customerDto.getAccountsDto() == null) {
            return false;
        }

        AccountsDto accountsDto = customerDto.getAccountsDto();
        Long accountNumber = accountsDto.getAccountNumber();
        if (accountNumber == null) {
            return false;
        }

        Account account = updateAccountDetails(accountsDto);
        updateCustomerDetails(account.getCustomerId(), customerDto);

        return true;
    }

    private Account updateAccountDetails(AccountsDto accountsDto) {
        Account account = accountRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "accountNumber", accountsDto.getAccountNumber().toString())
        );
        AccountsMapper.mapToAccounts(accountsDto, account);
        return accountRepository.save(account);
    }

    private void updateCustomerDetails(Long customerId, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "customerId", customerId.toString())
        );
        CustomerMapper.mapToCustomer(customerDto, customer);
        customerRepository.save(customer);
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        return false;
    }
}
