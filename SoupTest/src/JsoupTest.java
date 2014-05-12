import java.io.IOException;

public class JsoupTest extends Utils {

	public static void main(String[] args) throws IOException {

		JsonRequest.getJsonData();
	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}
}
