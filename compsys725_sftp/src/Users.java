import java.util.ArrayList;

public class Users {
    public ArrayList<String> u = new ArrayList<String>();
    public ArrayList<String> id = new ArrayList<String>();
    public ArrayList<String> pw = new ArrayList<String>();

    public Users() {

        //Adding users
        u.add("admin");
        u.add("fox");
        u.add("rat");

        //Adding accounts / IDs
        id.add("admin");
        id.add("foxy123");
        id.add("helpfulrat56jj");

        //Adding passwords
        pw.add("admin");
        pw.add("1234");
        pw.add("5678");
    }

    public String getUser(int i) {
        return u.get(i);
    }
}
