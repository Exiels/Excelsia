package fr.exiel.excelsia.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.launcher.util.UsernameSaver;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener{

	private Image background = Swinger.getResource("launcher.png");
	
	private UsernameSaver saver = new UsernameSaver(Launcher.EX_INFOS);
	
	private JTextField usernameField = new JTextField(saver.getUsername(""));
	private JPasswordField passwordField = new JPasswordField();
	
	private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png"));
	private STexturedButton hideButton = new STexturedButton(Swinger.getResource("hide.png"));
	private STexturedButton quitButton = new STexturedButton(Swinger.getResource("quit.png"));
	
	private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
	private JLabel infoLabel = new JLabel("Clique sur Se Connecter !",  SwingConstants.CENTER);

	public LauncherPanel() {
		this.setBackground(Swinger.TRANSPARENT);
		this.setLayout(null);
		
		usernameField.setForeground(Color.WHITE);
		usernameField.setFont(usernameField.getFont().deriveFont(20F));
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setBounds(130, 330, 238, 26);
		this.add(usernameField);
		
		passwordField.setForeground(Color.WHITE);
		passwordField.setFont(usernameField.getFont());
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setBounds(1006, 330, 238, 26);
		this.add(passwordField);
		
		playButton.setBounds(553, 463);
		playButton.addEventListener(this);
		this.add(playButton);
		
		hideButton.setBounds(1310, 27);
		hideButton.addEventListener(this);
		this.add(hideButton);
		
		quitButton.setBounds(1340, 27);
		quitButton.addEventListener(this);
		this.add(quitButton);
		
		progressBar.setBounds(186, 641, 1003, 45);
		this.add(progressBar);
		
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setFont(usernameField.getFont());
		infoLabel.setBounds(186, 641, 1003, 45);
		this.add(infoLabel);
	}
	
	@Override
	public void onEvent(SwingerEvent e) {
		
		if(e.getSource() == playButton) {
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Erreur, veuillez entrer un pseudo et un mot de passe valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
					Launcher.auth(usernameField.getText(), passwordField.getText());
					} catch (AuthenticationException e) {
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de se connecter : " +e.getErrorModel().getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						return;
					}
					
					try {
						Launcher.update();
						} catch (Exception e) {
							Launcher.interruptThread();
							JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de mettre à jour le jeu : " + e, "Erreur", JOptionPane.ERROR_MESSAGE);
							setFieldsEnabled(true);
							return;
						}
					
					try {
						Launcher.launch();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de lancer le jeu : " + e, "Erreur", JOptionPane.ERROR_MESSAGE);
							setFieldsEnabled(true);
						}
				}
			};
			t.start();
		}else if(e.getSource() == quitButton) {
			System.exit(0);
		}else if(e.getSource() == hideButton) {
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
		}
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Swinger.drawFullsizedImage(graphics, this, background);
	}
	
	private void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar() {
		return progressBar;
	}
	
	public void setInfoText(String text) {
		infoLabel.setText(text);
	}
}
