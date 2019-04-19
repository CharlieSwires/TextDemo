import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.codehaus.jackson.map.ObjectMapper;

public class TextDemoSync extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JTextField textField2;
	protected JTextArea textArea;
	private static String nodeName = "1";
	private JFrame frame;
	private final static String newline = "\n";
	static MyThread t;
	static TextDemoSync td;

	public TextDemoSync() {
		super(new GridBagLayout());
		textField2 = new JTextField(20);
		textField2.setText("1");
		textField2.addActionListener(this);

		textArea = new JTextArea(5, 20);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);

		//Add Components to this panel.
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;

		c.fill = GridBagConstraints.HORIZONTAL;
		add(textField2, c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(scrollPane, c);
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource()==textField2){
			nodeName = textField2.getText();
			td.frame.setTitle(nodeName);
		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		td = new TextDemoSync();
		//Create and set up the window.
		td.frame = new JFrame("Sync");
		td.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		t = td.new MyThread();
		t.start();
		//Add contents to the window.
		td.frame.add(td);

		//Display the window.
		td.frame.pack();
		td.frame.setVisible(true);
	}

	// HTTP GET request
	private static Tracks sendGet() throws Exception {

		String url = "http://localhost:8080/test-web-service/"+nodeName+"/get";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty( "charset", "utf-8");


		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(con.getInputStream())));

		String output;
		ObjectMapper mapper = new ObjectMapper();
		StringBuffer jsonInString = new StringBuffer();

		while ((output = br.readLine()) != null) {
			jsonInString.append(output+"\r\r\n");
		}
		//JSON from String to Object
		Tracks tracks = mapper.readValue(jsonInString.toString(), Tracks.class);
		br.close();
		//print result
		System.out.println(tracks.toString());
		return tracks;

	}

	class MyThread extends Thread{
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			do {
				Tracks tracks = null;
				try {
					tracks = sendGet();
					System.out.println("sendGet:"+tracks.toString());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(tracks.getTracks() != null){
					for (Track track: tracks.getTracks()){
						textArea.append(track.getText() + newline);
					}
				}
		        textArea.setCaretPosition(textArea.getDocument().getLength());

				try {
					t.sleep(1000);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (true);
		}
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
