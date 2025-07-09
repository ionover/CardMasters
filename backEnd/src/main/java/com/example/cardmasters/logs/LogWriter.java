package com.example.cardmasters.logs;

import com.example.cardmasters.dto.Amount;
import com.example.cardmasters.dto.Card;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LogWriter {

    private static final String LOG_FILE = "transfer.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Добавляет запись о переводе в лог-файл.
     *
     * @param cardFrom   номер карты списания
     * @param cardTo     номер карты зачисления
     * @param amount     сумма перевода (в копейках или центах)
     * @param commission сумма комиссии (в тех же единицах)
     * @param result     результат операции (например, "SUCCESS" или сообщение об ошибке)
     */
    public synchronized void addTransactionLog(String cardFrom,
                                               String cardTo,
                                               Amount amount,
                                               long commission,
                                               String result) {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DATE_FORMATTER);
        String time = now.format(TIME_FORMATTER);

        String logEntry = String.format(
                "%s %s | С карты номер: %s, на карту номер: %s, зафиксирован перевод: %s с комиссией: %d | %s",
                date, time, cardFrom, cardTo, amount, commission, result);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (Exception e) {
            System.out.println("При записи лога транзакции произошла ошибка. Причина: " + e.getMessage());
        }
    }

    public synchronized void addConfirmLog(Integer id, boolean success) {

        String message = success ? "успешно проведена." : "не проведена.";

        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DATE_FORMATTER);
        String time = now.format(TIME_FORMATTER);

        String logEntry = String.format("%s %s | Транзакция с ID: '%d' %s",
                                        date, time, id, message);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (Exception e) {
            System.out.println("При записи лога транзакции произошла ошибка. Причина: " + e.getMessage());
        }
    }

    public synchronized void addCardLog(Card createdCard, boolean success) {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DATE_FORMATTER);
        String time = now.format(TIME_FORMATTER);

        String status = success ? "добавлена успешно" : "не добавлена";

        String logEntry = String.format("%s %s | Карта номер: %s. Баланс: %d. Срок: %s. CVV: %d. | %s",
                                        date, time, createdCard.getNumber(), createdCard.getBalance(),
                                        createdCard.getValidTill(), createdCard.getCvv(), status);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (Exception e) {
            System.out.println("При записи лога добавления карты произошла ошибка. Причина: " + e.getMessage());
        }
    }
}
