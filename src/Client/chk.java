package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JLabel;

public class chk {

	private JFrame login_frame;
	private JTextField txtEnterNameHere;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					chk window = new chk();
					window.login_frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public chk() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		login_frame = new JFrame();
		login_frame.setTitle("T&O Chat: Login\r\n");
		login_frame.setBounds(100, 100, 522, 188);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.getContentPane().setLayout(null);
		
		txtEnterNameHere = new JTextField();
		txtEnterNameHere.setBackground(Color.ORANGE);
		txtEnterNameHere.setText("    Enter Name Here...");
		txtEnterNameHere.setBounds(15, 66, 200, 50);
		login_frame.getContentPane().add(txtEnterNameHere);
		txtEnterNameHere.setColumns(10);
		
		JLabel label = new JLabel("New label");
		label.setBounds(255, 66, 200, 50);
		login_frame.getContentPane().add(label);
	}
}
