package com.fcs.accounts.controller;

import com.fcs.accounts.constants.AccountsConstants;
import com.fcs.accounts.dto.CustomerDto;
import com.fcs.accounts.dto.ResponseDto;
import com.fcs.accounts.service.IAccountsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private IAccountsService iAccountsService;

    @InjectMocks
    private AccountController accountController;

    private static final String MOBILE_NUMBER = "1234567890";

    @Test
    @DisplayName("Should return 200 OK when account deletion is successful")
    void deleteAccountDetails_WhenSuccessful_ShouldReturn200() {
        when(iAccountsService.deleteAccount(MOBILE_NUMBER)).thenReturn(true);

        ResponseEntity<ResponseDto> response = accountController.deleteAccountDetails(MOBILE_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(AccountsConstants.STATUS_200, response.getBody().getStatusCode());
        assertEquals(AccountsConstants.MESSAGE_200, response.getBody().getStatusMsg());
        verify(iAccountsService).deleteAccount(MOBILE_NUMBER);
    }

    @Test
    @DisplayName("Should return 417 Expectation Failed when account deletion fails")
    void deleteAccountDetails_WhenFailed_ShouldReturn417() {
        when(iAccountsService.deleteAccount(MOBILE_NUMBER)).thenReturn(false);

        ResponseEntity<ResponseDto> response = accountController.deleteAccountDetails(MOBILE_NUMBER);

        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(AccountsConstants.STATUS_417, response.getBody().getStatusCode());
        assertEquals(AccountsConstants.MESSAGE_417_DELETE, response.getBody().getStatusMsg());
        verify(iAccountsService).deleteAccount(MOBILE_NUMBER);
    }

    @Test
    @DisplayName("Should return 200 OK when account update is successful")
    void updateAccountDetails_WhenSuccessful_ShouldReturn200() {
        CustomerDto customerDto = new CustomerDto();
        when(iAccountsService.updateAccount(customerDto)).thenReturn(true);

        ResponseEntity<ResponseDto> response = accountController.updateAccountDetails(customerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(AccountsConstants.STATUS_200, response.getBody().getStatusCode());
        assertEquals(AccountsConstants.MESSAGE_200, response.getBody().getStatusMsg());
        verify(iAccountsService).updateAccount(customerDto);
    }

    @Test
    @DisplayName("Should return 417 Expectation Failed when account update fails")
    void updateAccountDetails_WhenFailed_ShouldReturn417() {
        CustomerDto customerDto = new CustomerDto();
        when(iAccountsService.updateAccount(customerDto)).thenReturn(false);

        ResponseEntity<ResponseDto> response = accountController.updateAccountDetails(customerDto);

        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(AccountsConstants.STATUS_417, response.getBody().getStatusCode());
        assertEquals(AccountsConstants.MESSAGE_417_UPDATE, response.getBody().getStatusMsg());
        verify(iAccountsService).updateAccount(customerDto);
    }
}
