package com.gkemayo.library.loan;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("loanService")
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements ILoanService {

    private final ILoanDao loanDao;

    @Override
    public List<Loan> findAllLoansByEndDateBefore(LocalDate maxEndDate) {
        return loanDao.findByEndDateBefore(maxEndDate);
    }

    @Override
    public List<Loan> getAllOpenLoansOfThisCustomer(String email, LoanStatus status) {
        return loanDao.getAllOpenLoansOfThisCustomer(email, status);
    }

    @Override
    public Loan getOpenedLoan(SimpleLoanDTO simpleLoanDTO) {
        return loanDao.getLoanByCriteria(simpleLoanDTO.getBookId(),
                simpleLoanDTO.getCustomerId(), LoanStatus.OPEN);
    }

    @Override
    public boolean checkIfLoanExists(SimpleLoanDTO simpleLoanDTO) {
        Loan loan = loanDao.getLoanByCriteria(simpleLoanDTO.getBookId(),
                simpleLoanDTO.getCustomerId(), LoanStatus.OPEN);
        return loan != null;
    }

    @Override
    public Loan saveLoan(Loan loan) {
        return loanDao.save(loan);
    }

    /**
     * We will do logical deletion, because the status of the Loan object is set to CLOSE.
     */
    @Override
    public void closeLoan(Loan loan) {
        loanDao.save(loan);
    }
}