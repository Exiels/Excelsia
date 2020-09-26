package fr.exiel.excelsia.launcher;

import javax.swing.JFrame;

import com.sun.awt.AWTUtilities;

import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.util.WindowMover;

@SuppressWarnings("serial")
public class LauncherFrame  extends JFrame{
	
	private static LauncherFrame instance;
	private LauncherPanel launcherPanel;
	private static CrashReporter crashReporter;
	
	public LauncherFrame() {
		setTitle("Excelsia");
		setSize(1375, 758);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setBackground(Swinger.TRANSPARENT);
		setIconImage(Swinger.getResource("icon.png"));
		setContentPane(this.launcherPanel = new LauncherPanel());
		AWTUtilities.setWindowOpacity(this, 0.0F);
		
		WindowMover mover = new WindowMover(this);
		addMouseListener(mover);
		addMouseMotionListener(mover);
		
		setVisible(true);
		Animator.fadeInFrame(this, Animator.FAST);
	}
	
	public static void main(String[] args) {
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/exiel/excelsia/launcher/resources/");
		Launcher.EX_CRASHES_FOLDER.mkdirs();
		crashReporter = new CrashReporter("Excelsia Launcher", Launcher.EX_CRASHES_FOLDER);
		
		instance = new LauncherFrame();
	}
	
	public static LauncherFrame getInstance() {
		return instance;
	}
	
	public static CrashReporter getCrashReporter() {
		return crashReporter;
	}
	
	public LauncherPanel getLauncherPanel() {
		return this.launcherPanel;
	}

}
