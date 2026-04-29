import java.util.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        ConsoleManager consoleManager = new ConsoleManager();
        consoleManager.run();
    }
}


class ConsoleManager {
    private Scanner scanner;
    private User currentUser;
    private DataManager dataManager;
    private LoginManager loginManager;
    private BookOperations bookOperations;
    private ReadingSession readingSession;
    private MenuManager menuManager;

    public ConsoleManager() {
        AchievementNotifier achievementNotifier = new AchievementNotifier();
        achievementNotifier.addObserver(new ConsoleAchievementObserver());
        scanner = new Scanner(System.in);
        currentUser = null;
        dataManager = new DataManager();

        loginManager = new LoginManager(scanner, dataManager);
        bookOperations = new BookOperations(scanner, dataManager);
        readingSession = new ReadingSession(scanner, achievementNotifier);

        menuManager = new MenuManager(scanner, dataManager, bookOperations, readingSession, loginManager);
    }

    public void run() {
        System.out.println("   BOOK READING APP   ");
        menuManager.run();
    }
}




interface Menu {
    void display();
    String handleInput(Scanner scanner, User currentUser, DataManager dataManager,
                       BookOperations bookOperations, ReadingSession readingSession);
    boolean exit();
}

class LoginMenu implements Menu {
    private Menu nextMenu;
    private boolean exit = false;

    public void display() {
        System.out.println("══════════════════════════════════════════════════");
        System.out.println("                    LOGIN");
        System.out.println("══════════════════════════════════════════════════");
        System.out.println("---1.Login");
        System.out.println("---2.Register");
        System.out.println("---3.Exit");
        System.out.print("Choose option (1-3): ");
    }

    public String handleInput(Scanner scanner, User currentUser, DataManager dataManager,
                              BookOperations bookOperations, ReadingSession readingSession) {
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                return "login";
            case "2":
                return "register";
            case "3":
                exit = true;
                return "exit";
            default:
                System.out.println("Invalid option!");
                return "invalid";
        }
    }
    public boolean exit() {
        return exit;
    }
}

class ReaderMainMenu implements Menu {
    public void display() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("       MAIN MENU ");
        System.out.println("══════════════════════════════════════════════════");
        System.out.println("---1.Library of all books");
        System.out.println("---2.View my library");
        System.out.println("---3.Add book to my library");
        System.out.println("---4.Remove from my library");
        System.out.println("---5.Read a book from my library");
        System.out.println("---6.View achievements");
        System.out.println("---7.Logout");
        System.out.print("Choose option (1-7): ");
    }

    public String handleInput(Scanner scanner, User currentUser, DataManager dataManager,
                              BookOperations bookOperations, ReadingSession readingSession) {
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": return "browseLibrary";
            case "2": return "viewMyLibrary";
            case "3": return "addToLibrary";
            case "4": return "removeFromLibrary";
            case "5": return "readBook";
            case "6": return "viewAchievements";
            case "7": return "logout";
            default:
                System.out.println("Invalid choice!");
                return "invalid";
        }
    }

    public boolean exit() {
        return false;
    }
}

class PublisherMainMenu implements Menu {
    public void display() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("       MAIN MENU ");
        System.out.println("       PUBLISHER MENU");
        System.out.println("══════════════════════════════════════════════════");
        System.out.println("---1.Library of all books");
        System.out.println("---2.Write or publish new book");
        System.out.println("---3.View my published books");
        System.out.println("---4.Remove my book");
        System.out.println("---5.View achievements");
        System.out.println("---6.Logout");
        System.out.print("Choose option (1-6): ");
    }

    public String handleInput(Scanner scanner, User currentUser, DataManager dataManager,
                              BookOperations bookOperations, ReadingSession readingSession) {
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": return "viewAllBooks";
            case "2": return "publishBook";
            case "3": return "viewMyPublished";
            case "4": return "removeMyBook";
            case "5": return "viewAchievements";
            case "6": return "logout";
            default:
                System.out.println("Invalid choice!");
                return "invalid";
        }
    }

    public boolean exit() {
        return false;
    }
}

class MenuManager {
    private Menu currentMenu;
    private Scanner scanner;
    private DataManager dataManager;
    private BookOperations bookOperations;
    private ReadingSession readingSession;
    private User currentUser;
    private LoginManager loginManager;

    public MenuManager(Scanner scanner, DataManager dataManager, BookOperations bookOperations,
                       ReadingSession readingSession, LoginManager loginManager) {
        this.scanner = scanner;
        this.dataManager = dataManager;
        this.bookOperations = bookOperations;
        this.readingSession = readingSession;
        this.loginManager = loginManager;
        this.currentMenu = new LoginMenu();
        this.currentUser = null;
    }

    public void run() {
        while (true) {
            currentMenu.display();

            String action = currentMenu.handleInput(scanner, currentUser, dataManager, bookOperations, readingSession);

            if (currentMenu.exit()) {
                System.out.println("\nThank you for using our app. See u next time!");
                break;
            }

            executeAction(action);

            updateMenu();
        }
    }

    private void executeAction(String action) {
        switch (action) {
            case "login":
                currentUser = loginManager.login();
                break;
            case "register":
                User newUser = loginManager.register();
                if (newUser != null) {
                    dataManager.addUser(newUser);
                    currentUser = newUser;
                    System.out.println("\nRegistered successfully!");
                }
                break;
            case "exit":
                System.exit(0);
                break;
            case "browseLibrary":
                browseLibrary();
                break;
            case "viewMyLibrary":
                viewMyLibrary();
                break;
            case "addToLibrary":
                addToMyLibrary();
                break;
            case "removeFromLibrary":
                removeFromMyLibrary();
                break;
            case "readBook":
                readBook();
                break;
            case "viewAllBooks":
                bookOperations.displayAllBooks(dataManager.getAllBooks());
                break;
            case "publishBook":
                publishBook();
                break;
            case "viewMyPublished":
                viewMyPublishedBooks();
                break;
            case "removeMyBook":
                removeMyBook();
                break;
            case "viewAchievements":
                viewAchievements();
                break;
            case "logout":
                currentUser = null;
                System.out.println("\nLogged out!");
                break;
        }
    }

    private void updateMenu() {
        if (currentUser == null) {
            currentMenu = new LoginMenu();
        } else if (currentUser.canPublish()) {
            currentMenu = new PublisherMainMenu();
        } else {
            currentMenu = new ReaderMainMenu();
        }
    }

    private void browseLibrary() {
        bookOperations.displayAllBooks(dataManager.getAllBooks());
        System.out.println("══════════════════════════════════════════════════");
        System.out.print("Enter book number to add to your library (0-to go back): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 0) {
                return;
            }

            List<Book> allBooks = dataManager.getAllBooks();
            if (choice < 1 || choice > allBooks.size()) {
                System.out.println("Invalid number! Enter: (1-" + allBooks.size() + ")");
                return;
            }

            Book selectedBook = allBooks.get(choice - 1);

            if (currentUser.hasBookInLibrary(selectedBook)) {
                System.out.println("Book is already in your library!");
            } else {
                currentUser.addBookToLibrary(selectedBook);
                System.out.println("Added to your personal library: " + selectedBook.getTitle());
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Enter a number.");
        }
    }

    private void viewMyPublishedBooks() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("              My published books");
        System.out.println("══════════════════════════════════════════════════");
        List<Book> publishedBooks = dataManager.getBooksByPublisher(currentUser.getUsername());

        if (publishedBooks.isEmpty()) {
            System.out.println("You haven't published any books yet.");
            return;
        }

        for (int i = 0; i < publishedBooks.size(); i++) {
            Book book = publishedBooks.get(i);
            System.out.println((i+1) + ". " + book.getTitle());
            System.out.println("   Genre: " + book.getGenre() + " | Pages: " + book.getTotalPages());
        }
    }

    private void removeMyBook() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("               Remove my book");
        System.out.println("══════════════════════════════════════════════════");

        List<Book> publishedBooks = dataManager.getBooksByPublisher(currentUser.getUsername());
        if (publishedBooks.isEmpty()) {
            System.out.println("You have no books to remove.");
            return;
        }

        for (int i = 0; i < publishedBooks.size(); i++) {
            System.out.println((i+1) + ". " + publishedBooks.get(i).getTitle());
        }

        System.out.println("══════════════════════════════════════════════════");
        System.out.print("Which book to remove (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 0) {
                return;
            }

            if (choice < 1 || choice > publishedBooks.size()) {
                System.out.println("Invalid number! Enter between (1-" + publishedBooks.size() + ")");
                return;
            }

            Book toRemove = publishedBooks.get(choice-1);
            dataManager.removeBook(toRemove);
            System.out.println("Removed: " + toRemove.getTitle());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Enter a number");
        }
    }

    private void viewMyLibrary() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("              My personal library");
        System.out.println("══════════════════════════════════════════════════");

        if (currentUser.isPersonalLibraryEmpty()) {
            System.out.println("Your library is empty. Add some books to read.");
            return;
        }

        List<Book> personalLibrary = currentUser.getPersonalLibrary();
        for (int i = 0; i < personalLibrary.size(); i++) {
            Book book = personalLibrary.get(i);
            System.out.println((i+1) + ") " + book.getTitle() + " by " + book.getAuthor());
            System.out.println("   Genre: " + book.getGenre() + "   Pages: " + book.getTotalPages());
        }
    }

    private void addToMyLibrary() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("           Add book to my library");
        System.out.println("══════════════════════════════════════════════════");

        List<Book> allBooks = dataManager.getAllBooks();
        if (allBooks.isEmpty()) {
            System.out.println("No books in main library!");
            return;
        }

        for (int i = 0; i < allBooks.size(); i++) {
            System.out.println((i+1) + ") " + allBooks.get(i).getTitle());
        }

        System.out.println("══════════════════════════════════════════════════");
        System.out.print("Which book to add (0-to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 0) {
                return;
            }

            if (choice < 1 || choice > allBooks.size()) {
                System.out.println("Invalid number! Enter number: (1-" + allBooks.size() + ")");
                return;
            }

            Book selectedBook = allBooks.get(choice-1);

            if (currentUser.hasBookInLibrary(selectedBook)) {
                System.out.println("Book is already in your library!");
            } else {
                currentUser.addBookToLibrary(selectedBook);
                System.out.println("Added to your personal library: " + selectedBook.getTitle());
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Enter a number.");
        }
    }

    private void removeFromMyLibrary() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("         Remove from my library");
        System.out.println("══════════════════════════════════════════════════");

        if (currentUser.isPersonalLibraryEmpty()) {
            System.out.println("Your library is empty!");
            return;
        }

        List<Book> personalLibrary = currentUser.getPersonalLibrary();
        for (int i = 0; i < personalLibrary.size(); i++) {
            System.out.println((i+1) + ") " + personalLibrary.get(i).getTitle());
        }

        System.out.println("══════════════════════════════════════════════════");
        System.out.print("Which book to remove (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 0) {
                return;
            }

            if (choice < 1 || choice > personalLibrary.size()) {
                System.out.println("Invalid input! Enter number: (1- " + personalLibrary.size() + ")");
                return;
            }

            Book removed = currentUser.removeBookFromLibrary(choice-1);
            System.out.println("Removed from your library: " + removed.getTitle());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Enter a number.");
        }
    }

    private void readBook() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("                  Read a book");
        System.out.println("══════════════════════════════════════════════════");

        if (currentUser.isPersonalLibraryEmpty()) {
            System.out.println("Your library is empty! Add books first.");
            return;
        }

        System.out.println("Books in your library:");
        List<Book> personalLibrary = currentUser.getPersonalLibrary();
        for (int i = 0; i < personalLibrary.size(); i++) {
            System.out.println((i+1) + ". " + personalLibrary.get(i).getTitle());
        }

        System.out.println("══════════════════════════════════════════════════");
        System.out.print("Which book to read (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 0) {
                return;
            }

            if (choice < 1 || choice > personalLibrary.size()) {
                System.out.println("Invalid input! Enter number: (1-" + personalLibrary.size() + ")");
                return;
            }

            Book book = personalLibrary.get(choice-1);
            readingSession.readBook(book, currentUser);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Enter a number.");
        }
    }

    private void publishBook() {
        Book newBook = bookOperations.publishBook(currentUser.getUsername());
        if (newBook != null) {
            dataManager.addBook(newBook);
            System.out.println("\nPublished successfully!");
        }
    }

    private void viewAchievements() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("                  Achievements!");
        System.out.println("══════════════════════════════════════════════════");

        System.out.println("Current Streak: " + currentUser.getStreak() + " days");

        if (currentUser.getStreak() >= 7) {
            System.out.println("Achievement: 7-Day Streak Reader!");
        }
        if (currentUser.getStreak() >= 30) {
            System.out.println("Achievement: Monthly Reader!");
        }

        int booksInLibrary = currentUser.getPersonalLibrary().size();
        if (booksInLibrary >= 3) {
            System.out.println("Achievement: Book Collector!!!");
        }
        if (booksInLibrary >= 5) {
            System.out.println("Achievement: Dedicated Reader!!!");
        }

        if (currentUser.getStreak() == 0 && booksInLibrary == 0) {
            System.out.println("Start reading to earn achievements!");
        }

        System.out.println("\nPress- enter");
        scanner.nextLine();
    }
}

class LoginManager {
    private Scanner scanner;
    private DataManager dataManager;

    public LoginManager(Scanner scanner, DataManager dataManager) {
        this.scanner = scanner;
        this.dataManager = dataManager;
    }

    public User login() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("                       Logging in");
        System.out.println("══════════════════════════════════════════════════");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        List<User> allUsers = dataManager.getAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("\nLogin successful! Welcome, " + username + "!");
                if (user.getLastReadDate() == null) {
                    System.out.println("First day of your streak!");
                }
                return user;
            }
        }

        System.out.println("\nLogin failed! ");
        System.out.println("Try again:(username-123456 or publisher-admin)");
        return null;
    }

    public User register() {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("               Register new account");
        System.out.println("══════════════════════════════════════════════════");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        if (dataManager.userExists(username)) {
            System.out.println("Username already exists or taken!");
            return null;
        }

        System.out.print("Choose password: ");
        String password = scanner.nextLine();

        System.out.print("You are reader or publisher?(Reader/Publisher): ");
        String type = scanner.nextLine().toUpperCase();

        if (!type.equals("READER") && !type.equals("PUBLISHER")) {
            type = "READER";
            System.out.println("Wrong input. Made default reader type");
        }

        UserFactory factory = new UserFactory();
        return factory.createUser(username, password, type);
    }
}




class DataManager {
    private BookManager bookManager;
    private UserManager userManager;

    public DataManager() {
        this.bookManager = new BookDataManager();
        this.userManager = new UserDataManager();
    }


    public List<Book> getAllBooks() {

        return bookManager.getAllBooks();
    }

    public List<User> getAllUsers() {

        return userManager.getAllUsers();
    }

    public void addBook(Book book) {

        bookManager.addBook(book);
    }

    public void addUser(User user) {

        userManager.addUser(user);
    }

    public void removeBook(Book book) {

        bookManager.removeBook(book);
    }

    public List<Book> getBooksByPublisher(String publisherUsername) {
        return bookManager.getBooksByPublisher(publisherUsername);
    }

    public User findUserByUsername(String username) {

        return userManager.findByUsername(username);
    }

    public boolean userExists(String username) {

        return userManager.userExists(username);
    }
}

interface BookManager {
    List<Book> getAllBooks();
    void addBook(Book book);
    void removeBook(Book book);
    List<Book> getBooksByPublisher(String publisherUsername);
}

interface UserManager {
    List<User> getAllUsers();
    void addUser(User user);
    User findByUsername(String username);
    boolean userExists(String username);
}


class BookDataManager implements BookManager {
    private List<Book> books;
    private BookFactory bookFactory;

    public BookDataManager() {
        this.books = new ArrayList<>();
        this.bookFactory = new BookFactory();
        initializeSampleBooks();
    }

    private void initializeSampleBooks() {
        Book book1 = bookFactory.createBook("Sapiens: A Brief History of Humankind", "Yuval Noah Harari", 360, "Science");
        Book book2 = bookFactory.createBook("The universe in a nutshell", "Steven hawking", 420, "Science");
        Book book3 = bookFactory.createBook("The Night Before New Year's", "Natasha Wing", 130, "Adventure");
        Book book4 = bookFactory.createBook("IT", "Steven King", 420, "Horror");
        Book book5 = bookFactory.createBook("The book of words", "Abai Kunanbaiuly", 340, "Literature");

        book1.addPage("Homo sapiens rules the world because it is the only animal that " +
                "\ncan believe in things that exist purely in its own imagination, " +
                "\nsuch as gods, states, money, and human rights.");
        book1.addPage("Starting from this provocative idea, Sapiens goes on to retell the history of our species from a " +
                "\ncompletely fresh perspective. It explains " +
                "\nthat money is the most pluralistic system of mutual trust ever devised; that capitalism is the most successful religion " +
                "\never invented; that the treatment of animals in modern" +
                "\nagriculture is probably the worst crime in history; and that even though we are far " +
                "\nmore powerful than our ancient ancestors, we aren't much happier.");

        book2.addPage("Imagine the cosmos not as a silent movie, but as a symphony. " +
                "\nThe stars and galaxies are not simply there; they are in motion. And where" +
                "\nthere is motion in the fabric of spacetime, there is sound-a deep, fundamental " +
                "\nvibration we cannot hear with our ears, but can, in a sense, see with" +
                "\nour mathematics");
        book2.addPage("In September 2015, the LIGO observatory heard a note. It was a fleeting chirp, lasting " +
                "\nless than a second. That chirp was the C-sharp of two black holes, each thirty times the mass of our" +
                "\nsun, performing their final, furious dance a billion light-years away.");

        book3.addPage("T'was the night before New Year's, and all through the town," +
                "\nNot a creature was stirring, the snow drifted down." +
                "\nThe stockings were hung by the chimney with care," +
                "\nIn hopes that St. Nicholas soon would be there.");
        book3.addPage("The children were nestled all snug in their beds," +
                "\nWhile visions of fireworks danced in their heads." +
                "\nAnd Mama in her bathrobe, and I with my hat," +
                "\nHad just settled down for a long winter's chat.");
        book3.addPage("When out on the lawn there arose such a clatter," +
                "\nWe sprang from our chairs to see what was the matter." +
                "\nAway to the window I flew like a flash," +
                "\nTore open the shutters and threw up the sash.");

        book4.addPage("The terror, which would not end for another twenty-eight years—if it ever did end—began," +
                "\nso far as I know or can tell, with a boat made from a sheet of newspaper floating down a gutter" +
                "\nswollen with rain. The boat bobbed, listed, righted itself again, dived bravely through" +
                "\nturbulent whirlpools, and continued on its way down Witcham Street toward the traffic light" +
                "\nwhich marked the intersection of Witcham and Jackson.");
        book4.addPage("The little boy who made the boat was named George Denbrough. He was six." +
                "\nHis brother, William, known to most of the kids at Derry Elementary School (and to the teachers" +
                "\nas well) as Stuttering Bill, was at home, sick in bed with the flu.");

        book5.addPage("If you want to be rich, acquire knowledge first." +
                "\nWealth will vanish, but knowledge remains forever." +
                "\nA man without knowledge is like a tree without roots -" +
                "\nit may stand tall for a while, but will eventually fall.");

        book5.addPage("Learn the craft of the Russian, but do not abandon your own." +
                "\nKnowledge of other languages opens new worlds," +
                "\nbut the mother tongue is the soul of the people." +
                "\nHe who forgets his language forgets his ancestors.");

        books.add(book1);
        books.add(book2);
        books.add(book3);
        books.add(book4);
        books.add(book5);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public List<Book> getBooksByPublisher(String publisherUsername) {
        List<Book> publisherBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getPublisher() != null && book.getPublisher().equals(publisherUsername)) {
                publisherBooks.add(book);
            }
        }
        return publisherBooks;
    }

}

class UserDataManager implements UserManager {
    private List<User> users;
    private UserFactory userFactory;

    public UserDataManager() {
        this.users = new ArrayList<>();
        this.userFactory = new UserFactory();
        initializeSampleUsers();
    }

    private void initializeSampleUsers() {
        User user1 = User.loadExistingUser("username", "123456", false, 6, LocalDate.now().minusDays(1));
        User user2 = User.loadExistingUser("book_worm", "123456", false, 29, LocalDate.now().minusDays(1));
        User publisher1 = userFactory.createUser("user_publisher", "admin", "PUBLISHER");

        users.add(user1);
        users.add(user2);
        users.add(publisher1);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean userExists(String username) {
        return findByUsername(username) != null;
    }
}


class Book {
    private String title;
    private String author;
    private String publisher;
    private int totalPages;
    private String genre;
    private List<String> pages;

    public Book(String title, String author, int totalPages, String genre) {
        this.title = title;
        this.author = author;
        this.totalPages = totalPages;
        this.genre = genre;
        this.pages = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public int getTotalPages() { return totalPages; }
    public String getGenre() { return genre; }
    public List<String> getPages() { return new ArrayList<>(pages); }

    public void addPage(String content) {
        pages.add(content);
    }

    public String getPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > pages.size()) {
            return pageNumber + "'s page- no content";
        }
        return pages.get(pageNumber - 1);
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Book other = (Book) obj;
        return title.equals(other.title) && author.equals(other.author);
    }

    public void print() {
        System.out.println(title + " by " + author);
    }
}

abstract class User {
    protected String username;
    protected String password;
    protected int streak;
    protected LocalDate lastReadDate;
    protected List<Book> personalLibrary;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.streak = 0;
        this.lastReadDate = null;
        this.personalLibrary = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getStreak() { return streak; }
    public LocalDate getLastReadDate() { return lastReadDate; }

    public List<Book> getPersonalLibrary() {
        return new ArrayList<>(personalLibrary);
    }

    public boolean isPersonalLibraryEmpty() {
        return personalLibrary.isEmpty();
    }

    public boolean hasBookInLibrary(Book book) {
        return personalLibrary.contains(book);
    }

    public void addBookToLibrary(Book book) {
        personalLibrary.add(book);
    }

    public Book removeBookFromLibrary(int index) {
        return personalLibrary.remove(index);
    }

    public abstract boolean canPublish();

    public void updateReading() {
        LocalDate today = LocalDate.now();

        if (lastReadDate == null) {
            streak = 1;
        } else if (lastReadDate.equals(today)) {
            return;
        } else if (lastReadDate.equals(today.minusDays(1))) {
            streak++;
        } else {
            streak = 1;
        }

        lastReadDate = today;
    }

    public static User loadExistingUser(String username, String password, boolean isPublisher, int streak, LocalDate lastRead) {
        User user;

        if (isPublisher) {
            user = new Publisher(username, password);
        } else {
            user = new Reader(username, password);
        }

        user.streak = streak;
        user.lastReadDate = lastRead;

        return user;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return username.equals(other.username);
    }
}


class Reader extends User {

    public Reader(String username, String password) {
        super(username, password);
    }

    public boolean canPublish() {
        return false;
    }
}

class Publisher extends User {

    public Publisher(String username, String password) {

        super(username, password);
    }

    public boolean canPublish() {
        return true;
    }
}


class BookFactory {
    public Book createBook(String title, String author, int pages, String genre) {
        return new Book(title, author, pages, genre);
    }
}

class UserFactory {

    public User createUser(String username, String password, String type) {
        if (type.equalsIgnoreCase("publisher")) {
            return new Publisher(username, password);
        }
        return new Reader(username, password);
    }
}



class ReadingManager {
    public void startReading(User user, Book book) {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("Starting reading...");
        System.out.println("Book: " + book.getTitle());
        System.out.println("══════════════════════════════════════════════════");
    }

    public void saveProgress(User user, Book book, int page) {
        System.out.println("\nSaving progress...");
        System.out.println("Book: " + book.getTitle());
        System.out.println("Page: " + page + "/" + book.getTotalPages());
        System.out.println("Progress saved!");
    }
}

class BookOperations {
    private Scanner scanner;
    private DataManager dataManager;

    public BookOperations(Scanner scanner, DataManager dataManager) {
        this.scanner = scanner;
        this.dataManager = dataManager;
    }

    public void displayAllBooks(List<Book> allBooks) {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("                 All books in the library");
        System.out.println("══════════════════════════════════════════════════");

        if (allBooks.isEmpty()) {
            System.out.println(" No books yet!");
            return;
        }

        for (int i = 0; i < allBooks.size(); i++) {
            Book book = allBooks.get(i);
            System.out.println((i+1) + ") " + book.getTitle() + " by " + book.getAuthor());
            System.out.println("   Pages: " + book.getTotalPages() + "  Genre: " + book.getGenre());
            System.out.println();
        }
    }

    public Book publishBook(String publisherUsername) {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("               Publish new book");
        System.out.println("══════════════════════════════════════════════════");

        System.out.print("Book title: ");
        String title = scanner.nextLine();

        if (title.trim().isEmpty()) {
            System.out.println("Book title cannot be empty!");
            return null;
        }

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("Genre: ");
        String genre = scanner.nextLine();

        int pages = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("How many pages: ");
            String pagesInput = scanner.nextLine();

            try {
                pages = Integer.parseInt(pagesInput);

                if (pages <= 0) {
                    System.out.println("Number of pages must be positive!");
                } else {
                    validInput = true;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Enter a number.");
            }
        }

        BookFactory bookFactory = new BookFactory();
        Book newBook = bookFactory.createBook(title, author, pages, genre);
        newBook.setPublisher(publisherUsername);

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("           Add content to each page");
        System.out.println("(0 to exit)");
        System.out.println("══════════════════════════════════════════════════");

        for (int i = 1; i <= pages; i++) {
            System.out.print("Page " + i + " content: ");
            String pageContent = scanner.nextLine();

            if (pageContent.trim().isEmpty()) {
                pageContent = "No page content yet";
            }
            if(pageContent.equals("0")){
                break;
            }
            newBook.addPage(pageContent);
        }

        return newBook;
    }

    public List<Book> getPublishedBooks(String publisherUsername) {
        return dataManager.getBooksByPublisher(publisherUsername);
    }
}

class ReadingSession {
    private Scanner scanner;
    private AchievementNotifier achievementNotifier;

    public ReadingSession(Scanner scanner, AchievementNotifier achievementNotifier) {
        this.scanner = scanner;
        this.achievementNotifier = achievementNotifier;
    }

    public void readBook(Book book, User user) {
        ReadingManager readingManager = new ReadingManager();
        readingManager.startReading(user, book);
        readBookPages(book, user);
    }

    private void readBookPages(Book book, User user) {
        int currentPage = 1;
        boolean reading = true;

        while (reading && currentPage <= book.getTotalPages()) {
            System.out.println("\n══════════════════════════════════════════════════");
            System.out.println("          " + book.getTitle().toUpperCase());
            System.out.println("          Page " + currentPage + " of " + book.getTotalPages());
            System.out.println("══════════════════════════════════════════════════");

            String page = book.getPage(currentPage);
            if (page == null) {
                page = "[End of book content]";
            }
            System.out.println(page);

            System.out.println("\n══════════════════════════════════════════════════");
            System.out.println("\nn- next page  \np- previous page  \ns-save progress and exit  \nm- go to menu");
            System.out.print("Choice: ");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("n")) {
                if (currentPage < book.getTotalPages()) {
                    currentPage++;
                } else {
                    System.out.println("\nYou finished the book!");
                    user.updateReading();
                    achievementNotifier.checkAchievements(user);
                }
            } else if (input.equals("p")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("You're at the beginning of the book!");
                }
            } else if (input.equals("s")) {
                ReadingManager readingManager = new ReadingManager();
                readingManager.saveProgress(user, book, currentPage);
                user.updateReading();
                achievementNotifier.checkAchievements(user);
                reading = false;
            } else if (input.equals("m")) {
                System.out.println("Returning to menu...");
                reading = false;
            } else {
                System.out.println("Invalid choice! Enter: n, p, s, m");
            }
        }
    }
}



interface AchievementObserver {
    void newAchievement(String achievement);
    void streakUpdates(int streak);
}

class AchievementNotifier {
    private List<AchievementObserver> observers;

    public AchievementNotifier() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(AchievementObserver observer) {
        observers.add(observer);
    }

    public void checkAchievements(User user) {
        if (user.getStreak() == 7) {
            notifyAchievement("\uD83D\uDD25YOU'VE GOT 1 WEEK STREAK\uD83D\uDD25");
        }
        if (user.getStreak() == 30) {
            notifyAchievement("\uD83D\uDD25YOU HAVE 1 MONTH STREAK\uD83D\uDD25");
        }
        if (user.getPersonalLibrary().size() >= 3) {
            notifyAchievement("Book Collector!!!");
        }

        for (AchievementObserver observer : observers) {
            observer.streakUpdates(user.getStreak());
        }
    }

    private void notifyAchievement(String achievement) {
        for (AchievementObserver observer : observers) {
            observer.newAchievement(achievement);
        }
    }
}

class ConsoleAchievementObserver implements AchievementObserver {
    public void newAchievement(String achievement) {
        System.out.println("\nNew achievement: " + achievement + "\nCONGRATULATION!");
    }

    public void streakUpdates(int streak) {
        System.out.println("Current streak: " + streak + " days");

        if (streak == 1) {
            System.out.println("Great start! Keep going!\uD83D\uDD25");
        } else if (streak >= 5 && streak < 7) {
            System.out.println("Almost at weekly achievement!");
        }
    }
}
