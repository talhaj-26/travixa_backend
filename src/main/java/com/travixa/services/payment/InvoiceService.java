package com.travixa.services.payment;

import com.travixa.entity.booking.Booking;
import com.travixa.entity.payment_financial.Invoice;
import com.travixa.enums.InvoiceStatus;
import com.travixa.repository.booking.BookingGuestRepository;
import com.travixa.repository.booking.BookingItemRepository;
import com.travixa.repository.payment.InvoiceRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingItemRepository bookingItems;
    private final BookingGuestRepository bookingGuests;
    private final com.travixa.repository.hotel.RoomRepository rooms;
    private final com.travixa.repository.hotel.HotelImageRepository hotelImages;
    private final com.travixa.services.platform.FileStorageService fileStorage;

    public InvoiceService(InvoiceRepository invoiceRepository, BookingItemRepository bookingItems,
                          BookingGuestRepository bookingGuests,
                          com.travixa.repository.hotel.RoomRepository rooms,
                          com.travixa.repository.hotel.HotelImageRepository hotelImages,
                          com.travixa.services.platform.FileStorageService fileStorage) {
        this.invoiceRepository = invoiceRepository;
        this.bookingItems = bookingItems;
        this.bookingGuests = bookingGuests;
        this.rooms = rooms;
        this.hotelImages = hotelImages;
        this.fileStorage = fileStorage;
    }

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> findAllByCustomer(String userName) {
        return invoiceRepository.findAllByBooking_Customer_UserName(userName);
    }

    public Optional<Invoice> findByIdAndCustomer(Long id, String userName) {
        return invoiceRepository.findByIdAndBooking_Customer_UserName(id, userName);
    }

    @Transactional
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice issueForBooking(Booking booking) {
        return invoiceRepository.findFirstByBooking_Id(booking.getId()).orElseGet(() -> {
            Invoice invoice = new Invoice();
            invoice.setBooking(booking);
            invoice.setInvoiceNumber("INV-" + booking.getReferenceNumber());
            invoice.setIssuedOn(java.time.LocalDate.now());
            invoice.setSubtotal(booking.getSubtotalAmount());
            invoice.setTaxAmount(BigDecimal.ZERO);
            invoice.setTotalAmount(booking.getTotalAmount());
            invoice.setCurrency(booking.getCurrency());
            invoice.setStatus(InvoiceStatus.ISSUED);
            return invoiceRepository.save(invoice);
        });
    }

    @Transactional
    public void voidForBooking(Long bookingId) {
        invoiceRepository.findFirstByBooking_Id(bookingId)
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.ISSUED)
                .ifPresent(invoice -> {
                    invoice.setStatus(InvoiceStatus.VOID);
                    invoiceRepository.save(invoice);
                });
    }

    @Transactional(readOnly = true)
    public byte[] generatePdf(Invoice invoice) {
        try {
            JasperDesign design = invoiceDesign();
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoiceNumber", invoice.getInvoiceNumber());
            parameters.put("issuedOn", invoice.getIssuedOn().toString());
            parameters.put("bookingReference", invoice.getBooking().getReferenceNumber());
            parameters.put("serviceDates", dateRange(invoice));
            parameters.put("services", bookingItems.findAllByBooking_Id(invoice.getBooking().getId()).stream()
                    .map(item -> item.getResourceType().name() + " · " + item.getDescriptionSnapshot()
                            + " · " + item.getQuantity() + " × " + money(item.getUnitPrice(), item.getCurrency().name()))
                    .collect(java.util.stream.Collectors.joining(" | ")));
            String namedGuests = bookingGuests.findAllByBooking_Id(invoice.getBooking().getId()).stream()
                    .map(guest -> guest.getFullName() + (guest.getEmail() == null ? "" : " · " + guest.getEmail()))
                    .collect(java.util.stream.Collectors.joining(" | "));
            parameters.put("guests", invoice.getBooking().getGuestCount() + " traveller(s)"
                    + (namedGuests.isBlank() ? "" : " · " + namedGuests));
            parameters.put("customerName", customerName(invoice));
            parameters.put("customerEmail", invoice.getBooking().getCustomer().getEmail());
            parameters.put("bookingStatus", invoice.getBooking().getStatus().name());
            parameters.put("specialRequest", invoice.getBooking().getSpecialRequest() == null
                    ? "No special requests provided" : invoice.getBooking().getSpecialRequest());
            parameters.put("personalTouch", invoice.getBooking().getPersonalTouch() == null
                    ? "No additional preferences" : invoice.getBooking().getPersonalTouch());
            parameters.put("couponText", invoice.getBooking().getCouponCode() == null ? "—" : invoice.getBooking().getCouponCode());
            parameters.put("discount", money(invoice.getBooking().getDiscountAmount(), invoice.getCurrency().name()));
            parameters.put("invoiceStatus", invoice.getStatus().name());
            parameters.put("paymentStatus", switch (invoice.getStatus()) {
                case PAID -> "PAID";
                case VOID -> "CANCELLED";
                case ISSUED -> "PENDING";
            });
            parameters.put("subtotal", money(invoice.getSubtotal(), invoice.getCurrency().name()));
            parameters.put("tax", money(invoice.getTaxAmount(), invoice.getCurrency().name()));
            parameters.put("total", money(invoice.getTotalAmount(), invoice.getCurrency().name()));
            parameters.put("hotelImage", hotelCover(invoice.getBooking().getId()));
            JasperPrint print = JasperFillManager.fillReport(JasperCompileManager.compileReport(design), parameters,
                    new JREmptyDataSource(1));
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException ex) {
            throw new IllegalStateException("Invoice PDF could not be generated", ex);
        }
    }

    private JasperDesign invoiceDesign() throws JRException {
        JasperDesign design = new JasperDesign();
        design.setName("TravixaInvoice");
        design.setPageWidth(595);
        design.setPageHeight(842);
        design.setLeftMargin(42);
        design.setRightMargin(42);
        design.setTopMargin(18);
        design.setBottomMargin(18);
        design.setColumnCount(1);
        design.setColumnWidth(511);
        addParameter(design, "invoiceNumber");
        addParameter(design, "issuedOn");
        addParameter(design, "bookingReference");
        addParameter(design, "customerName");
        addParameter(design, "serviceDates");
        addParameter(design, "services");
        addParameter(design, "guests");
        addParameter(design, "customerEmail");
        addParameter(design, "bookingStatus");
        addParameter(design, "invoiceStatus");
        addParameter(design, "paymentStatus");
        addParameter(design, "subtotal");
        addParameter(design, "specialRequest");
        addParameter(design, "couponText");
        addParameter(design, "discount");
        addParameter(design, "personalTouch");
        JRDesignParameter imageParameter = new JRDesignParameter();
        imageParameter.setName("hotelImage");
        imageParameter.setValueClass(InputStream.class);
        design.addParameter(imageParameter);
        addParameter(design, "tax");
        addParameter(design, "total");
        JRDesignBand band = new JRDesignBand();
        band.setHeight(800);
        Color navy = new Color(10, 27, 58);
        Color blue = new Color(38, 91, 166);
        Color gold = new Color(218, 174, 83);
        Color ink = new Color(25, 43, 71);
        Color muted = new Color(104, 119, 142);
        Color pale = new Color(241, 246, 253);
        Color border = new Color(222, 230, 241);

        addBackground(band, 0, 0, 511, 800, new Color(250, 251, 254));
        addBackground(band, 0, 0, 511, 112, navy);
        addBackground(band, 0, 109, 511, 3, gold);
        addText(band, "TRAVIXA", 18, 16, 300, 27, 21, true, Color.WHITE);
        addText(band, "TRAVEL  ·  EXPERIENCE", 19, 43, 280, 15, 8, true, new Color(198, 217, 244));
        addText(band, "BOOKING INVOICE", 18, 70, 330, 24, 16, true, Color.WHITE);
        addText(band, "A clear record of your confirmed journey", 18, 91, 350, 14, 8, false, new Color(205, 219, 240));
        JRDesignImage image = new JRDesignImage(design);
        image.setX(406);
        image.setY(18);
        image.setWidth(88);
        image.setHeight(72);
        image.setScaleImage(net.sf.jasperreports.engine.type.ScaleImageEnum.RETAIN_SHAPE);
        image.setExpression(new JRDesignExpression("$P{hotelImage}"));
        band.addElement(image);

        // Invoice identity and booking reference.
        addBackground(band, 0, 126, 511, 54, pale);
        addText(band, "INVOICE NUMBER", 17, 134, 220, 13, 8, true, muted);
        addField(band, "invoiceNumber", 17, 150, 225, 20, 12, true);
        addText(band, "ISSUED ON", 285, 134, 195, 13, 8, true, muted);
        addField(band, "issuedOn", 285, 150, 195, 20, 12, true);
        addText(band, "BOOKING REFERENCE", 0, 194, 180, 13, 8, true, muted);
        addField(band, "bookingReference", 0, 210, 500, 22, 13, true);

        // Traveler and trip details.
        addBackground(band, 0, 246, 248, 103, Color.WHITE);
        addBackground(band, 260, 246, 251, 103, Color.WHITE);
        addBackground(band, 0, 246, 248, 2, blue);
        addBackground(band, 260, 246, 251, 2, gold);
        addText(band, "TRAVELER", 15, 258, 210, 13, 8, true, muted);
        addField(band, "customerName", 15, 276, 218, 21, 12, true);
        addField(band, "customerEmail", 15, 303, 218, 27, 9, false);
        addText(band, "JOURNEY DETAILS", 275, 258, 215, 13, 8, true, muted);
        addField(band, "serviceDates", 275, 276, 220, 22, 11, true);
        addField(band, "guests", 275, 304, 220, 24, 9, false);

        // Purchased service and live booking/payment states.
        addBackground(band, 0, 366, 511, 82, Color.WHITE);
        addText(band, "YOUR RESERVED EXPERIENCE", 15, 377, 470, 14, 8, true, muted);
        addField(band, "services", 15, 397, 478, 40, 10, true);
        addBackground(band, 0, 462, 248, 48, pale);
        addBackground(band, 260, 462, 251, 48, pale);
        addText(band, "BOOKING STATUS", 14, 470, 215, 12, 8, true, muted);
        addField(band, "bookingStatus", 14, 486, 215, 18, 10, true);
        addText(band, "PAYMENT STATUS", 274, 470, 215, 12, 8, true, muted);
        addField(band, "paymentStatus", 274, 486, 215, 18, 10, true);

        // Optional traveler preferences remain part of the same invoice design.
        addText(band, "PERSONAL TOUCH", 0, 528, 238, 13, 8, true, muted);
        addField(band, "personalTouch", 0, 544, 238, 34, 9, false);
        addText(band, "SPECIAL REQUEST", 260, 528, 235, 13, 8, true, muted);
        addField(band, "specialRequest", 260, 544, 251, 34, 9, false);

        // Clear itemized amount summary and a strong final total.
        addBackground(band, 0, 596, 511, 2, border);
        addText(band, "PRICE SUMMARY", 0, 606, 300, 18, 10, true, blue);
        addText(band, "Subtotal", 0, 632, 300, 18, 9, false, ink);
        addField(band, "subtotal", 320, 632, 190, 18, 9, true);
        addText(band, "Tax", 0, 654, 300, 18, 9, false, ink);
        addField(band, "tax", 320, 654, 190, 18, 9, false);
        addText(band, "Promo / discount", 0, 676, 130, 18, 9, false, ink);
        addField(band, "couponText", 134, 676, 160, 18, 9, false);
        addField(band, "discount", 320, 676, 190, 18, 9, true);
        addBackground(band, 0, 706, 511, 48, navy);
        addBackground(band, 0, 706, 4, 48, gold);
        addText(band, "TOTAL PAYABLE", 18, 718, 270, 20, 11, true, Color.WHITE);
        addField(band, "total", 305, 716, 190, 23, 14, true, Color.WHITE);
        addBackground(band, 0, 774, 511, 1, border);
        addText(band, "TRAVIXA  ·  Please keep this invoice with your travel documents.",
                0, 780, 511, 14, 8, false, muted);
        design.setTitle(band);
        return design;
    }

    private void addParameter(JasperDesign design, String name) throws JRException {
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setName(name);
        parameter.setValueClass(String.class);
        design.addParameter(parameter);
    }

    private void addText(JRDesignBand band, String value, int x, int y, int w, int h, int size, boolean bold, Color color) {
        JRDesignStaticText element = new JRDesignStaticText();
        element.setText(value);
        element.setX(x);
        element.setY(y);
        element.setWidth(w);
        element.setHeight(h);
        element.setForecolor(color);
        element.setFontSize(Float.valueOf(size));
        element.setBold(bold);
        band.addElement(element);
    }

    private void addBackground(JRDesignBand band, int x, int y, int width, int height, Color color) {
        net.sf.jasperreports.engine.design.JRDesignRectangle rectangle = new net.sf.jasperreports.engine.design.JRDesignRectangle();
        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setBackcolor(color);
        rectangle.setMode(net.sf.jasperreports.engine.type.ModeEnum.OPAQUE);
        band.addElement(rectangle);
    }

    private void addField(JRDesignBand band, String parameter, int x, int y, int w, int h, int size, boolean bold) {
        addField(band, parameter, x, y, w, h, size, bold, Color.BLACK);
    }

    private void addField(JRDesignBand band, String parameter, int x, int y, int w, int h, int size,
                          boolean bold, Color color) {
        JRDesignTextField element = new JRDesignTextField();
        element.setX(x);
        element.setY(y);
        element.setWidth(w);
        element.setHeight(h);
        element.setFontSize(Float.valueOf(size));
        element.setBold(bold);
        element.setForecolor(color);
        element.setExpression(new JRDesignExpression("$P{" + parameter + "}"));
        band.addElement(element);
    }

    private String money(BigDecimal amount, String currency) {
        return currency + " " + amount.toPlainString();
    }

    private InputStream hotelCover(Long bookingId) {
        return bookingItems.findAllByBooking_Id(bookingId).stream()
                .filter(item -> item.getResourceType() == com.travixa.enums.BookingType.HOTEL)
                .findFirst()
                .flatMap(item -> rooms.findById(item.getResourceId()))
                .flatMap(room -> hotelImages.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(room.getHotel().getId()).stream()
                        .sorted(java.util.Comparator.comparing(com.travixa.entity.hotel_stays.HotelImage::isCoverImage).reversed())
                        .findFirst())
                .map(image -> (InputStream) new ByteArrayInputStream(fileStorage.load(image.getStorageKey())))
                .orElse(null);
    }

    private String customerName(Invoice invoice) {
        String first = invoice.getBooking().getCustomer().getUserFirstName();
        String last = invoice.getBooking().getCustomer().getUserLastName();
        String name = ((first == null ? "" : first.trim()) + " " + (last == null ? "" : last.trim())).trim();
        return name.isEmpty() ? invoice.getBooking().getCustomer().getUserName() : name;
    }

    private String dateRange(Invoice invoice) {
        var start = invoice.getBooking().getServiceStartDate();
        var end = invoice.getBooking().getServiceEndDate();
        if (start == null && end == null) return "Service date not specified";
        return (start == null ? "—" : start.toString()) + " to " + (end == null ? "—" : end.toString());
    }
}
