import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

abstract class Member {
    private int memberId;
    private String name, email, phone;
    private Map<String, LocalDate> borrowedBooks = new LinkedHashMap<>();

    public Member(int id, String name, String email, String phone) {
        this.memberId = id; this.name = name; this.email = email; this.phone = phone;
    }
    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public void setName(String v) { name = v; }
    public void setEmail(String v) { email = v; }
    public void setPhone(String v) { phone = v; }
    public abstract int getBorrowLimit();
    public abstract double getFinePerDay();

    public boolean borrow(String isbn, LocalDate dueDate) {
        if (isbn == null || dueDate == null || borrowedBooks.containsKey(isbn)
                || borrowedBooks.size() >= getBorrowLimit()) return false;
        borrowedBooks.put(isbn, dueDate);
        return true;
    }
    public LocalDate returnBook(String isbn) { return borrowedBooks.remove(isbn); }
    public boolean hasBorrowed(String isbn) { return borrowedBooks.containsKey(isbn); }
    public int getBorrowedCount() { return borrowedBooks.size(); }
    public Map<String, LocalDate> getBorrowedBooks() {
        return Collections.unmodifiableMap(borrowedBooks);
    }
    public double calcFine(LocalDate due, LocalDate returned) {
        if (due == null || returned == null || !returned.isAfter(due)) return 0;
        return ChronoUnit.DAYS.between(due, returned) * getFinePerDay();
    }
    public void displayBorrowedBooks() {
        System.out.println("Borrowed books for " + name + ":");
        if (borrowedBooks.isEmpty()) System.out.println("  None");
        for (Map.Entry<String, LocalDate> e : borrowedBooks.entrySet())
            System.out.println("  ISBN: " + e.getKey() + ", Due: " + e.getValue());
    }
    public String toString() {
        return "Member ID: " + memberId + ", Name: " + name + ", Email: " + email
                + ", Phone: " + phone + ", Borrowed: " + borrowedBooks.size()
                + "/" + getBorrowLimit();
    }
}

class StudentMember extends Member {
    private int semester, borrowLimit, priority;
    private String department;
    public StudentMember(int id, String name, String email, String phone,
                         int semester, String department) {
        super(id, name, email, phone);
        this.semester = semester; this.department = department;
        borrowLimit = 3; priority = 1;
    }
    public int getSemester() { return semester; }
    public String getDepartment() { return department; }
    public int getPriority() { return priority; }
    public void setSemester(int v) { semester = v; }
    public void setDepartment(String v) { department = v; }
    public void setPriority(int v) { priority = v; }
    public int getBorrowLimit() { return borrowLimit; }
    public void setBorrowLimit(int v) {
        if (v < 1) throw new IllegalArgumentException("Invalid borrow limit");
        borrowLimit = v;
    }
    public double getFinePerDay() { return 2.0; }
    public String toString() {
        return "Student " + super.toString() + ", Semester: " + semester
                + ", Department: " + department;
    }
}

class FacultyMember extends Member {
    private String designation, facultyDepartment;
    private int borrowLimit, priority;
    public FacultyMember(int id, String name, String email, String phone,
                         String designation, String department) {
        super(id, name, email, phone);
        this.designation = designation; facultyDepartment = department;
        borrowLimit = 5; priority = 2;
    }
    public String getDesignation() { return designation; }
    public String getFacultyDepartment() { return facultyDepartment; }
    public int getPriority() { return priority; }
    public void setDesignation(String v) { designation = v; }
    public void setFacultyDepartment(String v) { facultyDepartment = v; }
    public void setPriority(int v) { priority = v; }
    public int getBorrowLimit() { return borrowLimit; }
    public void setBorrowLimit(int v) {
        if (v < 1) throw new IllegalArgumentException("Invalid borrow limit");
        borrowLimit = v;
    }
    public double getFinePerDay() { return 1.0; }
    public String toString() {
        return "Faculty " + super.toString() + ", Designation: " + designation
                + ", Department: " + facultyDepartment;
    }
}

class Book {
    private String bookId, isbn, title, author, category;
    private boolean available;
    public Book(String id, String isbn, String title, String author, String category) {
        bookId = id; this.isbn = isbn; this.title = title;
        this.author = author; this.category = category; available = true;
    }
    public String getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
    public void setTitle(String v) { title = v; }
    public void setAuthor(String v) { author = v; }
    public void setCategory(String v) { category = v; }
    public void setAvailable(boolean v) { available = v; }
    public String toString() {
        return "Book ID: " + bookId + ", ISBN: " + isbn + ", Title: " + title
                + ", Author: " + author + ", Category: " + category
                + ", Availability: " + (available ? "Available" : "Issued");
    }
}

class LibraryService {
    private String libraryName;
    private Map<Integer, Member> members = new LinkedHashMap<>();
    private Map<String, Book> books = new LinkedHashMap<>();
    private List<String> dailyTransactions = new ArrayList<>();
    private int loanPeriodDays = 14;

    public LibraryService(String name) { libraryName = name; }
    public String getLibraryName() { return libraryName; }
    public int getLoanPeriodDays() { return loanPeriodDays; }
    public void setLoanPeriodDays(int days) {
        if (days < 1) throw new IllegalArgumentException("Invalid loan period");
        loanPeriodDays = days;
    }
    public void addMember(Member m) {
        if (m == null || members.containsKey(m.getMemberId()))
            throw new IllegalArgumentException("Invalid or duplicate member");
        members.put(m.getMemberId(), m);
        dailyTransactions.add("MEMBER ADDED: " + m.getMemberId());
        System.out.println("Member added: " + m.getName());
    }
    public void addBook(Book b) {
        if (b == null || books.containsKey(b.getIsbn()))
            throw new IllegalArgumentException("Invalid or duplicate ISBN");
        books.put(b.getIsbn(), b);
        dailyTransactions.add("BOOK ADDED: " + b.getTitle());
        System.out.println("Book added: " + b.getTitle());
    }
    public Member getMember(int id) { return members.get(id); }
    public Book getBook(String isbn) { return books.get(isbn); }

    // Overloaded search methods: title, author and ISBN.
    public List<Book> search(String title) {
        List<Book> found = new ArrayList<>();
        for (Book b : books.values())
            if (b.getTitle().toLowerCase().contains(title.toLowerCase())) found.add(b);
        return found;
    }
    public List<Book> search(String author, boolean byAuthor) {
        List<Book> found = new ArrayList<>();
        if (byAuthor) for (Book b : books.values())
            if (b.getAuthor().toLowerCase().contains(author.toLowerCase())) found.add(b);
        return found;
    }
    public Book search(long isbn) { return books.get(String.valueOf(isbn)); }

    public boolean issueBook(int memberId, String isbn) {
        Member m = members.get(memberId); Book b = books.get(isbn);
        if (m == null || b == null || !b.isAvailable()) {
            System.out.println("Issue failed: check member, book and availability.");
            return false;
        }
        LocalDate due = LocalDate.now().plusDays(loanPeriodDays);
        if (!m.borrow(isbn, due)) {
            System.out.println("Issue failed: member borrow limit reached.");
            return false;
        }
        b.setAvailable(false);
        dailyTransactions.add("ISSUED: " + isbn + " to member " + memberId + ", due " + due);
        System.out.println("Book issued. Due date: " + due);
        return true;
    }
    public double returnBook(int memberId, String isbn, LocalDate returnDate) {
        Member m = members.get(memberId); Book b = books.get(isbn);
        if (m == null || b == null || !m.hasBorrowed(isbn)) {
            System.out.println("Return failed: issue record not found.");
            return -1;
        }
        LocalDate due = m.returnBook(isbn);
        b.setAvailable(true);
        double fine = m.calcFine(due, returnDate);
        dailyTransactions.add("RETURNED: " + isbn + " by member " + memberId
                + ", fine Rs." + String.format("%.2f", fine));
        System.out.println("Book returned. Fine: Rs." + String.format("%.2f", fine));
        return fine;
    }
    public void listBooks() {
        System.out.println("\\n--- BOOK CATALOGUE ---");
        for (Book b : books.values()) System.out.println(b);
    }
    public void listMembers() {
        System.out.println("\\n--- MEMBERS ---");
        for (Member m : members.values()) System.out.println(m);
    }
    public void listBorrowedItems(int memberId) {
        Member m = members.get(memberId);
        if (m == null) System.out.println("Member not found.");
        else m.displayBorrowedBooks();
    }
    public void printDailyIssueReturnReport() {
        System.out.println("\\n--- DAILY ISSUE/RETURN REPORT: " + LocalDate.now() + " ---");
        int issued = 0, returned = 0;
        for (String t : dailyTransactions) {
            System.out.println(t);
            if (t.startsWith("ISSUED:")) issued++;
            if (t.startsWith("RETURNED:")) returned++;
        }
        System.out.println("Books issued: " + issued);
        System.out.println("Books returned: " + returned);
        System.out.println("Total activities: " + dailyTransactions.size());
    }
    public void showFinePolicy() {
        System.out.println("Student fine: Rs.2 per overdue day");
        System.out.println("Faculty fine: Rs.1 per overdue day");
    }
}

public class LibraryAppMain {
    public static void main(String[] args) {
        LibraryService library = new LibraryService("ABC College Library");
        System.out.println("===== " + library.getLibraryName() + " =====");

        StudentMember student = new StudentMember(101, "Arun", "arun@gmail.com",
                "9876543210", 4, "Computer Science");
        FacultyMember faculty = new FacultyMember(201, "Priya", "priya@college.edu",
                "9876501234", "Assistant Professor", "Information Technology");
        library.addMember(student);
        library.addMember(faculty);

        library.addBook(new Book("B001", "9780135166307", "Java Programming",
                "James Gosling", "Programming"));
        library.addBook(new Book("B002", "9780262033848", "Introduction to Algorithms",
                "Thomas H. Cormen", "Computer Science"));
        library.addBook(new Book("B003", "9780132350884", "Clean Code",
                "Robert C. Martin", "Software Engineering"));
        library.addBook(new Book("B004", "9781492056355", "Learning Python",
                "Mark Lutz", "Programming"));

        library.listMembers();
        library.listBooks();

        System.out.println("\\n--- ISSUE BOOKS ---");
        library.issueBook(101, "9780135166307");
        library.issueBook(201, "9780262033848");

        System.out.println("\\n--- SEARCH BY TITLE ---");
        for (Book b : library.search("Java")) System.out.println(b);
        System.out.println("\\n--- SEARCH BY AUTHOR ---");
        for (Book b : library.search("Robert", true)) System.out.println(b);
        System.out.println("\\n--- SEARCH BY ISBN ---");
        System.out.println(library.search(9780132350884L));

        System.out.println("\\n--- BORROWED ITEMS ---");
        library.listBorrowedItems(101);
        library.listBorrowedItems(201);

        System.out.println("\\n--- RETURN BOOKS ---");
        library.returnBook(101, "9780135166307", LocalDate.now().plusDays(17));
        library.returnBook(201, "9780262033848", LocalDate.now());

        library.listBooks();
        library.showFinePolicy();
        library.printDailyIssueReturnReport();
        System.out.println("\\nLibrary Application Completed.");
    }
}
