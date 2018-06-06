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
public class StockName implements Formattable {
    private String symbol;
    private String companyName;
    private String frenchCompanyName;

    public StockName(String symbol, String companyName, String frenchCompanyName) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.frenchCompanyName = frenchCompanyName;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        StringBuilder sb = new StringBuilder();
        String name = companyName;
        if (formatter.locale().equals(Locale.FRANCE)) {
            name = frenchCompanyName;
        }

        boolean alternate = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        boolean usesSymbol = alternate || (precision != -1 && precision < 10);  // max len < 10 >> symbol
        String out = usesSymbol ? symbol : name;

        if (precision == -1 || out.length() < precision) {
            sb.append(out);
        } else {
            sb.append(out.substring(0, precision - 1)).append('*'); // cut name
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
        return String.format("%s -%s", symbol, companyName) ;
    }
}
