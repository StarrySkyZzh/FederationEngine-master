package au.com.d2dcrc.domain;

/**
 * Sample domain object
 */
public class Greeting {

    private String greeting;

    /**
     * Default constructor
     */
    public Greeting() {
        this.greeting = "";
    }

    /**
     * Constructor taking a greeting
     * @param greeting the greeting
     */
    public Greeting(String greeting) {
        this.greeting = greeting;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
