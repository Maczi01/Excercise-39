package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class PaymentService {

    private PaymentRepository paymentRepository;
    private DateTimeProvider dateTimeProvider;

    PaymentService(PaymentRepository paymentRepository, DateTimeProvider dateTimeProvider) {
        this.paymentRepository = paymentRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    List<Payment> findPaymentsSortedByDateDesc() {
        List<Payment> lp = paymentRepository.findAll()
                .parallelStream()
                .sorted(Comparator.comparing(Payment::getPaymentDate))
                .collect(Collectors.toList());
        Collections.reverse(lp);
        return lp;


    }

    List<Payment> findPaymentsForCurrentMonth() {
//        return paymentRepository.findAll()
//                .stream()
//                .filter(payment -> payment.getPaymentDate().getYear() == (LocalDate.now().getYear()))
//                .filter(payment -> payment.getPaymentDate().getMonth() == (LocalDate.now().getMonth()))
//                .collect(Collectors.toList());

        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getYear() == (dateTimeProvider.zonedDateTimeNow().getYear()))
                .filter(payment -> payment.getPaymentDate().getMonth() == (dateTimeProvider.zonedDateTimeNow().getMonth()))
                .collect(Collectors.toList());

    }

    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getYear() == (dateTimeProvider.zonedDateTimeNow().getYear()))
                .filter(payment -> payment.getPaymentDate().getMonth().equals(dateTimeProvider.zonedDateTimeNow().getMonth()))
                .collect(Collectors.toList());
    }

    List<Payment> findPaymentsForGivenLastDays(int days) {
        Long d = Long.valueOf(days);
        ZonedDateTime dateTime1 = dateTimeProvider.zonedDateTimeNow();
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().compareTo(dateTime1.minusDays(d)) > 0)
                .collect(Collectors.toList());
    }

    Set<Payment> findPaymentsWithOnePaymentItem() {
        Set<Payment> collect = paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentItems().size() == 1)
                .collect(Collectors.toSet());
        return collect;
    }

    Set<String> findProductsSoldInCurrentMonth() {

        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getYear() == (dateTimeProvider.zonedDateTimeNow().getYear()))
                .filter(payment -> payment.getPaymentDate().getMonth().equals(dateTimeProvider.zonedDateTimeNow().getMonth()))
                .map(payment -> payment.getPaymentItems())
                .flatMap(paymentItems -> paymentItems.stream())
                .map(paymentItem -> paymentItem.getName())
                .collect(Collectors.toSet());
    }

    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getYear() == (yearMonth.getYear()))
                .filter(payment -> payment.getPaymentDate().getMonth().equals(yearMonth.getMonth()))
                .map(payment -> payment.getPaymentItems())
                .flatMap(paymentItems -> paymentItems.stream())
                .map(paymentItem -> paymentItem.getFinalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getYear() == (yearMonth.getYear()))
                .filter(payment -> payment.getPaymentDate().getMonth().equals(yearMonth.getMonth()))
                .map(payment -> payment.getPaymentItems())
                .flatMap(paymentItems -> paymentItems.stream())
                .map(paymentItem -> paymentItem.getRegularPrice().subtract(paymentItem.getFinalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    List<PaymentItem> getPaymentsForUserWithEmail(String userEmail) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getUser().getEmail().equals(userEmail))
                .map(payment -> payment.getPaymentItems())
                .flatMap(paymentItems -> paymentItems.stream())
                .collect(Collectors.toList());
    }

    Set<Payment> findPaymentsWithValueOver(int value) {

        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentItems()
                        .stream()
                        .map(item -> item.getRegularPrice())
                        .reduce(BigDecimal::add).get().compareTo(BigDecimal.valueOf(value)) == 1)
                .collect(Collectors.toSet());


//        BigDecimal bd = new BigDecimal(value);
//        List<Payment> all = paymentRepository.findAll();
//                .stream()
//                .map(Payment::getPaymentItems)
//                .flatMap(PaymentItem::getFinalPrice)
//                .filter()

//                .stream()
//                .flatMap(payment -> payment.getPaymentItems().stream())
//                .map(paymentItem -> paymentItem.getFinalPrice())
//                .map(finalprice ->finalprice.doubleValue())
//                .filter(finalPrice -> finalPrice>value)
//                .collect(Collectors.toList());

//                .stream()
//                .flatMap(payment -> payment.getPaymentItems().stream())
//                .map(paymentItem -> paymentItem.getFinalPrice())
//
//                .filter(paymentItem -> paymentItem.getFinalPrice().compareTo(BigDecimal.valueOf(value))>=1)
//                .collect(Collectors.toSet());
//        paymentRepository.findAll()
//




//                 .stream()
//                .flatMap(payment -> payment.getPaymentItems().stream())
//                .map(paymentItem -> paymentItem.getFinalPrice().add(paymentItem.getFinalPrice()).compareTo(BigDecimal.valueOf(Long.valueOf(value))))
//                .collect(Collectors.toSet());


//        throw new RuntimeException("Not implemented");
    }

}
