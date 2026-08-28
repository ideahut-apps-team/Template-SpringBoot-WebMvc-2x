package net.ideahut.springboot.template;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.definition.LauncherDefinition;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.launcher.WebLauncher;
import net.ideahut.springboot.launcher.WebMvcLauncher;
import net.ideahut.springboot.template.app.AppProperties;

/*
 * Main Class, untuk eksekusi aplikasi
 */

@Slf4j
@SpringBootApplication
public class Application extends WebMvcLauncher {
	
	/*
	 * PACKAGE
	 */
	public static class Package {
		private Package() {}
		public static final String LIBRARY		= FrameworkHelper.PACKAGE;
		public static final String APPLICATION	= "net.ideahut.springboot.template";
	}
	
	private static boolean ready = false;
	private static void setReady(boolean b) { ready = b; }
	public static boolean isReady() { return ready; }
	
	/*
	 * MAIN
	 */
	public static void main(String... args) {
		WebLauncher.runApp(Application.class, args);
	}
	
	/*
	 * DEFINITION
	 */
	@Override
	public LauncherDefinition onDefinition(ApplicationContext applicationContext) {
		return FrameworkHelper.getBean(applicationContext, AppProperties.class).getLauncher();
	}
	
	/*
	 * READY
	 */
	@Override
	public void onReady(ApplicationContext applicationContext) {
		setReady(true);
	}
	
	/*
	 * ERROR
	 */
	@Override
	public void onError(ApplicationContext applicationContext, Throwable throwable) {
		log.error("Application", throwable);
		System.exit(0);
	}
	
	/*
	 * LOG
	 */
	@Override
	public void onLog(
		LauncherDefinition.Log.Type type, 
		LauncherDefinition.Log.Level level, 
		String message, 
		Throwable throwable
	) {
		level = ObjectHelper.useOrDefault(level, () -> LauncherDefinition.Log.Level.DEBUG);
		switch (level) {
		case TRACE:
			log.trace(message, throwable);
			break;
		case DEBUG:
			log.debug(message, throwable);
			break;
		case INFO:
			log.info(message, throwable);
			break;
		case WARN:
			log.warn(message, throwable);
			break;
		case ERROR:
			log.error(message, throwable);
			break;
		}
	}
	
	/*
	 * SOURCE
	 */
	@Override
	protected Class<? extends WebLauncher> source() {
		return Application.class;
	}
	
}
