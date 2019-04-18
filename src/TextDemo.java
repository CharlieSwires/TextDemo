/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


/* TextDemo.java requires no other files. */

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.*;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

public class TextDemo extends JPanel implements ActionListener {
	protected static JTextField textField;
	protected static JTextField textField2;
	protected static JTextArea textArea;
	static String nodeName = "1";
	static JFrame frame;
	private final static String newline = "\n";
	static MyThread t;
	static TextDemo td;

	public TextDemo() {
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
		if (evt.getSource()==textField){
			textField.selectAll();
			try {
				sendPost();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Make sure the new text is visible, even if there
			//was a selection in the text area.
			textArea.setCaretPosition(textArea.getDocument().getLength());
		} else if (evt.getSource()==textField2){
			nodeName = textField2.getText();
			frame.setTitle(nodeName);
		}
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("TextDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add contents to the window.
		frame.add(new TextDemo());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
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

	// HTTP POST request
	private static void sendPost() throws Exception {

		String url = "http://localhost:8080/test-web-service/central/post";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty( "Content-Type", "application/json"); 
		con.setRequestProperty( "charset", "utf-8");

		ObjectMapper mapper = new ObjectMapper();
		//JSON from String to Object
		Track track = new Track();
		track.setDest(nodeName);
		track.setText(textField.getText());
		String outputPayload = mapper.writeValueAsString(track);
		con.setRequestProperty( "Content-Length", Integer.toString( outputPayload.getBytes().length ));

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(outputPayload);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + outputPayload);
		System.out.println("Response Code : " + responseCode);

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
		        textArea.repaint();
				System.out.println("repaint()");

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
				td = new TextDemo();
				t = td.new MyThread();
				t.start();
				createAndShowGUI();
			}
		});
	}
}
