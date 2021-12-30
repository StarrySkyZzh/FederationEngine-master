public class defaultTests {
    public static void main(String[] args) {
        String s = "XYZ.abc_123$";
        char first = s.charAt(0);
        if (first < 'A' || first > 'Z') {
            s = "VAR_" + s;
        }
        s = s.replaceAll("[^a-zA-Z0-9_]", "_");
        System.out.println(s);
    }
}
