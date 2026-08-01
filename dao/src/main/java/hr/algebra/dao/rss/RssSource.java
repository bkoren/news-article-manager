package hr.algebra.dao.rss;

public enum RssSource {
    IMAGE_OF_THE_DAY("Image of the Day", "https://www.nasa.gov/feeds/iotd-feed/"),
    NEWS_RELEASES("News Releases", "https://www.nasa.gov/news-release/feed/"),
    RECENTLY_PUBLISHED_CONTENT("Recently Published Content", "https://www.nasa.gov/feed/"),
    TECHNOLOGY("Technology", "https://www.nasa.gov/technology/feed/"),
    AERONAUTICS("Aeronautics", "https://www.nasa.gov/aeronautics/feed/"),
    SPACE_STATION("Space Station", "https://www.nasa.gov/missions/station/feed/"),
    ARTEMIS("Artemis", "https://www.nasa.gov/missions/artemis/feed/"),
    HOUSTON_WE_HAVE_A_PODCAST("Houston, We Have a Podcast", "https://www.nasa.gov/feeds/podcasts/houston-we-have-a-podcast"),
    NASA_CURIOUS_UNIVERSE("NASA's Curious Universe", "https://www.nasa.gov/feeds/podcasts/curious-universe"),
    SMALL_STEPS_GIANT_LEAPS("Small Steps, Giant Leaps", "https://www.nasa.gov/feeds/podcasts/small-steps-giant-leaps"),
    UNIVERSO_CURIOSO_DE_LA_NASA("Universo curioso de la NASA", "https://www.nasa.gov/feeds/podcasts/universo-curioso-de-la-nasa"),
    AMES_RESEARCH_CENTER("Ames Research Center", "https://www.nasa.gov/centers-and-facilities/ames/feed/"),
    ARMSTRONG_FLIGHT_RESEARCH_CENTER("Armstrong Flight Research Center", "https://www.nasa.gov/centers-and-facilities/armstrong/feed/"),
    GLENN_RESEARCH_CENTER("Glenn Research Center", "https://www.nasa.gov/centers-and-facilities/glenn/feed/"),
    GODDARD_SPACE_FLIGHT_CENTER("Goddard Space Flight Center", "https://www.nasa.gov/centers-and-facilities/goddard/feed/"),
    HEADQUARTERS("Headquarters", "https://www.nasa.gov/centers-and-facilities/hq/feed/"),
    JET_PROPULSION_LABORATORY("Jet Propulsion Laboratory", "https://www.nasa.gov/centers-and-facilities/jpl/feed/"),
    JOHNSON_SPACE_CENTER("Johnson Space Center", "https://www.nasa.gov/centers-and-facilities/johnson/feed/"),
    KENNEDY_SPACE_CENTER("Kennedy Space Center", "https://www.nasa.gov/centers-and-facilities/kennedy/feed/"),
    LANGLEY_RESEARCH_CENTER("Langley Research Center", "https://www.nasa.gov/centers-and-facilities/langley/feed/"),
    MARSHALL_SPACE_FLIGHT_CENTER("Marshall Space Flight Center", "https://www.nasa.gov/centers-and-facilities/marshall/feed/"),
    NASA_SHARED_SERVICES_CENTER("NASA Shared Services Center", "https://www.nasa.gov/centers-and-facilities/nssc/feed/"),
    STENNIS_SPACE_CENTER("Stennis Space Center", "https://www.nasa.gov/centers-and-facilities/stennis/feed/"),
    GODDARD_INSTITUTE_FOR_SPACE_STUDIES("Goddard Institute for Space Studies", "https://www.nasa.gov/centers-and-facilities/giss/feed/"),
    KATHERINE_JOHNSON_IVV_FACILITY("Katherine Johnson Independent Verification & Validation Facility", "https://www.nasa.gov/centers-and-facilities/ivv/feed/"),
    MICHOUD_ASSEMBLY_FACILITY("Michoud Assembly Facility", "https://www.nasa.gov/centers-and-facilities/michoud/feed/"),
    NEIL_ARMSTRONG_TEST_FACILITY("Neil Armstrong Test Facility", "https://www.nasa.gov/centers-and-facilities/armstrong-test-facility/feed/"),
    WALLOPS_FLIGHT_FACILITY("Wallops Flight Facility", "https://www.nasa.gov/centers-and-facilities/wallops/feed/"),
    WHITE_SANDS_TEST_FACILITY("White Sands Test Facility", "https://www.nasa.gov/centers-and-facilities/white-sands/feed/");

    private final String name;
    private final String feedUrl;

    RssSource(String name, String feedUrl) {
        this.name = name;
        this.feedUrl = feedUrl;
    }

    public String getName() { return name; }
    public String getFeedUrl() { return feedUrl; }
}
