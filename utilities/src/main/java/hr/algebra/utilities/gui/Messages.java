package hr.algebra.utilities.gui;

public final class Messages {

    private Messages() { }

    public static final String LOGIN_FAILED         = "Invalid username or password.";
    public static final String USERNAME_TAKEN       = "That username is already registered.";
    public static final String PASSWORDS_DIFFER     = "The passwords do not match.";
    public static final String PASSWORDS_INVALID    = "Password must be at least 6 characters long.";
    public static final String PASSWORDS_WRONG      = "Wrong password,";

    public static final String REGISTRATION_SUCCESS = "Registration complete. Please sign in.";

    public static final String CONFIRM_DELETE_ALL   =
            "This will delete all articles, authors, categories and their images. Continue?";
    public static final String DELETE_SUCCESS       = "Deleted successfully.";

    public static final String DB_ERROR             = "A database error occurred. Please try again.";
    public static final String FEED_ERROR           = "Could not load the RSS feed.";
}