package SiteView.ecc.tools;

public class TextUtils {
	private static String pk = "SiteViewDragonflowSoftware";
	private static String pkpre = "(0x)";

	public static String obscure(String s) {
		if (s.length() == 0) {
			return s;
		}
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			int j = s.charAt(i);
			j += pk.charAt(i % pk.length());
			int k = j / 16;
			int l = j - k * 16;
			stringbuffer.append((char) (65 + k));
			stringbuffer.append((char) (65 + l));
		}

		return pkpre + stringbuffer.toString();
	}
}
