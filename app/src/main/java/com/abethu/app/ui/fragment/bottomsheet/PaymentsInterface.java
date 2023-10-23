package com.abethu.app.ui.fragment.bottomsheet;

import com.abethu.app.model.Invoice;

public interface PaymentsInterface {

    void onPaymentSucceeded(Invoice invoice);

    void onPaymentFailed(String failureReason);

    void onMakePayPalPayment(Invoice invoice);
}
