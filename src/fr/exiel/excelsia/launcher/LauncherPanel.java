package fr.exiel.excelsia.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener{

	private Image background = Swinger.getResource("launcher.png");
	
	private Saver saver = new Saver(new File(Launcher.EX_FOLDER, "launcher.properties"));
	
	private JTextField usernameField = new JTextField(this.saver.get("username"));
	private JPasswordField passwordField = new JPasswordField();
	
	private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png"));
	private STexturedButton hideButton = new STexturedButton(Swinger.getResource("hide.png"));
	private STexturedButton quitButton = new STexturedButton(Swinger.getResource("quit.png"));
	private STexturedButton ramButton = new STexturedButton(Swinger.getResource("ram.png"));

	private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
	private JLabel infoLabel = new JLabel("Clique sur Se Connecter !",  SwingConstants.CENTER);

	private RamSelector ramSelector = new RamSelector(new File(Launcher.EX_FOLDER, "ram.txt"));
	
	public LauncherPanel() {
		this.setBackground(Swinger.TRANSPARENT);
		this.setLayout(null);
		
		this.usernameField.setForeground(Color.WHITE);
		this.usernameField.setFont(usernameField.getFont().deriveFont(20F));
		this.usernameField.setCaretColor(Color.WHITE);
		this.usernameField.setOpaque(false);
		this.usernameField.setBorder(null);
		this.usernameField.setBounds(130, 330, 238, 26);
		this.add(usernameField);
		
		this.passwordField.setForeground(Color.WHITE);
		this.passwordField.setFont(usernameField.getFont());
		this.passwordField.setCaretColor(Color.WHITE);
		this.passwordField.setOpaque(false);
		this.passwordField.setBorder(null);
		this.passwordField.setBounds(1006, 330, 238, 26);
		this.add(passwordField);
		
		this.playButton.setBounds(553, 463);
		this.playButton.addEventListener(this);
		this.add(playButton);
		
		this.hideButton.setBounds(1310, 27);
		this.hideButton.addEventListener(this);
		this.add(hideButton);
		
		this.quitButton.setBounds(1340, 27);
		this.quitButton.addEventListener(this);
		this.add(quitButton);
		
		this.progressBar.setBounds(186, 641, 1003, 45);
		this.add(progressBar);
		
		this.infoLabel.setForeground(Color.WHITE);
		this.infoLabel.setFont(usernameField.getFont());
		this.infoLabel.setBounds(186, 641, 1003, 45);
		this.add(infoLabel);
		
		this.ramButton.setBounds(14, 28, 63, 25);
		this.ramButton.addEventListener(this);
		this.add(ramButton);
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
						LauncherFrame.getCrashReporter().catchError(e, "Impossible de se connecter !");
						setFieldsEnabled(true);
						return;
					}
					
					saver.set("username", usernameField.getText());
					saver.set("password", passwordField.getText());

					
					try {
						Launcher.update();
						} catch (Exception e) {
							Launcher.interruptThread();
							LauncherFrame.getCrashReporter().catchError(e, "Impossible de mettre a jour le jeu !");
							setFieldsEnabled(true);
							return;
						}
					
					try {
						Launcher.launch();
						} catch (LaunchException e) {
							LauncherFrame.getCrashReporter().catchError(e, "Impossible de lancer le jeu !");
							setFieldsEnabled(true);
						}
				}
			};
			t.start();
		}else if(e.getSource() == quitButton) {
			Animator.fadeOutFrame(LauncherFrame.getInstance(), Animator.FAST, new Runnable() {
				@Override
				public void run() {
					System.exit(0);
				}
			});
		}
		else if(e.getSource() == hideButton) {
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
		}
		else if(e.getSource() == ramButton) {
			ramSelector.display();
		}
		
	}
	
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
	
	public RamSelector getRamSelector() {
		return ramSelector;
	}
}
