package com.ilias.library.service;

import com.ilias.library.entity.Book;
import com.ilias.library.entity.User;
import com.ilias.library.repository.BookRepository;
import com.ilias.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class AdminService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getBorrowedBooks() {
        return bookRepository.findAllBorrowedBooks();
    }

    public void notifyUserToReturnBook(Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
        if (user != null) {
            System.out.println("Notify user: " + user.getUsername() + " to return their borrowed books.");
        }
    }

    public ByteArrayInputStream exportToPdf() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Books Report"));

            PdfPTable table = new PdfPTable(3); // Adjust columns as per Book entity
            table.addCell("ID");
            table.addCell("Title");
            table.addCell("Copies Available");

            List<Book> books = bookRepository.findAll();
            for (Book book : books) {
                table.addCell(book.getId().toString());
                table.addCell(book.getTitle());
                table.addCell(String.valueOf(book.getCopiesAvailable()));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Sheet sheet = workbook.createSheet("Books Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Title");
        header.createCell(2).setCellValue("Copies Available");

        List<Book> books = bookRepository.findAll();
        int rowIdx = 1;
        for (Book book : books) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(book.getId());
            row.createCell(1).setCellValue(book.getTitle());
            row.createCell(2).setCellValue(book.getCopiesAvailable());
        }

        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
