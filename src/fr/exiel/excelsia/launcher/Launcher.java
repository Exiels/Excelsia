package fr.exiel.excelsia.launcher;

import java.io.File;
import java.util.Arrays;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

public class Launcher {

	public static final GameVersion EX_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
	public static final GameInfos EX_INFOS = new GameInfos("Excelsia", EX_VERSION, new GameTweak[] {GameTweak.FORGE});
	public static final File EX_FOLDER = EX_INFOS.getGameDir();
	public static final File EX_CRASHES_FOLDER = new File(EX_FOLDER, "crashes");
	
	private static AuthInfos authInfos;
	private static Thread updateThread;
		
	public static void auth(String username, String password) throws AuthenticationException {
		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
	}
	
	public static void update() throws Exception{
		SUpdate su = new SUpdate("http://146.59.145.252/launcher/", EX_FOLDER);
		su.getServerRequester().setRewriteEnabled(true);
		su.addApplication(new FileDeleter());
		
		updateThread = new Thread() {
			 private int val;
			 private int max;
			
			@Override
			public void run() {
				while(!isInterrupted()) {
					if(BarAPI.getNumberOfFileToDownload() == 0) {
						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verification des fichiers");
						continue;
					}
					this.val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000L);
					this.max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000L);
					
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(this.max);
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(this.val);
					
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Telechargement des fichiers " +
							BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +
								Swinger.percentage(val, max) + "%");
				}
			}
		};
		updateThread.start();
		su.start();
		updateThread.interrupt();
	}
	
	public static void launch() throws LaunchException {
		
		ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(EX_INFOS, GameFolder.BASIC, authInfos);
		profile.getVmArgs().addAll(Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
		ExternalLauncher launcher = new ExternalLauncher(profile);
		
		LauncherFrame.getInstance().setVisible(false);

		launcher.launch();
		System.exit(0);
	}
	
	public static void interruptThread() {
		updateThread.interrupt();
	}
}
