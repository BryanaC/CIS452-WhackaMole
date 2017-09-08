import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ParametersDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField widthTextField;
	public JTextField heightTextField;
	public JTextField molesTextField;
	public JTextField timeTextField;
	public JButton okButton;
	public JButton cancelButton;
	public JButton useDefaultValuesButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ParametersDialog dialog = new ParametersDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ParametersDialog() {
		setTitle("Enter Game Parameters");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.WEST);
			panel.setLayout(new BorderLayout(3, 3));
			{
				JLabel lblNewLabel = new JLabel("");
				lblNewLabel.setIcon(new ImageIcon("C:\\Users\\Brie\\workspace\\WhackAMole\\src\\mole.png"));
				panel.add(lblNewLabel);
			}
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JLabel lblNewLabel_7 = new JLabel("");
			contentPanel.add(lblNewLabel_7);
		}
		{
			JLabel lblNewLabel_6 = new JLabel("");
			contentPanel.add(lblNewLabel_6);
		}
		{
			JLabel widthLabel = new JLabel("Board Width:");
			widthLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(widthLabel);
		}
		{
			widthTextField = new JTextField();
			contentPanel.add(widthTextField);
			widthTextField.setColumns(10);
		}
		{
			JLabel heightLabel = new JLabel("Board Height:");
			heightLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(heightLabel);
		}
		{
			heightTextField = new JTextField();
			contentPanel.add(heightTextField);
			heightTextField.setColumns(10);
		}
		{
			JLabel molesLabel = new JLabel("Number of Moles:");
			molesLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(molesLabel);
		}
		{
			molesTextField = new JTextField();
			contentPanel.add(molesTextField);
			molesTextField.setColumns(10);
		}
		{
			JLabel timeLabel = new JLabel("Time:");
			timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(timeLabel);
		}
		{
			timeTextField = new JTextField();
			contentPanel.add(timeTextField);
			timeTextField.setColumns(10);
		}
		{
			JLabel lblNewLabel_4 = new JLabel("");
			contentPanel.add(lblNewLabel_4);
		}
		{
			JLabel lblNewLabel_5 = new JLabel("");
			contentPanel.add(lblNewLabel_5);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				useDefaultValuesButton = new JButton("Use Default Values");
				buttonPane.add(useDefaultValuesButton);
			}
			{
				okButton = new JButton("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
