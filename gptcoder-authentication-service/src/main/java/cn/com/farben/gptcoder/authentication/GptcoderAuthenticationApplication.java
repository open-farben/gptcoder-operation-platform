package cn.com.farben.gptcoder.authentication;

import cn.com.farben.commons.web.listener.ApplicationEnvironmentPreparedListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = { "cn.com.farben.gptcoder.authentication", "cn.com.farben.commons" },
		exclude = { SecurityAutoConfiguration.class })
public class GptcoderAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(GptcoderAuthenticationApplication.class);
		springApplication.addListeners(new ApplicationEnvironmentPreparedListener());
		springApplication.run(args);
	}

}
