import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javax.imageio.ImageIO;
import javax.swing.*;
/********************************************************************
 * 
 * @author Bryana Craig
 * @Purpose Creates a whack-a-mole game using shared memory
 *
 ********************************************************************/
public class WhackaMole {
	private final static Color bgColor = new Color(108, 176, 52);
	private static Image moleImage;
	private static Image grassImage;

	private static int timer = 30;
	private static int score;
	private static int width = 3;
	private static int height = 3;
	private static int moleCount;

	private static JButton startButton;
	private JButton[][] moleButtons;
	private JLabel timerLabel, scoreLabel;
	private static JTextField timerTextField;
	private static JTextField scoreTextField;
	private JPanel boardPanel;
	private JPanel infoPanel;
	private ParametersDialog dialog;
	public ButtonListener listener = new ButtonListener();

	private static Random random = new Random(); 
	private static Semaphore sem;
	private static Thread[][] moleThreads;
	private ThreadGroup group;

	private static boolean play = true;

	// Runs the game
	public static void main(String[] args) {
		new WhackaMole();
	}

	// Builds the game GUI
	public WhackaMole() {
		// Ensuring the images are present in the folder
		try{
			moleImage = ImageIO.read(getClass().getResource("./mole.png"));
			grassImage = ImageIO.read(getClass().getResource("./grass.png"));
		}catch(Exception e){
			System.out.println("Image(s) Not Found");
		}

		// Creating the frame and setting size, screen location, layout and close function
		JFrame frame = new JFrame("Whack-a-Mole");
		frame.setSize(1300, 800);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		// Making the panels to hold the board and info screen
		boardPanel = new JPanel(new GridLayout(8,8, 5, 5));
		infoPanel = new JPanel();
		GridBagLayout gbl_infoPanel = new GridBagLayout();
		gbl_infoPanel.columnWidths = new int[] {30, 50, 0, 50, 50};
		gbl_infoPanel.rowHeights = new int[] {100, 100, 100, 0, 100};
		gbl_infoPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_infoPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		infoPanel.setLayout(gbl_infoPanel);
		frame.getContentPane().add(infoPanel,BorderLayout.WEST);
		
		// Adding the 'mole holes' to the board
		addMoleButtons();

		// Creates the start button, places it on the boards and 
		// adds a listener to it
		startButton = new JButton("Start");
		GridBagConstraints gbc_startButton = new GridBagConstraints();
		gbc_startButton.anchor = GridBagConstraints.WEST;
		gbc_startButton.insets = new Insets(0, 0, 5, 5);
		gbc_startButton.gridx = 2;
		gbc_startButton.gridy = 1;
		infoPanel.add(startButton, gbc_startButton);
		startButton.addActionListener(listener);

		// Creates the timer information and adds it to the panel
		timerLabel = new JLabel("Time Left:");
		GridBagConstraints gbc_timerLabel = new GridBagConstraints();
		gbc_timerLabel.anchor = GridBagConstraints.WEST;
		gbc_timerLabel.insets = new Insets(0, 0, 5, 5);
		gbc_timerLabel.gridx = 1;
		gbc_timerLabel.gridy = 2;
		infoPanel.add(timerLabel, gbc_timerLabel);
		timerTextField = new JTextField();
		timerTextField.setEditable(false);
		GridBagConstraints gbc_timerTextField = new GridBagConstraints();
		gbc_timerTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_timerTextField.gridwidth = 2;
		gbc_timerTextField.anchor = GridBagConstraints.WEST;
		gbc_timerTextField.insets = new Insets(0, 0, 5, 0);
		gbc_timerTextField.gridx = 2;
		gbc_timerTextField.gridy = 2;
		infoPanel.add(timerTextField, gbc_timerTextField);

		// Creates the score board information and adds it to the panel
		scoreLabel = new JLabel("Score:");
		GridBagConstraints gbc_scoreLabel = new GridBagConstraints();
		gbc_scoreLabel.anchor = GridBagConstraints.WEST;
		gbc_scoreLabel.insets = new Insets(0, 0, 0, 5);
		gbc_scoreLabel.gridx = 1;
		gbc_scoreLabel.gridy = 3;
		infoPanel.add(scoreLabel, gbc_scoreLabel);
		scoreTextField = new JTextField();
		scoreTextField.setEditable(false);
		GridBagConstraints gbc_scoreArea = new GridBagConstraints();
		gbc_scoreArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_scoreArea.gridwidth = 2;
		gbc_scoreArea.anchor = GridBagConstraints.WEST;
		gbc_scoreArea.gridx = 2;
		gbc_scoreArea.gridy = 3;
		infoPanel.add(scoreTextField, gbc_scoreArea);
		frame.getContentPane().add(boardPanel, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	// Displays the dialog box for the game's parameters
	public void openParameterDialog(){
		dialog = new ParametersDialog();
		dialog.setVisible(true);
		dialog.okButton.addActionListener(listener);
		dialog.cancelButton.addActionListener(listener);
		dialog.useDefaultValuesButton.addActionListener(listener);
	}

	// Adds the "mole hole" buttons to the board
	private void addMoleButtons(){
		// Uses user provided width and height
		moleButtons = new JButton[width][height];
		boardPanel.removeAll();
		boardPanel.setLayout(new GridLayout(width, height, 1, 1));
		for(int i = 0; i<width; i++){
			for(int j = 0; j<height; j++){
				moleButtons[i][j] = new JButton();
				moleButtons[i][j].setBackground(bgColor);
				moleButtons[i][j].setIcon(new ImageIcon(grassImage));
				moleButtons[i][j].setOpaque(true);
				moleButtons[i][j].addActionListener(listener);
				boardPanel.add(moleButtons[i][j]);
			}
		}
		// Reloads the game board
		boardPanel.revalidate();
		boardPanel.repaint();
	}

	// Listener for buttons in main board and the dialog box
	public class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// if user starts a new game
			if (e.getSource() == startButton) {
				startButton.setEnabled(false);
				openParameterDialog();
				Thread waitForUserInput = new Thread(new WaitForParameters());
				waitForUserInput.start();
			}
			// If user correctly hits a mole
			for (int i = 0; i < moleButtons.length; i++) {
				for(int j = 0; j < moleButtons[i].length; j++){
					if (e.getSource() == moleButtons[i][j]) {
						if (moleButtons[i][j].getText().equals("   ")) {
							score++;
							scoreTextField.setText("" + score);
							moleButtons[i][j].setText("");
							moleThreads[i][j].interrupt();
						}
					}
				}
			}
			// If user selects dialog box ok button
			if(e.getSource() == dialog.okButton){
				// Ensure the fields for game parameters have been filled
				if(dialog.widthTextField.getText().isEmpty() 
						|| dialog.heightTextField.getText().isEmpty() 
						|| dialog.molesTextField.getText().isEmpty() 
						|| dialog.timeTextField.getText().isEmpty()){
					JOptionPane.showMessageDialog(dialog, "Values Cannot Be Left Empty");
				}else{
					width = Integer.parseInt(dialog.widthTextField.getText());
					height = Integer.parseInt(dialog.heightTextField.getText());
					moleCount = Integer.parseInt(dialog.molesTextField.getText());
					timer = Integer.parseInt(dialog.timeTextField.getText());
					dialog.dispose();
				}
			}
			// If user cancels game creation
			if(e.getSource() == dialog.cancelButton){
				System.exit(0);
			}
			// If user prefers to use default values, fill in the text fields
			if(e.getSource() == dialog.useDefaultValuesButton){
				dialog.widthTextField.setText("3");
				dialog.heightTextField.setText("3");
				dialog.molesTextField.setText("1");
				dialog.timeTextField.setText("30");
			}
		}
	}

	// Begins the game, adds the mole holes to the board and creates threads for 
	// the moles
	private class TimeThread implements Runnable{
		public void run() {
			WhackaMole.play = true;
			addMoleButtons();
			moleThreads = new Thread[width][height];
			sem = new Semaphore(moleCount);
			for (int i = 0; i < moleThreads.length; i++) {
				for(int j = 0; j<moleThreads[i].length; j++){
					WhackaMole.moleThreads[i][j] = new Thread(group, new MolesThread(moleButtons[i][j]));
					WhackaMole.moleThreads[i][j].start();
				}
			}
			// While the game is still running
			while (timer >= 0) {
				// Count the time down and wait for the game to finish
				try {
					timerTextField.setText(Integer.toString(timer));
					timer--;
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(dialog, "Game Over! \n Your score is: " + score);
			WhackaMole.play = false;
			WhackaMole.timer = 30;
			WhackaMole.timerTextField.setText("" + timer);
			WhackaMole.score = 0;
			WhackaMole.scoreTextField.setText("" + score);
			WhackaMole.startButton.setEnabled(true);
		}
	}

	// Creates a thread for each button and controls the appearance and 
	// disappearance of moles
	private static class MolesThread implements Runnable {
		JButton moleholeButton;

		// Blank mole hole
		MolesThread(JButton button) {
			this.moleholeButton = button;
			button.setText("");
			button.setIcon(new ImageIcon(grassImage));
		}

		// While the game plays
		public void run() {
			while (play) {
				// sleep randomly between 1 and 3 seconds
				int randomTime = (random.nextInt(3000) + 1000) % 3000;
				// Moles pops up
				if(sem.tryAcquire() && timer > -1){
					moleholeButton.setIcon(new ImageIcon(moleImage));
					moleholeButton.setText("   ");
					try {
						// Mole hides for random time
						Thread.sleep(randomTime);
						moleholeButton.setIcon(new ImageIcon(grassImage));
						moleholeButton.setText("");
						sem.release();
					} catch (InterruptedException e) {
						moleholeButton.setIcon(new ImageIcon(grassImage));
						moleholeButton.setText("");
						sem.release();
					}
				}
				try {
					Thread.sleep(randomTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Makes the timer thread wait until user has completed input
	private class WaitForParameters implements Runnable{
		public void run() {
			while(true){
				// if the dialog box is closed, start the timer
				if(!dialog.isVisible()){
					Thread timerThread = new Thread(new TimeThread());
					timerThread.start();
					return;
				}else{
					// else wait and check again
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}