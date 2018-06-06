package nio.formatter;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.Locale;

/**
 * Exercise for interview
 * Created: Vadim Voronov
 * Date: 06-Jun-18
 * Email: vadim.v.voronov@gmail.com
 */
public class Employee implements Formattable {
    private String name;
    private int empno;

    public Employee(String name, int empno) {
        this.name = name;
        this.empno = empno;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        StringBuilder sb = new StringBuilder();

        String output = this.name;


        if (formatter.locale().equals(Locale.FRANCE) && name.equals("John Doe")) {
            output = "Jean Dupont";
        }

        output += ": " + empno;

        boolean upperCase = (flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE;
        if (upperCase) {
            output = output.toUpperCase();
        }

        boolean alternate = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        alternate |= (precision >= 0 && precision < 8);  // max len < 8 >> no name

        if (alternate) {
            output = "" + empno;
        }

        if (precision == -1 || output.length() <= precision) {
            sb.append(output);
        } else {
            sb.append(output.substring(0, precision - 1)).append('*'); // cut name
        }

        int len = sb.length();
        if (len < width) {
            for (int i = 0; i < width - len; i++) {
                if ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY) {
                    sb.append(' ');             // add after
                } else {
                    sb.insert(0, ' ');  // add before
                }
            }
        }
        formatter.format(sb.toString());

    }

    @Override
    public String toString() {
        return name + ": " + empno;
    }
}
