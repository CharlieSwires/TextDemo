

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.codehaus.jackson.map.ObjectMapper;

public class TextDemoSource extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JTextField textField;
	protected JTextField textField2;
	protected JTextArea textArea;
	protected String nodeName = "1";
	static JFrame frame;
	static TextDemoSource tds = null;
	
	private final static String newline = "\n";

	public TextDemoSource() {
		super(new GridBagLayout());
		textField2 = new JTextField(20);
		textField2.setText("1");
		textField2.addActionListener(this);

		textField = new JTextField(20);
		textField.addActionListener(this);

		textArea = new JTextArea(5, 20);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);

		//Add Components to this panel.
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;

		c.fill = GridBagConstraints.HORIZONTAL;
		add(textField2, c);
		add(textField, c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(scrollPane, c);
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource()==tds.textField){
			tds.textField.selectAll();
			Track track = new Track();
			track.setDest(tds.nodeName);
			track.setText(tds.textField.getText());
			try {
				new Thread() {
					public void run() {
						try {
							sendPost(track);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tds.textField.getText() != null){
				tds.textArea.append(tds.textField2.getText()+": "+tds.textField.getText() + newline);
			}
			//Make sure the new text is visible, even if there
			//was a selection in the text area.
			tds.textArea.setCaretPosition(tds.textArea.getDocument().getLength());
		} else if (evt.getSource()==textField2){
			tds.nodeName = tds.textField2.getText();
			tds.frame.setTitle("Source: "+ nodeName);

		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		tds = new TextDemoSource();

		frame = new JFrame("Source");
		//Create and set up the window.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add contents to the window.
		frame.add(tds);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	// HTTP POST request
	private static void sendPost(Track track2) throws Exception {

		String url = "http://localhost:8080/test-web-service/central/post";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		System.out.println("\nSending 'POST' request to URL : " + url);

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty( "Content-Type", "application/json"); 
		con.setRequestProperty( "charset", "utf-8");
		con.setConnectTimeout(1000);
		ObjectMapper mapper = new ObjectMapper();
		//JSON from String to Object
		String outputPayload = mapper.writeValueAsString(track2);
		con.setRequestProperty( "Content-Length", Integer.toString( outputPayload.getBytes().length ));

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(outputPayload);
		wr.flush();
		wr.close();


		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		
		int responseCode = con.getResponseCode();
		System.out.println("Post parameters : " + outputPayload);
		System.out.println("Response Code : " + responseCode);

	}


	public static void main(String[] args) {
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
