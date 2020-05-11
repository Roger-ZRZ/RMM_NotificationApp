import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.BorderUIResource;

public class GUI implements GComponentInterface{

	private JFrame frame;
	private JPanel mainPanel;
	private JLabel textLabel;
	private JTextPane textPane;
	private JButton clearButton;

	private JFrame popFrame;
	private JPanel popPanel;
	private JLabel popLabel;
	private JTextPane popTextpane;


	@Override
	public void init() {
		//frame
		frame = new JFrame("MechMarket Notifier GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//main panel
		mainPanel = new JPanel();
			//panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		mainPanel.setLayout(new BorderLayout());

		//text title
		textLabel = new JLabel("Keyword Monitor",SwingConstants.CENTER);
		textLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 27));
		textLabel.setPreferredSize(new Dimension(1000,75));
		textLabel.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));
		mainPanel.add(textLabel,BorderLayout.NORTH);

		//textpane
		textPane = new JTextPane();
		textPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		textPane.setPreferredSize(new Dimension(1000,500));
		textPane.setText("Initial Text");
		textPane.setEditable(true);
		textPane.setBackground(null);
		textPane.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));

		//button clear
		clearButton = new JButton("Clear");
		clearButton.setBackground(Color.GRAY);
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textPane.setText("");
			}
		});
		mainPanel.add(clearButton,BorderLayout.WEST);

		//scrollbar
		JScrollPane scrollPane = new JScrollPane (textPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(scrollPane,BorderLayout.CENTER);

		//Display the window.
		frame.setLocationRelativeTo(null);
		frame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);


		//popup window
		popFrame = new JFrame("MechMarket Notifier Notification");
		popFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		popPanel = new JPanel();
		popPanel.setLayout(new BorderLayout());

		//text title
		popLabel = new JLabel("title",SwingConstants.CENTER);
		popLabel.setForeground(Color.RED);
		popLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 27));
		popLabel.setPreferredSize(new Dimension(750,50));
		popLabel.setBorder(new BorderUIResource.LineBorderUIResource(Color.RED));
		popPanel.add(popLabel,BorderLayout.NORTH);

		//textpane
		popTextpane = new JTextPane();
		popTextpane.setContentType("text/html");
		popTextpane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		popTextpane.setPreferredSize(new Dimension(750,100));
		popTextpane.setText("link");
		popTextpane.setEditable(false);
		popTextpane.setBackground(null);
		popTextpane.setBorder(new BorderUIResource.LineBorderUIResource(Color.RED));
		popPanel.add(popTextpane);

		popTextpane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
					System.out.println(e.getURL());
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(e.getURL().toURI());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		//popframe build
		popFrame.setLocationRelativeTo(null);
		popFrame.add(popPanel);
		popFrame.pack();
		popFrame.setAlwaysOnTop(true);
		popFrame.setVisible(false);
	}

	@Override
	public String getText() {
		return textPane.getText();
	}

	@Override
	public List<String> getItemList() {
		List<String> out = new ArrayList<>();
		String[] allText = textPane.getText().split("\n");
		if(allText.length==0 || (allText.length==1&&allText[0].equals(""))){
			return null;
		}
		for(int i=0; i<allText.length;i+=2){
			out.add(allText[i]);
		}
		return out;
	}


	@Override
	public boolean appendText(String string) {
		String newText = textPane.getText()+((textPane.getText().length()!=0)?("\n"):(""))+string;
		textPane.setText(newText);
		return true;
	}

	@Override
	public boolean setText(String string) {
		textPane.setText(string);
		return true;
	}

	@Override
	public boolean popupLink(String keyword, String content, String link) {
		popLabel.setText("Keyword Found: "+keyword);
		popTextpane.setText("<a href="+link+"> <h1 style=\"text-align:center;\">"+content+"</h1> </a>");
		popFrame.setVisible(true);
		return true;
	}
}