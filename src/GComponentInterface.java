import java.util.List;

public interface GComponentInterface {


	void init();
	String getText();
	List<String> getItemList();
	boolean appendText(String string);
	boolean setText(String string);

	boolean popupLink(String keyword, String content, String link);

}
