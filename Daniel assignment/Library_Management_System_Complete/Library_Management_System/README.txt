LIBRARY MANAGEMENT SYSTEM - JAVA

Based on the supplied Assignment Question 4.

Classes:
- Member: base class with member details and borrowed-book records.
- StudentMember: student details, borrow limit and fine policy.
- FacultyMember: faculty details, borrow limit and fine policy.
- Book: book details, ISBN and availability.
- LibraryService: members, books, issue/return, search and reports.
- LibraryAppMain: main class with sample test data.

OOP concepts:
- Inheritance: StudentMember and FacultyMember extend Member.
- Method overloading: search by title, author and ISBN.
- Method overriding: different fine rates for students and faculty.
- Polymorphism: both member types are stored as Member references.
- Encapsulation: private fields with getters/setters.

Compile and run (JDK 8 or later):
  javac -d out src/LibraryAppMain.java
  java -cp out LibraryAppMain
